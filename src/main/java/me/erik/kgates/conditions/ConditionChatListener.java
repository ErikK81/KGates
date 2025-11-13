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

import static me.erik.kgates.KGates.getInstance;

public record ConditionChatListener(GateBuilderManager builderManager,
                                    GateManager gateManager,
                                    BuilderGUIListener builderGUI) implements Listener {

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        String msg = event.getMessage().trim();

        GateBuilderData builder = builderManager.getBuilder(player.getUniqueId());
        if (builder == null) return;

        // Garante que o jogador está no modo de input
        if (builder.isAwaitingConditionInput() && !builderManager.isWaitingForConditionInput(player.getUniqueId())) {
            builderManager.setWaitingForConditionInput(player.getUniqueId(), true);
        }

        if (!builderManager.isWaitingForConditionInput(player.getUniqueId())) return;

        // Cancela o chat padrão e processa o input
        event.setCancelled(true);
        builderManager.setWaitingForConditionInput(player.getUniqueId(), false);
        builder.setAwaitingConditionInput(false);

        Bukkit.getScheduler().runTask(getInstance(), () -> {
            try {
                if (msg.equalsIgnoreCase("cancel")) {
                    player.sendMessage(ChatColor.RED + "Condição cancelada.");
                    new ConditionGUI(builder, builderGUI).openMain(player);
                    return;
                }

                SimpleGateCondition condition = new SimpleGateCondition(msg);
                builder.addCondition(condition);
                gateManager.addGateFromBuilder(builder);

                player.sendMessage(ChatColor.GREEN + "Condição salva: " + ChatColor.WHITE + msg);

                ConditionGUI gui = new ConditionGUI(builder, builderGUI);
                Bukkit.getPluginManager().registerEvents(gui, getInstance());
                gui.openMain(player);

            } catch (Exception ex) {
                player.sendMessage(ChatColor.RED + "Formato inválido! Exemplo: %player_health% >= 10");
                ex.printStackTrace();
            }
        });
    }
}
