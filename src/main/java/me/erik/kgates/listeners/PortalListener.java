package me.erik.kgates.listeners;

import me.erik.kgates.commands.GateCommandExecutor;
import me.erik.kgates.conditions.SimpleGateCondition;
import me.erik.kgates.manager.GateData;
import me.erik.kgates.manager.GateManager;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

public record PortalListener(GateManager gateManager) implements Listener {

    private static final Map<GateData, Map<Player, Long>> portalCooldowns = new HashMap<>();

    public void startAmbientEffects(JavaPlugin plugin) {
        plugin.getServer().getScheduler().runTaskTimer(plugin, () -> {
            for (GateData gate : gateManager.getAllGates()) {
                spawnAmbientParticles(gate.getLoc1(), gate);
                spawnAmbientParticles(gate.getLoc2(), gate);
            }
        }, 10L, 10L);
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Location to = event.getTo();
        if (to == null) return;

        long currentTick = System.currentTimeMillis() / 50;

        for (GateData gate : gateManager.getAllGates()) {
            // Verifica cooldown
            Map<Player, Long> cooldowns = portalCooldowns.computeIfAbsent(gate, k -> new HashMap<>());
            long lastUse = cooldowns.getOrDefault(player, -gate.getCooldownTicks());
            if (currentTick - lastUse < gate.getCooldownTicks()) continue;

            // Verifica proximidade e condições
            boolean nearLoc1 = isInsidePortal(to, gate.getLoc1(), gate);
            boolean nearLoc2 = isInsidePortal(to, gate.getLoc2(), gate);

            if (!nearLoc1 && (!nearLoc2 || gate.getType() != GateData.PortalType.TWO_WAY)) continue;

            if (!canActivateGate(player, gate)) continue;

            // Teleporte e efeitos
            boolean teleported = nearLoc1
                    ? teleportPlayer(player, gate.getLoc2(), gate)
                    : teleportPlayer(player, gate.getLoc1(), gate);
            if (!teleported) continue;

            cooldowns.put(player, currentTick);

            // Se ONE_WAY, não verifica mais
            if (gate.getType() == GateData.PortalType.ONE_WAY) break;
        }
    }

    private boolean canActivateGate(Player player, GateData gate) {
        for (SimpleGateCondition condition : gate.getConditions()) {
            if (!condition.canActivate(player)) return false;
        }
        return true;
    }

    private boolean isInsidePortal(Location loc, Location blockLoc, GateData gate) {
        if (blockLoc == null || blockLoc.getWorld() == null) return false;
        if (!Objects.equals(loc.getWorld(), blockLoc.getWorld())) return false;
        double dx = loc.getX() - (blockLoc.getBlockX() + 0.5);
        double dy = loc.getY() - (blockLoc.getBlockY() + 1.0);
        double dz = loc.getZ() - (blockLoc.getBlockZ() + 0.5);
        double halfX = gate.getSizeX() / 2.0;
        double halfY = gate.getSizeY() / 2.0;
        double halfZ = gate.getSizeZ() / 2.0;
        return switch (gate.getShape()) {
            case SPHERE -> dx * dx + dy * dy + dz * dz <= halfX * halfX;
            case CYLINDER -> dx * dx + dz * dz <= halfX * halfX && Math.abs(dy) <= halfY;
            case RECTANGLE -> Math.abs(dx) <= halfX && Math.abs(dy) <= halfY && Math.abs(dz) <= halfZ;
        };
    }

    private boolean teleportPlayer(Player player, Location target, GateData gate) {
        if (target == null || target.getWorld() == null) return false;
        spawnParticles(player.getLocation(), gate.getEntryParticle(),
                gate.getEntryParticleCount(), gate.getEntryParticleSpeed());
        Location tp = target.clone().add(0.5, 1.0, 0.5);
        tp.setYaw(player.getLocation().getYaw());
        tp.setPitch(player.getLocation().getPitch());
        player.teleport(tp);
        spawnParticles(tp, gate.getExitParticle(),
                gate.getExitParticleCount(), gate.getExitParticleSpeed());
        playActivationSound(player, gate);

        if (gate.getCommands() != null) {
            for (String cmd : gate.getCommands()) {
                GateCommandExecutor.execute(player, cmd);
            }
        }
        return true;
    }

    private static void spawnParticles(Location location, Particle particle, int count, double speed) {
        if (location == null || location.getWorld() == null || particle == null || count <= 0) return;
        try {
            location.getWorld().spawnParticle(particle, location.clone().add(0.5, 1.0, 0.5),
                    count, 0.35, 0.6, 0.35, speed);
        } catch (IllegalArgumentException ignored) {
            // Some particles require extra block/item/color data and cannot use this generic renderer.
        }
    }

    /** Distributes ambient particles throughout the portal's detection volume. */
    private static void spawnAmbientParticles(Location center, GateData gate) {
        Particle particle = gate.getAmbientParticle();
        int count = gate.getAmbientParticleCount();
        double speed = gate.getAmbientParticleSpeed();
        if (center == null || center.getWorld() == null || particle == null || count <= 0) return;
        ThreadLocalRandom random = ThreadLocalRandom.current();

        for (int i = 0; i < count; i++) {
            double[] offset = randomOffset(gate, random);

            Location position = center.clone().add(0.5 + offset[0], 1.0 + offset[1], 0.5 + offset[2]);
            try {
                center.getWorld().spawnParticle(particle, position, 1,
                        0.08, 0.08, 0.08, speed);
            } catch (IllegalArgumentException ignored) {
                // Particles requiring extra data are not supported by the generic editor.
                return;
            }
        }
    }

    private static double[] randomOffset(GateData gate, ThreadLocalRandom random) {
        double halfX = gate.getSizeX() / 2.0;
        double halfY = gate.getSizeY() / 2.0;
        double halfZ = gate.getSizeZ() / 2.0;
        if (gate.getShape() == GateData.PortalShape.RECTANGLE) {
            return new double[]{random.nextDouble(-halfX, halfX), random.nextDouble(-halfY, halfY),
                    random.nextDouble(-halfZ, halfZ)};
        }
        if (gate.getShape() == GateData.PortalShape.CYLINDER) {
            double angle = random.nextDouble(Math.PI * 2.0);
            double radius = Math.sqrt(random.nextDouble()) * halfX;
            return new double[]{Math.cos(angle) * radius, random.nextDouble(-halfY, halfY), Math.sin(angle) * radius};
        }
        double x, y, z;
        do {
            x = random.nextDouble(-halfX, halfX);
            y = random.nextDouble(-halfX, halfX);
            z = random.nextDouble(-halfX, halfX);
        } while (x * x + y * y + z * z > halfX * halfX);
        return new double[]{x, y, z};
    }

    private void playActivationSound(Player player, GateData gate) {
        Sound sound = gate.getActivationSound();
        if (sound != null) {
            player.getWorld().playSound(
                    player.getLocation(),
                    sound,
                    gate.getActivationSoundVolume(),
                    gate.getActivationSoundPitch()
            );
        }
    }
}
