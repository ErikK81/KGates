package me.erik.kgates.conditions;

import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class SimpleGateCondition {

    public enum ConditionType { PERMISSION, WEATHER, TIME, HEALTH }

    private final ConditionType type;
    private String stringValue;
    private double numericValue;
    private long endTime;

    public SimpleGateCondition(ConditionType type, String value) {
        this.type = type;
        this.stringValue = value;
    }

    public SimpleGateCondition(ConditionType type, double numericValue) {
        this.type = type;
        this.numericValue = numericValue;
    }

    public SimpleGateCondition(long startTime, long endTime) {
        this.type = ConditionType.TIME;
        this.numericValue = startTime;
        this.endTime = endTime;
    }

    public ConditionType getType() { return type; }

    public boolean canActivate(Player player) {
        World world = player.getWorld();

        return switch (type) {
            case PERMISSION -> player.hasPermission(stringValue);
            case WEATHER -> {
                String weather = stringValue == null ? "" : stringValue.toUpperCase();
                boolean raining = world.hasStorm();
                boolean thunder = world.isThundering();
                yield switch (weather) {
                    case "SUN", "CLEAR" -> !raining && !thunder;
                    case "RAIN" -> raining && !thunder;
                    case "STORM", "THUNDER" -> thunder;
                    default -> true;
                };
            }
            case HEALTH -> player.getHealth() >= numericValue;
            case TIME -> {
                long currentTime = world.getTime();
                long start = (long) numericValue;
                long end = endTime;
                yield (start <= end)
                        ? (currentTime >= start && currentTime <= end)
                        : (currentTime >= start || currentTime <= end);
            }
        };
    }

    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        map.put("type", type.name());

        switch (type) {
            case PERMISSION, WEATHER -> map.put("value", stringValue);
            case HEALTH -> map.put("value", numericValue);
            case TIME -> {
                map.put("start", numericValue);
                map.put("end", endTime);
            }
        }

        return map;
    }

    public static SimpleGateCondition deserialize(Map<String, Object> map) {
        String typeStr = (String) map.get("type");
        if (typeStr == null) return null;
        ConditionType type = ConditionType.valueOf(typeStr);

        return switch (type) {
            case PERMISSION, WEATHER -> new SimpleGateCondition(type, (String) map.get("value"));
            case HEALTH -> new SimpleGateCondition(type, ((Number) map.get("value")).doubleValue());
            case TIME -> new SimpleGateCondition(
                    ((Number) map.get("start")).longValue(),
                    ((Number) map.get("end")).longValue()
            );
        };
    }

    public String getDisplayText() {
        return switch (type) {
            case PERMISSION -> "Permission needed: " + stringValue;
            case WEATHER -> "Weather: " + stringValue;
            case HEALTH -> "Minimum health: " + numericValue;
            case TIME -> "Time: " + numericValue + " → " + endTime;
        };
    }
}
