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
 * @param parent para poder reabrir GUI do builder
 */
public record ConditionSelectionListener(GateBuilderManager builderManager, GateBuilderData builder,
                                         BuilderGUIListener parent) implements Listener {

    @EventHandler
    public void onConditionInventoryClick(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player player)) return;
        if (e.getClickedInventory() == null) return;

        String title = e.getView().getTitle();
        if (!"Selecionar Condição".equals(title) && !"Gate Conditions".equals(title)) return;

        // somente top inventory
        if (e.getClickedInventory() != e.getView().getTopInventory()) return;

        e.setCancelled(true);
        int slot = e.getRawSlot();

        SimpleGateCondition.ConditionType type = switch (slot) {
            case 10 -> SimpleGateCondition.ConditionType.PERMISSION;
            case 12 -> SimpleGateCondition.ConditionType.WEATHER;
            case 14 -> SimpleGateCondition.ConditionType.TIME;
            case 16 -> SimpleGateCondition.ConditionType.HEALTH;
            default -> null;
        };

        // botão voltar (se existir)
        if (type == null) {
            if ("Gate Conditions".equals(title) && slot == 18) {
                player.closeInventory();
                // reabre GUI principal do builder no main thread
                Bukkit.getScheduler().runTask(KGates.getInstance(), () -> parent.openBuilderGUI(player, builder));
                cleanup();
            }
            return;
        }

        // clique direito = remover condição
        if (e.isRightClick()) {
            builder.removeCondition(type);
            // reabre GUI de condições (ConditionGUI) — essa GUI deve estar registrada separadamente
            ConditionGUI gui = new ConditionGUI(builder, parent);
            // re-abrir a GUI de condições (registrando temporariamente a GUI listener original)
            Bukkit.getPluginManager().registerEvents(gui, KGates.getInstance());
            gui.openMain(player);
            // manter este listener ativo — não limpa ainda
            return;
        }

        // clique esquerdo: iniciar entrada via chat
        player.closeInventory();
        // aqui chamamos método do builder para iniciar o input de condição (presuma que existe)
        builder.startConditionInput(type, player);
        // este listener temporário não é mais necessário
        cleanup();
    }

    private void cleanup() {
        // desregistra este listener para evitar acumular instâncias
        HandlerList.unregisterAll(this);
    }
}
