package me.erik.kgates.conditions;

import me.erik.kgates.builder.GateBuilderData;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public record ConditionSelectorGUI(GateBuilderData builderData) implements Listener {

    public void open(Player player) {
        Inventory inv = Bukkit.createInventory(null, 27, "Selecionar Condição");

        addOption(inv, 10, Material.NAME_TAG, SimpleGateCondition.ConditionType.PERMISSION);
        addOption(inv, 12, Material.SUNFLOWER, SimpleGateCondition.ConditionType.WEATHER);
        addOption(inv, 14, Material.CLOCK, SimpleGateCondition.ConditionType.TIME);
        addOption(inv, 16, Material.APPLE, SimpleGateCondition.ConditionType.HEALTH);

        player.openInventory(inv);
    }

    private void addOption(Inventory inv, int slot, Material mat, SimpleGateCondition.ConditionType type) {
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName("§e" + type.name());
            item.setItemMeta(meta);
            inv.setItem(slot, item);
        }
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (!"Selecionar Condição".equals(e.getView().getTitle())) return;
        e.setCancelled(true);

        Player player = (Player) e.getWhoClicked();
        int slot = e.getRawSlot();

        SimpleGateCondition.ConditionType type = switch (slot) {
            case 10 -> SimpleGateCondition.ConditionType.PERMISSION;
            case 12 -> SimpleGateCondition.ConditionType.WEATHER;
            case 14 -> SimpleGateCondition.ConditionType.TIME;
            case 16 -> SimpleGateCondition.ConditionType.HEALTH;
            default -> null;
        };

        if (type == null) return;

        if (e.isRightClick()) {
            builderData.removeCondition(type);
            player.closeInventory();
            player.sendMessage("§cCondição §6" + type.name() + "§c removida!");
        } else {
            builderData.startConditionInput(type, player);
        }
    }
}
