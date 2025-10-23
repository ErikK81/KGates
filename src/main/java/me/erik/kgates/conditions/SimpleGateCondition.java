package me.erik.kgates.conditions;

import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class SimpleGateCondition {

    public enum ConditionType { PERMISSION, WEATHER, TIME, HEALTH }

    private final ConditionType type;
    private final String stringValue;
    private final double numericValue;
    private final long endTime;

    // Construtores públicos
    public SimpleGateCondition(ConditionType type, String value) {
        this(type, value, 0, 0);
    }

    public SimpleGateCondition(ConditionType type, double numericValue) {
        this(type, null, numericValue, 0);
    }

    public SimpleGateCondition(long startTime, long endTime) {
        this(ConditionType.TIME, null, startTime, endTime);
    }

    // Construtor privado unificado
    private SimpleGateCondition(ConditionType type, String stringValue, double numericValue, long endTime) {
        this.type = type;
        this.stringValue = stringValue;
        this.numericValue = numericValue;
        this.endTime = endTime;
    }

    public ConditionType getType() {
        return type;
    }

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

                // Intervalo que cruza meia-noite
                yield (start <= end) ? (currentTime >= start && currentTime <= end)
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

    public String getDisplayText() {
        return switch (type) {
            case PERMISSION -> "Permissão necessária: " + stringValue;
            case WEATHER -> "Clima: " + stringValue;
            case HEALTH -> "Vida mínima: " + numericValue;
            case TIME -> "Horário: " + numericValue + " → " + endTime;
        };
    }
}
