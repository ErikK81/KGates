package me.erik.kgates.manager;

import me.erik.kgates.conditions.SimpleGateCondition;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;

public class GateConditionLoader {

    public static List<SimpleGateCondition> loadConditions(ConfigurationSection section) {
        List<SimpleGateCondition> conditions = new ArrayList<>();
        if (section == null) return conditions;

        for (String key : section.getKeys(false)) {
            ConfigurationSection condSection = section.getConfigurationSection(key);
            if (condSection == null) continue;

            String typeStr = condSection.getString("type", "PERMISSION").toUpperCase();
            SimpleGateCondition.ConditionType type;
            try {
                type = SimpleGateCondition.ConditionType.valueOf(typeStr);
            } catch (IllegalArgumentException e) {
                continue; // Ignora tipo inválido
            }

            switch (type) {
                case PERMISSION, WEATHER -> {
                    String value = condSection.getString("value", "");
                    if (!value.isBlank()) {
                        conditions.add(new SimpleGateCondition(type, value));
                    }
                }
                case HEALTH -> {
                    double health = condSection.getDouble("value", 0.0);
                    conditions.add(new SimpleGateCondition(type, health));
                }
                case TIME -> {
                    long start = condSection.getLong("start", 0);
                    long end = condSection.getLong("end", 0);
                    conditions.add(new SimpleGateCondition(start, end));
                }
            }
        }

        return conditions;
    }
}
