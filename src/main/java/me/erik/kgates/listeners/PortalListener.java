package me.erik.kgates.listeners;

import me.erik.kgates.conditions.SimpleGateCondition;
import me.erik.kgates.manager.GateData;
import me.erik.kgates.manager.GateManager;
import org.bukkit.Location;
import org.bukkit.Particle;
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

            boolean allConditionsMet = gate.getConditions().stream().allMatch(c -> c.canActivate(player));
            if (!allConditionsMet) continue;

            double radius = gate.getDetectionRadius();
            long cooldownTicks = gate.getCooldownTicks();
            Map<Player, Long> cooldowns = portalCooldowns.computeIfAbsent(gate, k -> new HashMap<>());
            long lastTeleport = cooldowns.getOrDefault(player, -cooldownTicks);

            if (currentTick - lastTeleport < cooldownTicks) continue;

            if (isNearCenter(to, gate.getLoc1(), radius)) {
                teleportPlayer(player, gate.getLoc2());
                cooldowns.put(player, currentTick);
                break;
            } else if (isNearCenter(to, gate.getLoc2(), radius)) {
                teleportPlayer(player, gate.getLoc1());
                cooldowns.put(player, currentTick);
                break;
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

    private void teleportPlayer(Player player, Location target) {
        Location tp = target.clone().add(0.5, 1.0, 0.5);
        tp.setYaw(player.getLocation().getYaw());
        tp.setPitch(player.getLocation().getPitch());

        player.teleport(tp);
        player.getWorld().playSound(tp, "minecraft:entity.enderman.teleport", 1f, 1f);
        player.getWorld().spawnParticle(Particle.PORTAL, tp, 60, 1, 1, 1, 0.1);
    }
}
