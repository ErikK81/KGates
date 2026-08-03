package me.erik.kgates.conditions;

import me.erik.kgates.builder.BuilderGUIItems;
import me.erik.kgates.builder.BuilderMenuHolder;
import me.erik.kgates.builder.GateBuilderData;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.List;

public final class ConditionGUI {
    public static final int ADD = 11, CLEAR = 15, SUMMARY = 13, BACK = 22;

    public void open(Player player, GateBuilderData data) {
        Inventory inventory = Bukkit.createInventory(
                new BuilderMenuHolder(BuilderMenuHolder.Menu.CONDITIONS), 27,
                ChatColor.DARK_AQUA + "Condições | " + data.getId());
        BuilderGUIItems.fill(inventory, Material.BLUE_STAINED_GLASS_PANE);
        inventory.setItem(ADD, BuilderGUIItems.item(Material.LIME_DYE, ChatColor.GREEN + "Adicionar condição",
                List.of(ChatColor.GRAY + "Use placeholders e comparadores", ChatColor.DARK_GRAY + "Ex.: %player_health% >= 10")));
        List<String> summary = new ArrayList<>();
        for (SimpleGateCondition condition : data.getConditions()) summary.add(ChatColor.GRAY + "• " + ChatColor.WHITE + condition.getExpression());
        if (summary.isEmpty()) summary.add(ChatColor.DARK_GRAY + "Nenhuma condição configurada");
        inventory.setItem(SUMMARY, BuilderGUIItems.item(Material.REPEATER,
                ChatColor.AQUA + "Condições atuais (" + data.getConditions().size() + ")", summary));
        inventory.setItem(CLEAR, BuilderGUIItems.item(Material.BARRIER, ChatColor.RED + "Limpar condições",
                List.of(ChatColor.GRAY + "Remove todas as condições")));
        inventory.setItem(BACK, BuilderGUIItems.back());
        player.openInventory(inventory);
    }
}
