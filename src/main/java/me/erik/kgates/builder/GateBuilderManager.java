package me.erik.kgates.builder;

import java.util.*;

public class GateBuilderManager {

    private final Map<UUID, GateBuilderData> activeBuilders = new HashMap<>();
    private final Map<UUID, Boolean> waitingForName = new HashMap<>();
    private final Map<UUID, Boolean> waitingForBlockClick = new HashMap<>();
    private final Map<UUID, Boolean> waitingForPointA = new HashMap<>();
    private final Map<UUID, Boolean> waitingForDetectionRadius = new HashMap<>();
    private final Map<UUID, Boolean> waitingForCooldown = new HashMap<>();
    private final Map<UUID, Boolean> waitingForConditionInput = new HashMap<>();

    private final Set<String> gatesBeingEdited = new HashSet<>();

    // ---------------------------------------------------
    // Builder lifecycle
    // ---------------------------------------------------

    public void startBuilding(GateBuilderData builder) {
        UUID id = builder.getPlayerId();
        activeBuilders.put(id, builder);
        waitingForName.put(id, false);
        waitingForBlockClick.put(id, false);
        waitingForPointA.put(id, true);
        waitingForDetectionRadius.put(id, false);
        waitingForCooldown.put(id, false);
        waitingForConditionInput.put(id, false);
    }

    public void stopBuilding(UUID playerId) {
        activeBuilders.remove(playerId);
        waitingForName.remove(playerId);
        waitingForBlockClick.remove(playerId);
        waitingForPointA.remove(playerId);
        waitingForDetectionRadius.remove(playerId);
        waitingForCooldown.remove(playerId);
        waitingForConditionInput.remove(playerId);
    }

    // ---------------------------------------------------
    // Editing lock
    // ---------------------------------------------------

    public boolean startEditing(String gateId) {
        return gatesBeingEdited.add(gateId); // true se não estava sendo editado
    }

    public void stopEditing(String gateId) {
        gatesBeingEdited.remove(gateId);
    }

    // ---------------------------------------------------
    // Accessors
    // ---------------------------------------------------

    public GateBuilderData getBuilder(UUID playerId) { return activeBuilders.get(playerId); }
    public boolean isBuilding(UUID playerId) { return activeBuilders.containsKey(playerId); }

    public void setWaitingForName(UUID playerId, boolean waiting) {
        if (isBuilding(playerId)) waitingForName.put(playerId, waiting);
    }
    public boolean isWaitingForName(UUID playerId) { return waitingForName.getOrDefault(playerId, false); }

    public void setWaitingForBlockClick(UUID playerId, boolean waiting) {
        if (isBuilding(playerId)) waitingForBlockClick.put(playerId, waiting);
    }
    public boolean isWaitingForBlockClick(UUID playerId) { return waitingForBlockClick.getOrDefault(playerId, false); }

    public void setWaitingForPointA(UUID playerId, boolean isPointA) {
        if (isBuilding(playerId)) waitingForPointA.put(playerId, isPointA);
    }
    public boolean isWaitingForPointA(UUID playerId) { return waitingForPointA.getOrDefault(playerId, true); }

    public void setWaitingForDetectionRadius(UUID playerId, boolean waiting) {
        if (isBuilding(playerId)) waitingForDetectionRadius.put(playerId, waiting);
    }
    public boolean isWaitingForDetectionRadius(UUID playerId) { return waitingForDetectionRadius.getOrDefault(playerId, false); }

    public void setWaitingForCooldown(UUID playerId, boolean waiting) {
        if (isBuilding(playerId)) waitingForCooldown.put(playerId, waiting);
    }
    public boolean isWaitingForCooldown(UUID playerId) { return waitingForCooldown.getOrDefault(playerId, false); }

    // ---------------------------------------------------
    // New dynamic condition system
    // ---------------------------------------------------

    public void setWaitingForConditionInput(UUID playerId, boolean waiting) {
        if (isBuilding(playerId)) waitingForConditionInput.put(playerId, waiting);
    }

    public boolean isWaitingForConditionInput(UUID playerId) {
        return waitingForConditionInput.getOrDefault(playerId, false);
    }
}
