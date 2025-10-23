package me.erik.kgates.conditions;

import me.erik.kgates.builder.GateBuilderData;
import me.erik.kgates.builder.GateBuilderManager;
import me.erik.kgates.manager.GateManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import static me.erik.kgates.KGates.getInstance;

public record ConditionChatListener(GateBuilderManager builderManager, GateManager gateManager) implements Listener {

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        GateBuilderData builder = builderManager.getBuilder(player.getUniqueId());
        if (builder == null || builder.getWaitingConditionType() == null) return;

        event.setCancelled(true);
        String msg = event.getMessage().trim();

        if (msg.equalsIgnoreCase("cancelar")) {
            cancelConditionInput(builder, player);
            return;
        }

        addConditionFromInput(builder, player, msg);
    }

    private void cancelConditionInput(GateBuilderData builder, Player player) {
        builder.setWaitingConditionType(null);
        player.sendMessage(ChatColor.RED + "Entrada de condição cancelada.");
        reopenConditionGUI(builder, player);
    }

    private void addConditionFromInput(GateBuilderData builder, Player player, String input) {
        SimpleGateCondition.ConditionType type = builder.getWaitingConditionType();

        try {
            SimpleGateCondition condition = switch (type) {
                case PERMISSION, WEATHER -> new SimpleGateCondition(type, input);
                case HEALTH -> new SimpleGateCondition(type, Double.parseDouble(input));
                case TIME -> parseTimeCondition(input);
            };

            builder.addCondition(condition);
            builder.setWaitingConditionType(null);

            gateManager.addGateFromBuilder(builder);

            player.sendMessage(ChatColor.GREEN + "Condição adicionada: " + ChatColor.YELLOW + type.name());
            reopenConditionGUI(builder, player);

        } catch (Exception e) {
            player.sendMessage(ChatColor.RED + "Valor inválido para condição " + type.name() + ".");
        }
    }

    private SimpleGateCondition parseTimeCondition(String input) {
        String[] parts = input.split("-");
        if (parts.length != 2) {
            throw new IllegalArgumentException("Formato inválido! Use: inicio-fim (ex: 6000-18000)");
        }
        long start = Long.parseLong(parts[0]);
        long end = Long.parseLong(parts[1]);
        return new SimpleGateCondition(start, end);
    }

    private void reopenConditionGUI(GateBuilderData builder, Player player) {
        Bukkit.getScheduler().runTask(getInstance(), () -> builder.openConditionGUI(player));
    }
}
