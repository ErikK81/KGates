package me.erik.kgates.conditions;

import me.erik.kgates.builder.GateBuilderData;
import me.erik.kgates.builder.BuilderGUIListener;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

import static me.erik.kgates.KGates.getInstance;

public record ConditionGUI(GateBuilderData builderData, BuilderGUIListener builderGUIListener) implements Listener {

    public void openMain(Player player) {
        Inventory inv = Bukkit.createInventory(null, 27, "Gate Conditions");

        inv.setItem(10, makeConditionItem(SimpleGateCondition.ConditionType.WEATHER));
        inv.setItem(12, makeConditionItem(SimpleGateCondition.ConditionType.TIME));
        inv.setItem(14, makeConditionItem(SimpleGateCondition.ConditionType.PERMISSION));
        inv.setItem(16, makeConditionItem(SimpleGateCondition.ConditionType.HEALTH));
        inv.setItem(18, makeItem(ChatColor.AQUA + "Back"));

        player.openInventory(inv);
        Bukkit.getPluginManager().registerEvents(this, getInstance());
    }

    private ItemStack makeConditionItem(SimpleGateCondition.ConditionType type) {
        Material mat = switch (type) {
            case WEATHER -> Material.FEATHER;
            case TIME -> Material.CLOCK;
            case PERMISSION -> Material.BARRIER;
            case HEALTH -> Material.APPLE;
        };

        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.AQUA + type.name());

            List<String> lore = getStrings(type);
            meta.setLore(lore);
            item.setItemMeta(meta);
        }
        return item;
    }

    private List<String> getStrings(SimpleGateCondition.ConditionType type) {
        List<String> lore = new ArrayList<>();
        boolean hasCondition = false;
        for (SimpleGateCondition cond : builderData.getConditions()) {
            if (cond.getType() == type) {
                lore.add(ChatColor.GREEN + "Active: " + cond.getDisplayText());
                hasCondition = true;
            }
        }
        if (!hasCondition) lore.add(ChatColor.GRAY + "No condition defined");

        lore.add("");
        lore.add(ChatColor.GRAY + "Left-click to edit, right-click to remove");
        return lore;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player player)) return;
        if (e.getClickedInventory() == null) return;
        if (!"Gate Conditions".equals(e.getView().getTitle())) return;

        e.setCancelled(true);
        int slot = e.getRawSlot();

        if (slot == 18) { // back
            player.closeInventory();
            HandlerList.unregisterAll(this);
            builderGUIListener.openBuilderGUI(player, builderData);
            return;
        }

        SimpleGateCondition.ConditionType type = switch (slot) {
            case 10 -> SimpleGateCondition.ConditionType.WEATHER;
            case 12 -> SimpleGateCondition.ConditionType.TIME;
            case 14 -> SimpleGateCondition.ConditionType.PERMISSION;
            case 16 -> SimpleGateCondition.ConditionType.HEALTH;
            default -> null;
        };
        if (type == null) return;

        if (e.isRightClick()) {
            builderData.removeCondition(type);
            player.sendMessage(ChatColor.RED + "Condition " + type.name() + " removed!");
            player.closeInventory();
            Bukkit.getScheduler().runTask(getInstance(), () -> openMain(player));
            return;
        }

        // Start chat input
        player.closeInventory();
        builderData.startConditionInput(type, player);
        HandlerList.unregisterAll(this);
    }

    private static ItemStack makeItem(String name) {
        ItemStack item = new ItemStack(Material.ARROW);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
            meta.setLore(List.of(ChatColor.GRAY + "Click to configure"));
            item.setItemMeta(meta);
        }
        return item;
    }
}
