package me.erik.kgates.builder;

import me.erik.kgates.manager.GateManager;
import me.erik.kgates.conditions.SimpleGateCondition;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static me.erik.kgates.KGates.getInstance;

public record BuilderGUIListener(GateBuilderManager builderManager, GateManager gateManager) implements Listener {

    private static final int FINALIZE_SLOT = 26;

    // -------------------- Inventory Click --------------------
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (event.getClickedInventory() == null) return;

        String title = event.getView().getTitle();
        event.setCancelled(true);

        GateBuilderData builder = builderManager.getBuilder(player.getUniqueId());
        if (builder == null) return;

        int slot = event.getRawSlot();

        if (title.startsWith("✧ Portal: ")) handleBuilderGUIClick(player, builder, slot);
        else if (title.equals("Condições do Portal")) handleConditionsGUIClick(player, builder, event.getCurrentItem());
    }

    private void handleBuilderGUIClick(Player player, GateBuilderData builder, int slot) {
        switch (slot) {
            case 10 -> openTypeSelection(player);
            case 12 -> promptPointSelection(player, builder, true);
            case 14 -> promptPointSelection(player, builder, false);
            case 16 -> promptDetectionRadius(player, builder);
            case 20 -> openConditionsGUI(player, builder);
            case 22 -> promptCustomName(player, builder);
            case 24 -> promptCooldown(player, builder);
            case FINALIZE_SLOT -> finalizePortal(player, builder);
        }
    }

    private void handleConditionsGUIClick(Player player, GateBuilderData builder, ItemStack clicked) {
        if (clicked == null || clicked.getType() == Material.AIR) return;

        String name = Objects.requireNonNull(clicked.getItemMeta()).getDisplayName().replace("§a", "");
        SimpleGateCondition.ConditionType type = SimpleGateCondition.ConditionType.valueOf(name);

        player.closeInventory();
        player.sendMessage(ChatColor.AQUA + "Digite o valor para a condição " + type.name() + " no chat:");
        builderManager.setWaitingForCondition(player.getUniqueId(), type);
    }

    // -------------------- Block Click --------------------
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (!builderManager.isBuilding(player.getUniqueId())
                || !builderManager.isWaitingForBlockClick(player.getUniqueId())
                || event.getAction() != Action.LEFT_CLICK_BLOCK) return;

        event.setCancelled(true);
        GateBuilderData builder = builderManager.getBuilder(player.getUniqueId());
        if (builder == null) return;

        Location loc = Objects.requireNonNull(event.getClickedBlock()).getLocation();
        if (builderManager.isWaitingForPointA(player.getUniqueId())) {
            builder.setLocA(loc);
            builderManager.setWaitingForPointA(player.getUniqueId(), false);
            player.sendMessage(ChatColor.GREEN + "Ponto A definido!");
        } else {
            builder.setLocB(loc);
            builderManager.setWaitingForBlockClick(player.getUniqueId(), false);
            player.sendMessage(ChatColor.RED + "Ponto B definido!");
        }

        Bukkit.getScheduler().runTask(getInstance(), () -> openBuilderGUI(player, builder));
    }

    // -------------------- Chat Input --------------------
    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        GateBuilderData builder = builderManager.getBuilder(player.getUniqueId());
        if (builder == null) return;

        String msg = event.getMessage();
        event.setCancelled(true);

        if (handleConditionInput(player, builder, msg)) return;
        handleGeneralInput(player, builder, msg);
    }

    private boolean handleConditionInput(Player player, GateBuilderData builder, String msg) {
        SimpleGateCondition.ConditionType waiting = builderManager.getWaitingCondition(player.getUniqueId());
        if (waiting == null) return false;

        try {
            SimpleGateCondition condition = switch (waiting) {
                case PERMISSION, WEATHER -> new SimpleGateCondition(waiting, msg);
                case HEALTH -> new SimpleGateCondition(waiting, Double.parseDouble(msg));
                case TIME -> {
                    String[] parts = msg.split("-");
                    yield new SimpleGateCondition(Long.parseLong(parts[0]), Long.parseLong(parts[1]));
                }
            };
            builder.addCondition(condition);
            player.sendMessage(ChatColor.GREEN + "Condição " + waiting.name() + " adicionada!");
        } catch (Exception e) {
            player.sendMessage(ChatColor.RED + "Valor inválido. Use o formato correto.");
            return true;
        }

        builderManager.removeWaitingCondition(player.getUniqueId());
        Bukkit.getScheduler().runTask(getInstance(), () -> openBuilderGUI(player, builder));
        return true;
    }

    private boolean handleGeneralInput(Player player, GateBuilderData builder, String msg) {
        if (!builderManager.isWaitingForName(player.getUniqueId())) return false;

        try {
            if (builder.isAwaitingRadius()) {
                double radius = Double.parseDouble(msg);
                builder.setDetectionRadius(radius);
                builder.setAwaitingRadius(false);
                player.sendMessage(ChatColor.GREEN + "Detection radius definido: " + ChatColor.YELLOW + radius);
            } else if (builder.isAwaitingCooldown()) {
                long ticks = Long.parseLong(msg);
                builder.setCooldownTicks(ticks);
                builder.setAwaitingCooldown(false);
                player.sendMessage(ChatColor.GREEN + "Cooldown definido: " + ChatColor.YELLOW + ticks + " ticks");
            } else {
                builder.setName(msg);
                player.sendMessage(ChatColor.GREEN + "Nome definido como: " + ChatColor.YELLOW + msg);
            }
        } catch (NumberFormatException e) {
            player.sendMessage(ChatColor.RED + "Valor inválido. Tente novamente.");
            return true;
        }

        builderManager.setWaitingForName(player.getUniqueId(), false);
        Bukkit.getScheduler().runTask(getInstance(), () -> openBuilderGUI(player, builder));
        return true;
    }

    // -------------------- GUI --------------------
    public void openBuilderGUI(Player player, GateBuilderData builder) {
        Inventory inv = Bukkit.createInventory(null, 27, "✧ Portal: " + builder.getId());

        inv.setItem(10, makeItem(Material.PAPER, ChatColor.AQUA + "Tipo: " + ChatColor.YELLOW + builder.getType()));
        inv.setItem(12, makePointItem(ChatColor.GREEN + "Ponto A", builder.getLocA(), Material.GREEN_WOOL));
        inv.setItem(14, makePointItem(ChatColor.RED + "Ponto B", builder.getLocB(), Material.RED_WOOL));
        inv.setItem(16, makeItem(Material.LIME_DYE, ChatColor.AQUA + "Detection Radius: " + ChatColor.YELLOW + builder.getDetectionRadius()));
        inv.setItem(20, getConditionItem(builder));
        inv.setItem(22, makeItem(Material.NAME_TAG, ChatColor.AQUA + "Nome: " + ChatColor.YELLOW + builder.getName()));
        inv.setItem(24, makeItem(Material.CLOCK, ChatColor.AQUA + "Cooldown (ticks): " + ChatColor.YELLOW + builder.getCooldownTicks()));
        inv.setItem(FINALIZE_SLOT, makeItem(Material.EMERALD_BLOCK, ChatColor.GREEN + "Finalizar Portal"));

        player.openInventory(inv);
    }

    private static ItemStack makeItem(Material mat, String name) {
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) meta.setDisplayName(name);
        item.setItemMeta(meta);
        return item;
    }

    private static ItemStack makePointItem(String name, Location loc, Material mat) {
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
            meta.setLore(List.of(loc != null ? String.format("X: %.0f Y: %.0f Z: %.0f", loc.getX(), loc.getY(), loc.getZ()) : "Não definido"));
            item.setItemMeta(meta);
        }
        return item;
    }

    private static ItemStack getConditionItem(GateBuilderData builder) {
        ItemStack item = new ItemStack(Material.IRON_BARS);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.LIGHT_PURPLE + "Condições");
            List<String> lore = new ArrayList<>();
            if (builder.getConditions().isEmpty()) lore.add("Nenhuma condição definida");
            else builder.getConditions().forEach(c -> lore.add(ChatColor.GRAY + "- " + c.getDisplayText()));
            meta.setLore(lore);
            item.setItemMeta(meta);
        }
        return item;
    }

    public void openConditionsGUI(Player player, GateBuilderData builder) {
        Inventory gui = Bukkit.createInventory(null, 9, "Condições do Portal");
        SimpleGateCondition.ConditionType[] types = SimpleGateCondition.ConditionType.values();

        for (int i = 0; i < types.length; i++) {
            SimpleGateCondition.ConditionType type = types[i];
            ItemStack item = new ItemStack(Material.PAPER);
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                meta.setDisplayName("§a" + type.name());
                String val = builder.getConditions().stream()
                        .filter(c -> c.getType() == type)
                        .findFirst()
                        .map(SimpleGateCondition::getDisplayText)
                        .orElse("Clique para definir valor da condição");
                meta.setLore(List.of(val));
                item.setItemMeta(meta);
                gui.setItem(i, item);
            }
        }

        player.openInventory(gui);
    }

    // -------------------- Prompts --------------------
    private void promptPointSelection(Player player, GateBuilderData builder, boolean isPointA) {
        builderManager.setWaitingForBlockClick(player.getUniqueId(), true);
        builderManager.setWaitingForPointA(player.getUniqueId(), isPointA);
        player.closeInventory();
        player.sendMessage(ChatColor.YELLOW + "Clique com o botão esquerdo em um bloco para definir " +
                (isPointA ? "Ponto A" : "Ponto B") + "!");
    }

    private void promptCustomName(Player player, GateBuilderData builder) {
        builderManager.setWaitingForName(player.getUniqueId(), true);
        player.closeInventory();
        player.sendMessage(ChatColor.AQUA + "Digite o nome do portal no chat:");
    }

    private void promptDetectionRadius(Player player, GateBuilderData builder) {
        builderManager.setWaitingForName(player.getUniqueId(), true);
        builder.setAwaitingRadius(true);
        player.closeInventory();
        player.sendMessage(ChatColor.AQUA + "Digite o detection radius do portal no chat:");
    }

    private void promptCooldown(Player player, GateBuilderData builder) {
        builderManager.setWaitingForName(player.getUniqueId(), true);
        builder.setAwaitingCooldown(true);
        player.closeInventory();
        player.sendMessage(ChatColor.AQUA + "Digite o cooldown do portal (em ticks) no chat:");
    }

    private void finalizePortal(Player player, GateBuilderData builder) {
        if (!builder.isComplete()) {
            player.sendMessage(ChatColor.RED + "O portal ainda não está completo!");
            return;
        }
        gateManager.addGateFromBuilder(builder);
        builderManager.stopBuilding(player.getUniqueId());
        player.sendMessage(ChatColor.GREEN + "Portal '" + builder.getName() + "' criado com sucesso!");
        player.closeInventory();
    }

    private void openTypeSelection(Player player) {
        player.sendMessage(ChatColor.YELLOW + "Seleção de tipo ainda não implementada.");
    }


}
