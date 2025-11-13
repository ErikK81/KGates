package me.erik.kgates.conditions;

import me.erik.kgates.KGates;
import me.erik.kgates.builder.BuilderGUIListener;
import me.erik.kgates.builder.GateBuilderData;
import me.erik.kgates.builder.GateBuilderManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.HandlerList;

/**
 * Listener para seleção e edição de condições baseadas em PlaceholderAPI.
 */
public record ConditionSelectionListener(GateBuilderManager builderManager,
                                         GateBuilderData builder,
                                         BuilderGUIListener parent) implements Listener {

    @EventHandler
    public void onConditionInventoryClick(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player player)) return;
        if (e.getClickedInventory() == null) return;

        String title = e.getView().getTitle();
        if (!"Gate Conditions".equals(title)) return;

        // Apenas o inventário superior (GUI principal)
        if (e.getClickedInventory() != e.getView().getTopInventory()) return;

        e.setCancelled(true);
        int slot = e.getRawSlot();

        // Botão voltar
        if (slot == 18) {
            player.closeInventory();
            Bukkit.getScheduler().runTask(KGates.getInstance(), () -> parent.openBuilderGUI(player, builder));
            cleanup();
            return;
        }

        // Clique direito = remover condição
        if (e.isRightClick()) {
            builder.addCondition(null);
            ConditionGUI gui = new ConditionGUI(builder, parent);
            Bukkit.getPluginManager().registerEvents(gui, KGates.getInstance());
            gui.openMain(player);
            return;
        }

        // Clique esquerdo = adicionar/editar condição via chat
        if (e.isLeftClick()) {
            player.closeInventory();
            startPlaceholderInput(player);
            cleanup();
        }
    }

    private void startPlaceholderInput(Player player) {
        player.sendMessage("§eDigite no chat a condição (use placeholders, ex: %player_health% >= 10)");
        player.sendMessage("§7Exemplo: %server_online% < 50 ou %player_has_permission_vip% == true");

        // O GateBuilderData deve possuir método para lidar com input via chat
        builder.startConditionInput(player);
    }

    private void cleanup() {
        HandlerList.unregisterAll(this);
    }
}
