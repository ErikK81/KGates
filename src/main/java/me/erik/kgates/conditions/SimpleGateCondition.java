package me.erik.kgates.conditions;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public record SimpleGateCondition(String expression) {

    public boolean canActivate(Player player) {
        if (expression == null || expression.isEmpty()) return true;

        // Substitui placeholders (ex: %player_health%)
        String parsed = PlaceholderAPI.setPlaceholders(player, expression);

        try {
            return evaluateExpression(parsed);
        } catch (Exception e) {
            System.out.println("[KGates] Erro ao avaliar condição: " + parsed);
            e.printStackTrace();
            return false;
        }
    }

    public String getExpression() {
        return expression;
    }

    private boolean evaluateExpression(String expr) {
        expr = expr.trim().toLowerCase();

        try {
            // Suporte básico para operadores
            if (expr.contains(">=")) {
                String[] parts = expr.split(">=");
                return parts.length >= 2 && Double.parseDouble(parts[0].trim()) >= Double.parseDouble(parts[1].trim());
            } else if (expr.contains("<=")) {
                String[] parts = expr.split("<=");
                return parts.length >= 2 && Double.parseDouble(parts[0].trim()) <= Double.parseDouble(parts[1].trim());
            } else if (expr.contains("!=")) {
                String[] parts = expr.split("!=");
                return parts.length >= 2 && !parts[0].trim().equalsIgnoreCase(parts[1].trim());
            } else if (expr.contains("==")) {
                String[] parts = expr.split("==");
                return parts.length >= 2 && parts[0].trim().equalsIgnoreCase(parts[1].trim());
            } else if (expr.contains(">")) {
                String[] parts = expr.split(">");
                return parts.length >= 2 && Double.parseDouble(parts[0].trim()) > Double.parseDouble(parts[1].trim());
            } else if (expr.contains("<")) {
                String[] parts = expr.split("<");
                return parts.length >= 2 && Double.parseDouble(parts[0].trim()) < Double.parseDouble(parts[1].trim());
            }

            // Caso não haja operador, aceita "true"/"false"
            return expr.equalsIgnoreCase("true");
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        map.put("expression", expression);
        return map;
    }

    public static SimpleGateCondition deserialize(Object obj) {
        if (obj == null) return null;

        // Bukkit pode retornar Map ou MemorySection, por isso tratamos ambos
        if (obj instanceof Map<?, ?> map) {
            Object exprObj = map.get("expression");
            if (exprObj instanceof String expr && !expr.isBlank()) {
                return new SimpleGateCondition(expr);
            }
        }

        // Caso tenha vindo de outro formato, tenta interpretar diretamente
        if (obj instanceof String expr && !expr.isBlank()) {
            return new SimpleGateCondition(expr);
        }

        return null;
    }

    public String getDisplayText() {
        return "Condition: " + expression;
    }

    public boolean evaluate(Player player) {
        String expr = expression
                .replace("%player_health%", String.valueOf(player.getHealth()))
                .replace("%player_y%", String.valueOf(player.getLocation().getY()));

        try {
            return evaluateExpression(expr);
        } catch (Exception e) {
            return false;
        }
    }
}
