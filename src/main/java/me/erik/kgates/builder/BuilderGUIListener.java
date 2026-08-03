package me.erik.kgates.builder;

import me.erik.kgates.manager.GateManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import static me.erik.kgates.KGates.getInstance;

public final class BuilderGUIListener implements Listener {
    private final BuilderGUI gui;
    private final GateBuilderManager builderManager;

    public BuilderGUIListener(GateBuilderManager builderManager, GateManager gateManager) {
        this.builderManager = builderManager;
        this.gui = new BuilderGUI(builderManager, gateManager);
    }

    public BuilderGUI gui() { return gui; }

    @EventHandler public void onInventoryClick(InventoryClickEvent event) { gui.handleInventoryClick(event); }
    @EventHandler public void onPlayerInteract(PlayerInteractEvent event) { gui.handleBlockClick(event); }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!(event.getInventory().getHolder() instanceof BuilderMenuHolder holder)) return;
        if (holder.menu() == BuilderMenuHolder.Menu.BROWSE) return;
        if (!(event.getPlayer() instanceof Player player)) return;

        // Inventory navigation also fires a close event. Waiting one tick lets us
        // distinguish navigation/input prompts from an intentional manual close.
        Bukkit.getScheduler().runTask(getInstance(), () -> {
            GateBuilderData data = builderManager.getBuilder(player.getUniqueId());
            if (data == null) return;
            if (player.getOpenInventory().getTopInventory().getHolder() instanceof BuilderMenuHolder) return;
            if (data.isAwaitingAnyInput() || builderManager.isWaitingForBlockClick(player.getUniqueId())) return;

            builderManager.stopBuilding(player.getUniqueId());
            player.sendMessage(ChatColor.RED + "Modo de edição encerrado.");
        });
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        GateBuilderData data = builderManager.getBuilder(player.getUniqueId());
        if (data == null || !data.isAwaitingAnyInput() || data.isAwaitingConditionInput()) return;
        event.setCancelled(true);
        String message = event.getMessage().trim();
        if (message.equalsIgnoreCase("cancel") || message.equalsIgnoreCase("cancelar")) {
            data.clearAllAwaitingFlags();
            player.sendMessage(ChatColor.RED + "Entrada cancelada.");
        } else if (!BuilderInputHandler.handle(player, data, message)) {
            player.sendMessage(ChatColor.RED + "Nenhuma entrada estava pendente.");
        }
        Bukkit.getScheduler().runTask(getInstance(), () -> gui.openEditor(player, data));
    }
}
