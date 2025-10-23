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

import java.util.*;

public record ConditionGUI(GateBuilderData builderData) implements Listener {

    private static final int ADD_BUTTON_SLOT = 26;

    // Configuração de slots para seleção de tipo de condição
    private static final LinkedHashMap<Integer, SimpleGateCondition.ConditionType> SLOT_TO_CONDITION = new LinkedHashMap<>() {{
        put(10, SimpleGateCondition.ConditionType.PERMISSION);
        put(12, SimpleGateCondition.ConditionType.WEATHER);
        put(14, SimpleGateCondition.ConditionType.TIME);
        put(16, SimpleGateCondition.ConditionType.HEALTH);
    }};

    public void open(Player player) {
        Inventory inv = Bukkit.createInventory(null, 27, "Gate Conditions");

        // Adiciona as condições já definidas
        List<SimpleGateCondition> conditions = builderData.getConditions();
        for (int i = 0; i < conditions.size(); i++) {
            inv.setItem(i, makeItem(Material.PAPER, "§e" + conditions.get(i).getType().name(),
                    List.of("§7" + conditions.get(i).getDisplayText(), "§cClique direito para remover")));
        }

        // Botão para adicionar nova condição
        inv.setItem(ADD_BUTTON_SLOT, makeItem(Material.EMERALD_BLOCK, "§aAdicionar nova condição", null));

        player.openInventory(inv);
    }

    private ItemStack makeItem(Material mat, String name, List<String> lore) {
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setDisplayName(name);
        if (lore != null) meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        Player player = (Player) e.getWhoClicked();
        String title = e.getView().getTitle();
        int slot = e.getRawSlot();

        if (title.equals("Gate Conditions")) {
            e.setCancelled(true);
            handleMainGUI(player, slot, e.isRightClick());
        } else if (title.equals("Selecionar Condição")) {
            e.setCancelled(true);
            handleSelectorGUI(player, slot, e.isRightClick());
        }
    }

    private void handleMainGUI(Player player, int slot, boolean rightClick) {
        if (slot == ADD_BUTTON_SLOT) {
            new ConditionSelectorGUI(builderData).open(player);
            return;
        }

        List<SimpleGateCondition> conditions = builderData.getConditions();
        if (slot >= 0 && slot < conditions.size() && rightClick) {
            SimpleGateCondition removed = conditions.remove(slot);
            player.sendMessage("§cCondição §6" + removed.getType().name() + "§c removida!");
            open(player);
        }
    }

    private void handleSelectorGUI(Player player, int slot, boolean rightClick) {
        SimpleGateCondition.ConditionType type = SLOT_TO_CONDITION.get(slot);
        if (type == null) return;

        if (rightClick) {
            builderData.removeCondition(type);
            player.closeInventory();
            player.sendMessage("§cCondição §6" + type.name() + "§c removida!");
        } else {
            builderData.startConditionInput(type, player);
        }
    }
}

// GUI genérica de seleção de opções, reutilizável
record ConditionSelectorGUI(GateBuilderData builderData) {

    private static final Map<Integer, OptionData> OPTIONS = Map.of(
            10, new OptionData(Material.NAME_TAG, "Permission", SimpleGateCondition.ConditionType.PERMISSION),
            12, new OptionData(Material.SUNFLOWER, "Weather", SimpleGateCondition.ConditionType.WEATHER),
            14, new OptionData(Material.CLOCK, "Time", SimpleGateCondition.ConditionType.TIME),
            16, new OptionData(Material.APPLE, "Health", SimpleGateCondition.ConditionType.HEALTH)
    );

    public void open(Player player) {
        Inventory inv = Bukkit.createInventory(null, 27, "Selecionar Condição");

        OPTIONS.forEach((slot, option) -> inv.setItem(slot, makeItem(option.material(), "§e" + option.name(), null)));
        player.openInventory(inv);
    }

    private ItemStack makeItem(Material mat, String name, List<String> lore) {
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setDisplayName(name);
        if (lore != null) meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    // Classe interna para dados de opção
    private record OptionData(Material material, String name, SimpleGateCondition.ConditionType type) {}
}
