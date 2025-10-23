package me.erik.kgates.builder;

import me.erik.kgates.conditions.ConditionGUI;
import me.erik.kgates.conditions.SimpleGateCondition;
import me.erik.kgates.manager.GateManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static me.erik.kgates.KGates.getInstance;

public class BuilderGUIListener implements Listener {

    private final GateBuilderManager builderManager;
    private final GateManager gateManager;
    private static final int FINALIZE_SLOT = 26;

    public BuilderGUIListener(GateBuilderManager builderManager, GateManager gateManager) {
        this.builderManager = builderManager;
        this.gateManager = gateManager;
    }

    // -------------------- Inventory Click --------------------
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (event.getClickedInventory() == null || !event.getView().getTitle().startsWith("✧ Portal: ")) return;

        event.setCancelled(true);
        GateBuilderData builder = builderManager.getBuilder(player.getUniqueId());
        if (builder == null) return;

        handleBuilderGUIClick(player, builder, event.getRawSlot());
    }

    private void handleBuilderGUIClick(Player player, GateBuilderData builder, int slot) {
        switch (slot) {
            case 10 -> openTypeSelection(player);
            case 12 -> promptPointSelection(player, builder, true);
            case 14 -> promptPointSelection(player, builder, false);
            case 16 -> promptNumericInput(player, builder, "detection");
            case 20 -> openConditionsGUI(player, builder);
            case 22 -> promptTextInput(player);
            case 24 -> promptNumericInput(player, builder, "cooldown");
            case FINALIZE_SLOT -> finalizePortal(player, builder);
        }
    }

    // -------------------- Block Click --------------------
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        UUID id = player.getUniqueId();

        if (!builderManager.isBuilding(id)
                || !builderManager.isWaitingForBlockClick(id)
                || event.getAction() != Action.LEFT_CLICK_BLOCK) return;

        event.setCancelled(true);
        GateBuilderData builder = builderManager.getBuilder(id);
        if (builder == null) return;

        Location loc = Objects.requireNonNull(event.getClickedBlock()).getLocation();
        boolean isPointA = builderManager.isWaitingForPointA(id);

        if (isPointA) {
            builder.setLocA(loc);
            player.sendMessage(ChatColor.GREEN + "Ponto A definido!");
        } else {
            builder.setLocB(loc);
            player.sendMessage(ChatColor.RED + "Ponto B definido!");
        }

        builderManager.setWaitingForBlockClick(id, false);
        Bukkit.getScheduler().runTask(getInstance(), () -> openBuilderGUI(player, builder));
    }

    // -------------------- Chat Input --------------------
    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        GateBuilderData builder = builderManager.getBuilder(player.getUniqueId());
        if (builder == null) return;

        event.setCancelled(true);
        String msg = event.getMessage();

        if (handleConditionInput(player, builder, msg)) return;
        handleGeneralInput(player, builder, msg);
    }

    private boolean handleConditionInput(Player player, GateBuilderData builder, String msg) {
        var waiting = builder.getWaitingConditionType();
        if (waiting == null) return false;

        if (msg.equalsIgnoreCase("cancelar")) {
            builder.setWaitingConditionType(null);
            player.sendMessage(ChatColor.RED + "Entrada de condição cancelada.");
            reopenGUI(player, builder);
            return true;
        }

        try {
            SimpleGateCondition condition = switch (waiting) {
                case PERMISSION, WEATHER -> new SimpleGateCondition(waiting, msg);
                case HEALTH -> new SimpleGateCondition(waiting, Double.parseDouble(msg));
                case TIME -> {
                    String[] parts = msg.split("-");
                    if (parts.length != 2) throw new IllegalArgumentException();
                    yield new SimpleGateCondition(Long.parseLong(parts[0]), Long.parseLong(parts[1]));
                }
            };
            builder.addCondition(condition);
            player.sendMessage(ChatColor.GREEN + "Condição " + waiting.name() + " adicionada!");
        } catch (Exception e) {
            player.sendMessage(ChatColor.RED + "Valor inválido. Use o formato correto.");
        }

        builder.setWaitingConditionType(null);
        reopenGUI(player, builder);
        return true;
    }

    private void handleGeneralInput(Player player, GateBuilderData builder, String msg) {
        UUID id = player.getUniqueId();
        if (!builderManager.isWaitingForName(id)) return;

        try {
            if (builder.isAwaitingRadius()) {
                builder.setDetectionRadius(Double.parseDouble(msg));
                builder.setAwaitingRadius(false);
                player.sendMessage(ChatColor.GREEN + "Detection radius definido!");
            } else if (builder.isAwaitingCooldown()) {
                builder.setCooldownTicks(Long.parseLong(msg));
                builder.setAwaitingCooldown(false);
                player.sendMessage(ChatColor.GREEN + "Cooldown definido!");
            } else {
                builder.setName(msg);
                player.sendMessage(ChatColor.GREEN + "Nome definido como: " + ChatColor.YELLOW + msg);
            }
        } catch (NumberFormatException e) {
            player.sendMessage(ChatColor.RED + "Valor inválido. Tente novamente.");
            return;
        }

        builderManager.setWaitingForName(id, false);
        reopenGUI(player, builder);
    }

    // -------------------- GUI --------------------
    public void openBuilderGUI(Player player, GateBuilderData builder) {
        Inventory inv = Bukkit.createInventory(null, 27, "✧ Portal: " + builder.getId());

        inv.setItem(10, item(Material.PAPER, ChatColor.AQUA + "Tipo: " + ChatColor.YELLOW + builder.getType()));
        inv.setItem(12, pointItem(ChatColor.GREEN + "Ponto A", builder.getLocA(), Material.GREEN_WOOL));
        inv.setItem(14, pointItem(ChatColor.RED + "Ponto B", builder.getLocB(), Material.RED_WOOL));
        inv.setItem(16, item(Material.LIME_DYE, ChatColor.AQUA + "Detection Radius: " + ChatColor.YELLOW + builder.getDetectionRadius()));
        inv.setItem(20, conditionSummary(builder));
        inv.setItem(22, item(Material.NAME_TAG, ChatColor.AQUA + "Nome: " + ChatColor.YELLOW + builder.getName()));
        inv.setItem(24, item(Material.CLOCK, ChatColor.AQUA + "Cooldown: " + ChatColor.YELLOW + builder.getCooldownTicks() + " ticks"));
        inv.setItem(FINALIZE_SLOT, item(Material.EMERALD_BLOCK, ChatColor.GREEN + "Finalizar Portal"));

        player.openInventory(inv);
    }

    private static ItemStack item(Material mat, String name) {
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
            item.setItemMeta(meta);
        }
        return item;
    }

    private static ItemStack pointItem(String name, Location loc, Material mat) {
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
            meta.setLore(List.of(loc != null ?
                    String.format("X: %.0f Y: %.0f Z: %.0f", loc.getX(), loc.getY(), loc.getZ()) :
                    "Não definido"));
            item.setItemMeta(meta);
        }
        return item;
    }

    private static ItemStack conditionSummary(GateBuilderData builder) {
        ItemStack item = new ItemStack(Material.IRON_BARS);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.LIGHT_PURPLE + "Condições");
            List<String> lore = new ArrayList<>();
            if (builder.getConditions().isEmpty()) {
                lore.add(ChatColor.GRAY + "Nenhuma condição definida");
            } else {
                builder.getConditions().forEach(cond ->
                        lore.add(ChatColor.GREEN + "- " + cond.getDisplayText()));
            }
            lore.add("");
            lore.add(ChatColor.GRAY + "Clique para abrir GUI de condições");
            meta.setLore(lore);
            item.setItemMeta(meta);
        }
        return item;
    }

    // -------------------- Prompts --------------------
    private void promptPointSelection(Player player, GateBuilderData builder, boolean isPointA) {
        UUID id = player.getUniqueId();
        builderManager.setWaitingForBlockClick(id, true);
        builderManager.setWaitingForPointA(id, isPointA);
        player.closeInventory();
        player.sendMessage(ChatColor.YELLOW + "Clique em um bloco para definir " + (isPointA ? "Ponto A" : "Ponto B") + ".");
    }

    private void promptTextInput(Player player) {
        builderManager.setWaitingForName(player.getUniqueId(), true);
        player.closeInventory();
        player.sendMessage(ChatColor.AQUA + "Digite o " + "nome do portal" + " no chat:");
    }

    private void promptNumericInput(Player player, GateBuilderData builder, String type) {
        builderManager.setWaitingForName(player.getUniqueId(), true);
        player.closeInventory();

        switch (type.toLowerCase()) {
            case "detection" -> {
                builder.setAwaitingRadius(true);
                player.sendMessage(ChatColor.AQUA + "Digite o detection radius do portal no chat:");
            }
            case "cooldown" -> {
                builder.setAwaitingCooldown(true);
                player.sendMessage(ChatColor.AQUA + "Digite o cooldown do portal (em ticks) no chat:");
            }
        }
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

    private void reopenGUI(Player player, GateBuilderData builder) {
        Bukkit.getScheduler().runTask(getInstance(), () -> openBuilderGUI(player, builder));
    }

    private void openTypeSelection(Player player) {
        player.sendMessage(ChatColor.YELLOW + "Seleção de tipo ainda não implementada.");
    }

    public void openConditionsGUI(Player player, GateBuilderData builder) {
        new ConditionGUI(builder, this).openMain(player);
    }
}
