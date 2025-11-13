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
            // Verifica cooldown
            Map<Player, Long> cooldowns = portalCooldowns.computeIfAbsent(gate, k -> new HashMap<>());
            long lastUse = cooldowns.getOrDefault(player, -gate.getCooldownTicks());
            if (currentTick - lastUse < gate.getCooldownTicks()) continue;

            // Verifica proximidade e condições
            boolean nearLoc1 = isNearCenter(to, gate.getLoc1(), gate.getDetectionRadius());
            boolean nearLoc2 = isNearCenter(to, gate.getLoc2(), gate.getDetectionRadius());

            if (!nearLoc1 && (!nearLoc2 || gate.getType() != GateData.PortalType.TWO_WAY)) continue;

            if (!canActivateGate(player, gate)) continue;

            // Teleporte e efeitos
            if (nearLoc1) {
                teleportPlayer(player, gate.getLoc2(), gate);
            } else {
                teleportPlayer(player, gate.getLoc1(), gate);
            }

            cooldowns.put(player, currentTick);
            playActivationEffects(player, gate);

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

        if (gate.getCommands() != null) {
            for (String cmd : gate.getCommands()) {
                GateCommandExecutor.execute(player, cmd);
            }
        }
    }

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
