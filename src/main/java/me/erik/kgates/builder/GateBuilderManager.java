package me.erik.kgates.builder;

import me.erik.kgates.conditions.SimpleGateCondition;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GateBuilderManager {

    private final Map<UUID, GateBuilderData> activeBuilders = new HashMap<>();
    private final Map<UUID, Map<BuilderState, Boolean>> stateFlags = new HashMap<>();
    private final Map<UUID, SimpleGateCondition.ConditionType> waitingForCondition = new HashMap<>();

    // --- Estados de entrada via chat / bloco ---
    private enum BuilderState {
        NAME,
        BLOCK_CLICK,
        POINT_A,
        DETECTION_RADIUS,
        COOLDOWN
    }

    // --- Controle de construção ---
    public void startBuilding(GateBuilderData builder) {
        UUID playerId = builder.getPlayerId();
        activeBuilders.put(playerId, builder);

        Map<BuilderState, Boolean> flags = new EnumMap<>(BuilderState.class);
        flags.put(BuilderState.NAME, false);
        flags.put(BuilderState.BLOCK_CLICK, false);
        flags.put(BuilderState.POINT_A, true); // padrão: começando com ponto A
        flags.put(BuilderState.DETECTION_RADIUS, false);
        flags.put(BuilderState.COOLDOWN, false);

        stateFlags.put(playerId, flags);
        waitingForCondition.put(playerId, null);
    }

    public void stopBuilding(UUID playerId) {
        activeBuilders.remove(playerId);
        stateFlags.remove(playerId);
        waitingForCondition.remove(playerId);
    }

    public GateBuilderData getBuilder(UUID playerId) {
        return activeBuilders.get(playerId);
    }

    public boolean isBuilding(UUID playerId) {
        return activeBuilders.containsKey(playerId);
    }

    // --- Flags genéricas ---
    private void setFlag(UUID playerId, BuilderState state, boolean value) {
        Map<BuilderState, Boolean> flags = stateFlags.get(playerId);
        if (flags != null) flags.put(state, value);
    }

    private boolean getFlag(UUID playerId, BuilderState state, boolean defaultValue) {
        return stateFlags.getOrDefault(playerId, Map.of()).getOrDefault(state, defaultValue);
    }

    // --- Métodos públicos de flags ---
    public void setWaitingForName(UUID playerId, boolean waiting) { setFlag(playerId, BuilderState.NAME, waiting); }
    public boolean isWaitingForName(UUID playerId) { return getFlag(playerId, BuilderState.NAME, false); }

    public void setWaitingForBlockClick(UUID playerId, boolean waiting) { setFlag(playerId, BuilderState.BLOCK_CLICK, waiting); }
    public boolean isWaitingForBlockClick(UUID playerId) { return getFlag(playerId, BuilderState.BLOCK_CLICK, false); }

    public void setWaitingForPointA(UUID playerId, boolean isPointA) { setFlag(playerId, BuilderState.POINT_A, isPointA); }
    public boolean isWaitingForPointA(UUID playerId) { return getFlag(playerId, BuilderState.POINT_A, true); }

    public void setWaitingForDetectionRadius(UUID playerId, boolean waiting) { setFlag(playerId, BuilderState.DETECTION_RADIUS, waiting); }
    public boolean isWaitingForDetectionRadius(UUID playerId) { return getFlag(playerId, BuilderState.DETECTION_RADIUS, false); }

    public void setWaitingForCooldown(UUID playerId, boolean waiting) { setFlag(playerId, BuilderState.COOLDOWN, waiting); }
    public boolean isWaitingForCooldown(UUID playerId) { return getFlag(playerId, BuilderState.COOLDOWN, false); }

    // --- Controle de condição ---
    public void setWaitingForCondition(UUID playerId, SimpleGateCondition.ConditionType type) {
        if (isBuilding(playerId)) waitingForCondition.put(playerId, type);
    }

    public SimpleGateCondition.ConditionType getWaitingCondition(UUID playerId) {
        return waitingForCondition.get(playerId);
    }

    public void removeWaitingCondition(UUID playerId) {
        waitingForCondition.put(playerId, null);
    }
}
