package me.erik.kgates.conditions;

import me.erik.kgates.KGates;
import me.erik.kgates.builder.BuilderGUIListener;
import me.erik.kgates.builder.GateBuilderData;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

/**
 * GUI simplificada para configurar uma condição PlaceholderAPI.
 */
public class ConditionGUI implements Listener {

    private final GateBuilderData builderData;
    private final BuilderGUIListener builderGUIListener;

    public ConditionGUI(GateBuilderData builderData, BuilderGUIListener builderGUIListener) {
        this.builderData = builderData;
        this.builderGUIListener = builderGUIListener;
    }

    public void openMain(Player player) {
        Inventory inv = Bukkit.createInventory(null, 27, ChatColor.DARK_AQUA + "Gate Conditions");

        inv.setItem(13, makeConditionItem());
        inv.setItem(18, makeBackItem());

        player.openInventory(inv);
        Bukkit.getPluginManager().registerEvents(this, KGates.getInstance());
    }

    private ItemStack makeConditionItem() {
        ItemStack item = new ItemStack(Material.REPEATER);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.AQUA + "Placeholder Condition");

            List<SimpleGateCondition> conditions = builderData.getConditions();
            if (conditions != null && !conditions.isEmpty()) {
                String formatted = ChatColor.WHITE + conditions.stream()
                        .map(SimpleGateCondition::getExpression)
                        .reduce((a, b) -> a + ChatColor.GRAY + ", " + ChatColor.WHITE + b)
                        .orElse("");
                meta.setLore(List.of(
                        ChatColor.GREEN + "Active:",
                        formatted,
                        "",
                        ChatColor.GRAY + "Left-click to edit",
                        ChatColor.GRAY + "Right-click to remove"
                ));
            } else {
                meta.setLore(List.of(
                        ChatColor.GRAY + "No condition defined",
                        "",
                        ChatColor.GRAY + "Left-click to add new condition"
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
            meta.setDisplayName(ChatColor.AQUA + "Back");
            meta.setLore(List.of(ChatColor.GRAY + "Return to Builder"));
            item.setItemMeta(meta);
        }
        return item;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player player)) return;
        if (e.getClickedInventory() == null) return;
        if (!ChatColor.stripColor(e.getView().getTitle()).equalsIgnoreCase("Gate Conditions")) return;

        e.setCancelled(true);
        int slot = e.getRawSlot();

        if (slot == 18) { // back button
            HandlerList.unregisterAll(this);
            player.closeInventory();
            builderGUIListener.openBuilderGUI(player, builderData);
            return;
        }

        if (slot != 13) return; // only central item is the condition

        if (e.isRightClick()) {
            builderData.clearConditions();
            player.sendMessage(ChatColor.RED + "All conditions removed!");
            Bukkit.getScheduler().runTask(KGates.getInstance(), () -> {
                player.closeInventory();
                openMain(player);
            });
            return;
        }

        // Left-click: start chat input
        HandlerList.unregisterAll(this);
        player.closeInventory();

        Bukkit.getScheduler().runTaskLater(KGates.getInstance(), () -> startConditionInput(player), 1L);
    }

    private void startConditionInput(Player player) {
        player.sendMessage(ChatColor.YELLOW + "Type your condition using placeholders (e.g. %player_health% >= 10)");
        player.sendMessage(ChatColor.GRAY + "Example: %server_online% < 50 or %player_has_permission_vip% == true");
        player.sendMessage(ChatColor.DARK_GRAY + "(Type 'cancel' to abort)");

        builderData.setAwaitingConditionInput(true); // garante modo input ativo
        builderData.startConditionInput(player);

        Bukkit.getLogger().info("[DEBUG] " + player.getName() + " agora está no modo de input de condição.");
    }
}
