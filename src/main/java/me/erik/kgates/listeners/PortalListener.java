package me.erik.kgates.listeners;

import me.erik.kgates.commands.GateCommandExecutor;
import me.erik.kgates.manager.GateData;
import me.erik.kgates.manager.GateManager;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public record PortalListener(GateManager gateManager) implements Listener {

    private static final Map<GateData, Map<Player, Long>> portalCooldowns = new HashMap<>();

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Location to = event.getTo();
        if (to == null) return;

        long currentTick = System.currentTimeMillis() / 50;

        for (GateData gate : gateManager.getAllGates()) {
            if (!gate.canActivate(player)) continue;

            double radius = gate.getDetectionRadius();
            long cooldownTicks = gate.getCooldownTicks();
            Map<Player, Long> cooldowns = portalCooldowns.computeIfAbsent(gate, k -> new HashMap<>());
            long lastTeleport = cooldowns.getOrDefault(player, -cooldownTicks);

            // efeitos ambientais (sempre no portal)
            playAmbientEffects(gate);

            if (currentTick - lastTeleport < cooldownTicks) continue;

            if (isNearCenter(to, gate.getLoc1(), radius)) {
                teleportPlayer(player, gate.getLoc2(), gate);
                cooldowns.put(player, currentTick);
                playActivationEffects(player, gate);
                if (gate.getType() == GateData.PortalType.ONE_WAY) break;
            } else if (gate.getType() == GateData.PortalType.TWO_WAY && isNearCenter(to, gate.getLoc2(), radius)) {
                teleportPlayer(player, gate.getLoc1(), gate);
                cooldowns.put(player, currentTick);
                playActivationEffects(player, gate);
            }
        }
    }

    private boolean isNearCenter(Location loc, Location blockLoc, double radius) {
        if (!Objects.equals(loc.getWorld(), blockLoc.getWorld())) return false;
        double dx = loc.getX() - (blockLoc.getBlockX() + 0.5);
        double dy = loc.getY() - (blockLoc.getBlockY() + 0.5);
        double dz = loc.getZ() - (blockLoc.getBlockZ() + 0.5);
        return dx * dx + dy * dy + dz * dz <= radius * radius;
    }

    private void teleportPlayer(Player player, Location target, GateData gate) {
        Location tp = target.clone().add(0.5, 1.0, 0.5);
        tp.setYaw(player.getLocation().getYaw());
        tp.setPitch(player.getLocation().getPitch());
        player.teleport(tp);

        if (gate.getCommands() != null && !gate.getCommands().isEmpty()) {
            for (String cmd : gate.getCommands()) {
                GateCommandExecutor.execute(player, cmd);
            }
        }
    }

    // -------------------- Ambient Effects --------------------
    private void playAmbientEffects(GateData gate) {
        Particle particle = gate.getAmbientParticle();
        Sound sound = gate.getAmbientSound();

        if (particle != null) {
            for (Location loc : new Location[]{gate.getLoc1(), gate.getLoc2()}) {
                Objects.requireNonNull(loc.getWorld()).spawnParticle(
                        particle,
                        loc,
                        gate.getAmbientParticleCount(),
                        gate.getAmbientParticleSpeed(),
                        gate.getAmbientParticleSpeed(),
                        gate.getAmbientParticleSpeed()
                );
            }
        }

        if (sound != null) {
            for (Location loc : new Location[]{gate.getLoc1(), gate.getLoc2()}) {
                Objects.requireNonNull(loc.getWorld()).playSound(
                        loc,
                        sound,
                        gate.getAmbientSoundVolume(),
                        gate.getAmbientSoundPitch()
                );
            }
        }
    }

    // -------------------- Activation Effects --------------------
    private void playActivationEffects(Player player, GateData gate) {
        Particle particle = gate.getActivationParticle();
        Sound sound = gate.getActivationSound();

        if (particle != null) {
            player.getWorld().spawnParticle(
                    particle,
                    player.getLocation(),
                    gate.getActivationParticleCount(),
                    gate.getActivationParticleSpeed(),
                    gate.getActivationParticleSpeed(),
                    gate.getActivationParticleSpeed()
            );
        }

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
