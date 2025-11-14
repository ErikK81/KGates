package me.erik.kgates.conditions;

import me.erik.kgates.builder.GateBuilderData;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public record ConditionGUI(GateBuilderData builderData) {

    public void openMain(Player player) {
        Inventory inv = Bukkit.createInventory(null, 27, ChatColor.DARK_AQUA + "Gate Conditions");

        inv.setItem(13, makeConditionItem());
        inv.setItem(18, makeBackItem());

        player.openInventory(inv);
    }

    private ItemStack makeConditionItem() {
        ItemStack item = new ItemStack(Material.REPEATER);
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.setDisplayName(ChatColor.AQUA + "Placeholder Condition");

            List<SimpleGateCondition> conditions = builderData.getConditions();

            if (!conditions.isEmpty()) {
                String formatted = ChatColor.WHITE + String.join(
                        ChatColor.GRAY + ", " + ChatColor.WHITE,
                        conditions.stream().map(SimpleGateCondition::getExpression).toList()
                );

                meta.setLore(List.of(
                        ChatColor.GREEN + "Ativas:",
                        formatted,
                        "",
                        ChatColor.GRAY + "Left-click → adicionar nova",
                        ChatColor.GRAY + "Right-click → remover todas"
                ));
            } else {
                meta.setLore(List.of(
                        ChatColor.GRAY + "Nenhuma condição definida",
                        "",
                        ChatColor.GRAY + "Left-click → adicionar condição"
                ));
            }

            item.setItemMeta(meta);
        }

        return item;
    }

    private static ItemStack makeBackItem() {
        ItemStack item = new ItemStack(Material.ARROW);
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.setDisplayName(ChatColor.AQUA + "Voltar");
            meta.setLore(List.of(ChatColor.GRAY + "Retornar ao Builder"));
            item.setItemMeta(meta);
        }

        return item;
    }
}
