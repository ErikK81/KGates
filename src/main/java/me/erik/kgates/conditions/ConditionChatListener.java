package me.erik.kgates.conditions;

import me.erik.kgates.builder.BuilderGUIListener;
import me.erik.kgates.builder.BuilderInputHandler;
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

public record ConditionChatListener(
        GateBuilderManager builderManager,
        GateManager gateManager,
        BuilderGUIListener builderGUI) implements Listener {

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        String msg = event.getMessage().trim();

        GateBuilderData builder = builderManager.getBuilder(player.getUniqueId());
        if (builder == null) return;

        // Se nenhum input de condição está ativo, não é nosso evento
        if (!builder.isAwaitingConditionInput()) return;

        // Cancela o chat normal
        event.setCancelled(true);

        // Processa "cancel" antes do handler
        if (msg.equalsIgnoreCase("cancel")) {
            builder.setAwaitingConditionInput(false);
            player.sendMessage(ChatColor.RED + "Entrada de condição cancelada.");
            Bukkit.getScheduler().runTask(getInstance(),
                    () -> new ConditionGUI(builder).openMain(player));
            return;
        }

        // Lida com a condição via BuilderInputHandler (mantém o padrão!)
        boolean handled = BuilderInputHandler.handle(player, builder, msg);

        if (!handled) {
            // Isso só ocorre se o flag estava ativo mas o handler não tratou
            player.sendMessage(ChatColor.RED + "Formato inválido! Exemplo: %player_health% >= 10");
            return;
        }

        // Condição foi aceita — abrir GUI novamente
        Bukkit.getScheduler().runTask(getInstance(), () -> {
            new ConditionGUI(builder).openMain(player);
        });

        // (Opcional)
        // Se você quiser criar automaticamente o gate após condição:
        // gateManager.addGateFromBuilder(builder);
    }
}
