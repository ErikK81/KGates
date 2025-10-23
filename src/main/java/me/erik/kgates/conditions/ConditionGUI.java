package me.erik.kgates.conditions;

import me.erik.kgates.builder.GateBuilderData;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public record ConditionGUI(GateBuilderData builderData) implements Listener {

    private static final int ADD_BUTTON_SLOT = 26;

    public void open(Player player) {
        Inventory inv = Bukkit.createInventory(null, 27, "Gate Conditions");

        List<SimpleGateCondition> conditions = builderData.getConditions();
        for (int i = 0; i < conditions.size(); i++) {
            SimpleGateCondition cond = conditions.get(i);
            ItemStack item = new ItemStack(Material.PAPER);
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                meta.setDisplayName("§e" + cond.getType().name());
                meta.setLore(List.of("§7" + cond.getDisplayText(), "§cClique direito para remover"));
                item.setItemMeta(meta);
                inv.setItem(i, item);
            }
        }

        ItemStack add = new ItemStack(Material.EMERALD_BLOCK);
        ItemMeta meta = add.getItemMeta();
        if (meta != null) {
            meta.setDisplayName("§aAdicionar nova condição");
            add.setItemMeta(meta);
            inv.setItem(ADD_BUTTON_SLOT, add);
        }

        player.openInventory(inv);
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        String title = e.getView().getTitle();
        Player player = (Player) e.getWhoClicked();

        if ("Gate Conditions".equals(title)) {
            e.setCancelled(true);
            int slot = e.getRawSlot();

            if (slot == ADD_BUTTON_SLOT) {
                new ConditionSelectorGUI(builderData).open(player);
                return;
            }

            List<SimpleGateCondition> conditions = builderData.getConditions();
            if (slot >= 0 && slot < conditions.size() && e.isRightClick()) {
                SimpleGateCondition removed = conditions.remove(slot);
                player.sendMessage("§cCondição §6" + removed.getType().name() + "§c removida!");
                open(player);
            }
        }
    }
}
