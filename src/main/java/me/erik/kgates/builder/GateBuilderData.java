package me.erik.kgates.builder;

import me.erik.kgates.conditions.SimpleGateCondition;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class GateBuilderData {

    private final UUID playerId;
    private final String id;
    private String name;
    private String type = "default";
    private Location locA;
    private Location locB;
    private double detectionRadius = 1.5;
    private long cooldownTicks = 20;

    private boolean awaitingRadius = false;
    private boolean awaitingCooldown = false;
    private final List<SimpleGateCondition> conditions = new ArrayList<>();
    private SimpleGateCondition.ConditionType waitingConditionType;

    public GateBuilderData(UUID playerId, String id) {
        this.playerId = playerId;
        this.id = id;
    }

    public UUID getPlayerId() { return playerId; }
    public String getId() { return id; }
    public String getName() { return name != null ? name : id; }
    public void setName(String name) { this.name = name; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public Location getLocA() { return locA; }
    public void setLocA(Location locA) { this.locA = locA; }
    public Location getLocB() { return locB; }
    public void setLocB(Location locB) { this.locB = locB; }
    public double getDetectionRadius() { return detectionRadius; }
    public void setDetectionRadius(double detectionRadius) { this.detectionRadius = detectionRadius; }
    public long getCooldownTicks() { return cooldownTicks; }
    public void setCooldownTicks(long cooldownTicks) { this.cooldownTicks = cooldownTicks; }
    public boolean isAwaitingRadius() { return awaitingRadius; }
    public void setAwaitingRadius(boolean awaitingRadius) { this.awaitingRadius = awaitingRadius; }
    public boolean isAwaitingCooldown() { return awaitingCooldown; }
    public void setAwaitingCooldown(boolean awaitingCooldown) { this.awaitingCooldown = awaitingCooldown; }

    public List<SimpleGateCondition> getConditions() {
        return Collections.unmodifiableList(conditions);
    }
    public void addCondition(SimpleGateCondition condition) {
        if (condition == null) return;
        conditions.removeIf(c -> c.getType() == condition.getType());
        conditions.add(condition);
    }

    public void removeCondition(SimpleGateCondition.ConditionType type) {
        conditions.removeIf(c -> c.getType() == type);
    }

    public SimpleGateCondition.ConditionType getWaitingConditionType() { return waitingConditionType; }
    public void setWaitingConditionType(SimpleGateCondition.ConditionType waitingConditionType) { this.waitingConditionType = waitingConditionType; }

    public void startConditionInput(SimpleGateCondition.ConditionType type, Player player) {
        this.waitingConditionType = type;
        player.closeInventory();
        player.sendMessage(ChatColor.AQUA + "Digite o valor para a condição "
                + ChatColor.YELLOW + type.name()
                + ChatColor.AQUA + " no chat, ou digite "
                + ChatColor.RED + "'cancelar'" + ChatColor.AQUA + " para abortar.");
    }

    public boolean isComplete() {
        return locA != null && locB != null && type != null && !type.isEmpty();
    }
}
