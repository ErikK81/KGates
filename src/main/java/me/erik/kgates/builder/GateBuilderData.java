package me.erik.kgates.builder;

import me.erik.kgates.conditions.SimpleGateCondition;
import me.erik.kgates.conditions.ConditionGUI;
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

    private double detectionRadius = 1.5; // padrão
    private long cooldownTicks = 20;      // padrão: 1 segundo

    // Flags de entrada via chat
    private boolean awaitingRadius = false;
    private boolean awaitingCooldown = false;

    // Condições do portal
    private final List<SimpleGateCondition> conditions = new ArrayList<>();
    private SimpleGateCondition.ConditionType waitingConditionType;

    public GateBuilderData(UUID playerId, String id) {
        this.playerId = playerId;
        this.id = id;
    }

    // --- Getters básicos ---
    public UUID getPlayerId() { return playerId; }
    public String getId() { return id; }
    public String getName() { return name != null ? name : id; }
    public String getType() { return type; }
    public Location getLocA() { return locA; }
    public Location getLocB() { return locB; }
    public double getDetectionRadius() { return detectionRadius; }
    public long getCooldownTicks() { return cooldownTicks; }
    public boolean isAwaitingRadius() { return awaitingRadius; }
    public boolean isAwaitingCooldown() { return awaitingCooldown; }
    public SimpleGateCondition.ConditionType getWaitingConditionType() { return waitingConditionType; }
    public List<SimpleGateCondition> getConditions() { return Collections.unmodifiableList(conditions); }

    // --- Setters básicos ---
    public void setName(String name) { this.name = name; }
    public void setType(String type) { this.type = type; }
    public void setLocA(Location locA) { this.locA = locA; }
    public void setLocB(Location locB) { this.locB = locB; }
    public void setDetectionRadius(double detectionRadius) { this.detectionRadius = detectionRadius; }
    public void setCooldownTicks(long cooldownTicks) { this.cooldownTicks = cooldownTicks; }
    public void setAwaitingRadius(boolean awaitingRadius) { this.awaitingRadius = awaitingRadius; }
    public void setAwaitingCooldown(boolean awaitingCooldown) { this.awaitingCooldown = awaitingCooldown; }
    public void setWaitingConditionType(SimpleGateCondition.ConditionType type) { this.waitingConditionType = type; }

    // --- Manipulação de condições ---
    public void addCondition(SimpleGateCondition condition) {
        if (condition != null) conditions.add(condition);
    }

    public void removeCondition(SimpleGateCondition.ConditionType type) {
        conditions.removeIf(c -> c.getType() == type);
    }

    public boolean hasCondition(SimpleGateCondition.ConditionType type) {
        return conditions.stream().anyMatch(c -> c.getType() == type);
    }

    // --- Status do portal ---
    public boolean isComplete() {
        return locA != null && locB != null && type != null && !type.isEmpty();
    }

    // --- Interação via chat ---
    public void startConditionInput(SimpleGateCondition.ConditionType type, Player player) {
        this.waitingConditionType = type;
        player.closeInventory();
        player.sendMessage(ChatColor.AQUA + "Digite o valor para a condição "
                + ChatColor.YELLOW + type.name()
                + ChatColor.AQUA + " no chat, ou digite "
                + ChatColor.RED + "'cancelar'" + ChatColor.AQUA + " para abortar.");
    }

    // --- GUI ---
    public void openConditionGUI(Player player) {
        new ConditionGUI(this).open(player);
    }
}
