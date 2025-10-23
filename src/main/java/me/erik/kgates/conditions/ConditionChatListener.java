package me.erik.kgates.conditions;

import me.erik.kgates.builder.BuilderGUIListener;
import me.erik.kgates.builder.GateBuilderData;
import me.erik.kgates.builder.GateBuilderManager;
import me.erik.kgates.manager.GateManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.Arrays;

import static me.erik.kgates.KGates.getInstance;

public record ConditionChatListener(GateBuilderManager builderManager, GateManager gateManager, BuilderGUIListener builderGUI) implements Listener {

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();

        GateBuilderData builder = builderManager.getBuilder(player.getUniqueId());
        if (builder == null) return;

        SimpleGateCondition.ConditionType waitingType = builder.getWaitingConditionType();
        if (waitingType == null) return;

        event.setCancelled(true);
        String msg = event.getMessage().trim();

        if ("cancel".equalsIgnoreCase(msg)) {
            builder.setWaitingConditionType(null);
            player.sendMessage(ChatColor.RED + "Condition input canceled.");
            Bukkit.getScheduler().runTask(getInstance(), () -> new ConditionGUI(builder, builderGUI).openMain(player));
            return;
        }

        try {
            SimpleGateCondition condition = switch (waitingType) {
                case PERMISSION, WEATHER -> new SimpleGateCondition(waitingType, msg);
                case HEALTH -> new SimpleGateCondition(waitingType, Double.parseDouble(msg));
                case TIME -> {
                    String[] parts = msg.split("-");
                    if (parts.length != 2)
                        throw new IllegalArgumentException("Invalid format! Use: start-end (e.g., 6000-18000)");
                    yield new SimpleGateCondition(
                            SimpleGateCondition.ConditionType.TIME,
                            Arrays.toString(new long[]{Long.parseLong(parts[0]), Long.parseLong(parts[1])})
                    );
                }
            };

            builder.addCondition(condition);
            builder.setWaitingConditionType(null);
            gateManager.addGateFromBuilder(builder);

            player.sendMessage(ChatColor.GREEN + "Condition added: " + ChatColor.YELLOW + waitingType.name());
            Bukkit.getScheduler().runTask(getInstance(), () -> new ConditionGUI(builder, builderGUI).openMain(player));

        } catch (Exception ex) {
            player.sendMessage(ChatColor.RED + "Invalid value for condition " + waitingType.name() + ".");
        }
    }
}
