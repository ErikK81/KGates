package me.erik.kgates.conditions;

import me.erik.kgates.builder.GateBuilderData;
import me.erik.kgates.manager.GateManager;
import me.erik.kgates.builder.GateBuilderManager;
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
        if (builder == null) return;

        SimpleGateCondition.ConditionType waitingType = builder.getWaitingConditionType();
        if (waitingType == null) return;

        event.setCancelled(true);
        String msg = event.getMessage().trim();

        if ("cancelar".equalsIgnoreCase(msg)) {
            builder.setWaitingConditionType(null);
            player.sendMessage(ChatColor.RED + "Entrada de condição cancelada.");
            Bukkit.getScheduler().runTask(getInstance(), () -> builder.openConditionGUI(player));
            return;
        }

        try {
            SimpleGateCondition condition = switch (waitingType) {
                case PERMISSION, WEATHER -> new SimpleGateCondition(waitingType, msg);
                case HEALTH -> new SimpleGateCondition(waitingType, Double.parseDouble(msg));
                case TIME -> {
                    String[] parts = msg.split("-");
                    if (parts.length != 2) throw new IllegalArgumentException("Formato inválido! Use: inicio-fim (ex: 6000-18000)");
                    yield new SimpleGateCondition(Long.parseLong(parts[0]), Long.parseLong(parts[1]));
                }
            };

            builder.addCondition(condition);
            builder.setWaitingConditionType(null);
            gateManager.addGateFromBuilder(builder);

            player.sendMessage(ChatColor.GREEN + "Condição adicionada: " + ChatColor.YELLOW + waitingType.name());
            Bukkit.getScheduler().runTask(getInstance(), () -> builder.openConditionGUI(player));

        } catch (Exception ex) {
            player.sendMessage(ChatColor.RED + "Valor inválido para condição " + waitingType.name() + ".");
        }
    }
}
