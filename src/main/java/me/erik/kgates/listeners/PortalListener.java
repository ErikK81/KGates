package me.erik.kgates.listeners;

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

    // Portal → (Player → lastTeleportTick)
    private static final Map<GateData, Map<Player, Long>> portalCooldowns = new HashMap<>();

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Location to = event.getTo();
        if (to == null) return;

        long currentTick = System.currentTimeMillis() / 50;

        for (GateData gate : gateManager.getAllGates()) {
            if (!gate.canActivate(player)) continue;
            if (!playerMeetsConditions(player, gate)) continue;

            Map<Player, Long> cooldowns = portalCooldowns.computeIfAbsent(gate, k -> new HashMap<>());
            long lastTeleport = cooldowns.getOrDefault(player, -gate.getCooldownTicks());
            if (currentTick - lastTeleport < gate.getCooldownTicks()) continue;

            // Teleporta se próximo de loc1 ou loc2
            if (tryTeleport(player, to, gate.getLoc2(), cooldowns, currentTick)) break;
            if (tryTeleport(player, to, gate.getLoc1(), cooldowns, currentTick)) break;
        }
    }

    private boolean playerMeetsConditions(Player player, GateData gate) {
        if (gate.getConditions() == null || gate.getConditions().isEmpty()) return true;
        return gate.getConditions().stream().allMatch(cond -> cond.canActivate(player));
    }

    private boolean tryTeleport(Player player, Location from, Location target, Map<Player, Long> cooldowns, long currentTick) {
        double radius = 1.5; // fallback se necessário
        if (isNearCenter(from, target, radius)) {
            teleportPlayer(player, target);
            cooldowns.put(player, currentTick);
            return true;
        }
        return false;
    }

    private boolean isNearCenter(Location loc, Location blockLoc, double radius) {
        if (!Objects.equals(loc.getWorld(), blockLoc.getWorld())) return false;

        double dx = loc.getX() - (blockLoc.getBlockX() + 0.5);
        double dy = loc.getY() - (blockLoc.getBlockY() + 0.5);
        double dz = loc.getZ() - (blockLoc.getBlockZ() + 0.5);

        return dx * dx + dy * dy + dz * dz <= radius * radius;
    }

    private void teleportPlayer(Player player, Location target) {
        Location tpLocation = target.clone().add(0.5, 1.0, 0.5);
        tpLocation.setYaw(player.getLocation().getYaw());
        tpLocation.setPitch(player.getLocation().getPitch());

        player.teleport(tpLocation);

        // Efeitos visuais e sonoros
        player.getWorld().playSound(tpLocation, Sound.ENTITY_ENDERMAN_TELEPORT, 1f, 1f);
        player.getWorld().spawnParticle(Particle.PORTAL, tpLocation, 60, 1, 1, 1, 0.1);
    }
}
