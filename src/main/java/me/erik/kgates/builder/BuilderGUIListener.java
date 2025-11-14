package me.erik.kgates.builder;

import me.erik.kgates.manager.GateManager;
import me.erik.kgates.conditions.ConditionGUI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class BuilderGUIListener implements Listener {

    private final BuilderGUI gui;
    private final GateBuilderManager builderManager;

    public BuilderGUIListener(GateBuilderManager builderManager, GateManager gateManager) {
        this.builderManager = builderManager;
        this.gui = new BuilderGUI(builderManager, gateManager, this);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {

        // GUI de condições
        if (ChatColor.stripColor(event.getView().getTitle()).equalsIgnoreCase("Gate Conditions")) {

            event.setCancelled(true);

            if (!(event.getWhoClicked() instanceof Player player)) return;
            var builder = builderManager.getBuilder(player.getUniqueId());
            if (builder == null) return;

            int slot = event.getRawSlot();

            // botão voltar
            if (slot == 18) {
                player.closeInventory();
                BuilderGUI.openPortalEditor(player, builder);
                return;
            }

            // slot da condição
            if (slot == 13) {

                // botão direito → limpar tudo
                if (event.isRightClick()) {
                    builder.clearConditions();
                    player.sendMessage(ChatColor.RED + "Todas as condições foram removidas!");
                    player.closeInventory();
                    new ConditionGUI(builder).openMain(player);
                    return;
                }

                // botão esquerdo → começar input via chat
                player.closeInventory();

                player.sendMessage(ChatColor.YELLOW + "Digite a condição usando placeholders:");
                player.sendMessage(ChatColor.GRAY + "Exemplo: %player_health% >= 10");
                player.sendMessage(ChatColor.DARK_GRAY + "(digite 'cancel' para cancelar)");

                builder.setAwaitingConditionInput(true);
                return;
            }

            return;
        }

        // Lógica da BuilderGUI normal
        gui.handleInventoryClick(event);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        gui.handleBlockClick(event);
    }

    // ======================================================
    //               INPUT VIA CHAT (NOVO)
    // ======================================================

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();

        GateBuilderData data = builderManager.getBuilder(player.getUniqueId());
        if (data == null) return;

        // se não estiver esperando input que NÃO seja condição, ignora
        if (!data.isAwaitingAnyInput() || data.isAwaitingConditionInput()) return;

        event.setCancelled(true);
        String msg = event.getMessage().trim();

        // Cancelamento universal
        if (msg.equalsIgnoreCase("cancel") || msg.equalsIgnoreCase("cancelar")) {
            data.clearAllAwaitingFlags();
            player.sendMessage("§cInput cancelado.");
            reopenBuilderGUI(player, data);
            return;
        }

        // Envia para o handler
        boolean handled = BuilderInputHandler.handle(player, data, msg);

        if (!handled) {
            player.sendMessage("§cNão estou esperando esse tipo de input agora.");
            return;
        }

        // Input OK → reabrir GUI principal
        reopenBuilderGUI(player, data);
    }
    private void reopenBuilderGUI(Player player, GateBuilderData data) {
        Bukkit.getScheduler().runTask(me.erik.kgates.KGates.getInstance(), () -> {
            BuilderGUI.openPortalEditor(player, data);
        });
    }

}
