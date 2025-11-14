package me.erik.kgates.builder;

import me.erik.kgates.conditions.ConditionGUI;
import me.erik.kgates.manager.GateData;
import me.erik.kgates.manager.GateManager;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import static me.erik.kgates.KGates.getInstance;

public record BuilderGUI(GateBuilderManager builderManager, GateManager gateManager,
                         BuilderGUIListener builderListener) {

    private static final int FINALIZE_SLOT = 26;

    // ============================================================
    //                      EVENT HANDLERS
    // ============================================================

    public void handleInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (event.getClickedInventory() == null) return;
        if (event.getCurrentItem() == null || !event.getCurrentItem().hasItemMeta()) return;

        String title = event.getView().getTitle();

        switch (ChatColor.stripColor(title)) {

            case "✦ Browse Portals" -> handleBrowseClick(player, event.getCurrentItem());
            case "✦ Select Portal Type" -> handleTypeSelection(player, event.getSlot());

            default -> {
                if (title.startsWith("✧ Portal: ")) {
                    GateBuilderData builder = builderManager.getBuilder(player.getUniqueId());
                    if (builder != null) {
                        handlePortalEditorClick(player, builder, event.getRawSlot());
                    }
                }

                if (title.startsWith("⚡ Commands: ")) {
                    GateBuilderData builder = builderManager.getBuilder(player.getUniqueId());
                    if (builder != null) {
                        handleCommandClick(player, builder, event.getRawSlot());
                    }
                }
            }
        }
    }

    public void handleBlockClick(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        UUID uid = player.getUniqueId();

        if (!builderManager.isBuilding(uid)) return;
        if (!builderManager.isWaitingForBlockClick(uid)) return;
        if (event.getAction() != Action.LEFT_CLICK_BLOCK) return;

        event.setCancelled(true);

        GateBuilderData builder = builderManager.getBuilder(uid);
        if (builder == null) return;

        Location loc = Objects.requireNonNull(event.getClickedBlock()).getLocation();

        if (builderManager.isWaitingForPointA(uid)) {
            builder.setLocA(loc);
            player.sendMessage(ChatColor.GREEN + "Point A set!");
        } else {
            builder.setLocB(loc);
            player.sendMessage(ChatColor.RED + "Point B set!");
        }

        builderManager.setWaitingForBlockClick(uid, false);
        reopenGUI(player, builder);
    }

    // ============================================================
    //                       INVENTORY ACTIONS
    // ============================================================

    private void handleBrowseClick(Player player, ItemStack clicked) {
        String name = ChatColor.stripColor(Objects.requireNonNull(clicked.getItemMeta()).getDisplayName());
        GateData gate = gateManager.getGate(name);

        if (gate == null) {
            player.sendMessage(ChatColor.RED + "Portal not found.");
            return;
        }

        GateBuilderData builder = GateBuilderData.fromGate(player.getUniqueId(), gate);
        builderManager.startBuilding(builder);
        openPortalEditor(player, builder);
    }

    private void handleTypeSelection(Player player, int slot) {
        GateBuilderData builder = builderManager.getBuilder(player.getUniqueId());
        if (builder == null) return;

        if (slot == 3) {
            builder.setType("TWO_WAY");
            player.sendMessage(ChatColor.GREEN + "Portal set to Two-Way");
        } else if (slot == 5) {
            builder.setType("ONE_WAY");
            player.sendMessage(ChatColor.GREEN + "Portal set to One-Way");
        }

        reopenGUI(player, builder);
    }

    private void handlePortalEditorClick(Player player, GateBuilderData builder, int slot) {

        switch (slot) {
            case 10 -> openTypeSelector(player);
            case 12 -> setPointPrompt(player, true);
            case 14 -> setPointPrompt(player, false);
            case 16 -> startNumberPrompt(player, "radius");
            case 18 -> openCommandsGUI(player, builder);
            case 20 -> new ConditionGUI(builder).openMain(player);
            case 24 -> startNumberPrompt(player, "cooldown");
            case 28 -> startParticlePrompt(player, true);
            case 29 -> startParticlePrompt(player, false);
            case 30 -> startSoundPrompt(player, true);
            case 31 -> startSoundPrompt(player, false);
            case FINALIZE_SLOT -> finalizePortal(player, builder);
        }
    }

    private void handleCommandClick(Player player, GateBuilderData builder, int slot) {
        switch (slot) {
            case 11 -> {
                builder.setAwaitingCommandInput(true);
                player.closeInventory();
                player.sendMessage(ChatColor.AQUA + "Type the command to add:");
            }
            case 13 -> {
                builder.setAwaitingCommandRemoval(true);
                player.closeInventory();
                player.sendMessage(ChatColor.AQUA + "Type the command number to remove:");
            }
            case 15 -> {
                player.closeInventory();
                sendCommandList(player, builder);
                reopenGUI(player, builder);
            }
            case 26 -> openPortalEditor(player, builder);
        }
    }

    // ============================================================
    //                           GUI
    // ============================================================

    public static void openPortalEditor(Player player, GateBuilderData b) {
        Inventory inv = Bukkit.createInventory(null, 36, "✧ Portal: " + b.getId());

        inv.setItem(10, BuilderGUIItems.typeItem(b));
        inv.setItem(12, BuilderGUIItems.pointA(b));
        inv.setItem(14, BuilderGUIItems.pointB(b));
        inv.setItem(16, BuilderGUIItems.detectionItem(b));
        inv.setItem(18, BuilderGUIItems.commandSummary(b));
        inv.setItem(20, BuilderGUIItems.conditionSummary(b));
        inv.setItem(22, BuilderGUIItems.nameItem(b));
        inv.setItem(24, BuilderGUIItems.cooldownItem(b));

        inv.setItem(28, BuilderGUIItems.ambientParticle(b));
        inv.setItem(29, BuilderGUIItems.activationParticle(b));
        inv.setItem(30, BuilderGUIItems.ambientSound(b));
        inv.setItem(31, BuilderGUIItems.activationSound(b));

        inv.setItem(FINALIZE_SLOT, BuilderGUIItems.finalizeItem());
        player.openInventory(inv);
    }

    public void openCommandsGUI(Player player, GateBuilderData b) {
        Inventory inv = Bukkit.createInventory(null, 27, "⚡ Commands: " + b.getId());

        inv.setItem(11, BuilderGUIItems.addCommand());
        inv.setItem(13, BuilderGUIItems.removeCommand());
        inv.setItem(15, BuilderGUIItems.listCommands());
        inv.setItem(26, BuilderGUIItems.back());

        player.openInventory(inv);
    }

    private void openTypeSelector(Player p) {
        Inventory inv = Bukkit.createInventory(null, 9, "✦ Select Portal Type");
        inv.setItem(3, BuilderGUIItems.twoWay());
        inv.setItem(5, BuilderGUIItems.oneWay());
        p.openInventory(inv);
    }

    // ============================================================
    //                        PROMPTS
    // ============================================================

    private void setPointPrompt(Player p, boolean a) {
        UUID uid = p.getUniqueId();

        builderManager.setWaitingForBlockClick(uid, true);
        builderManager.setWaitingForPointA(uid, a);

        p.closeInventory();
        p.sendMessage(ChatColor.YELLOW + "Click a block to set " + (a ? "Point A" : "Point B"));
    }

    private void startNumberPrompt(Player p, String type) {
        builderManager.getInputHandler().beginNumberInput(p, type);
        p.closeInventory();
    }

    private void startParticlePrompt(Player p, boolean ambient) {
        builderManager.getInputHandler().beginParticleInput(p, ambient);
        p.closeInventory();
    }

    private void startSoundPrompt(Player p, boolean ambient) {
        builderManager.getInputHandler().beginSoundInput(p, ambient);
        p.closeInventory();
    }

    private void finalizePortal(Player p, GateBuilderData b) {
        if (!b.isComplete()) {
            p.sendMessage(ChatColor.RED + "Portal isn't complete!");
            return;
        }

        gateManager.addGateFromBuilder(b);
        builderManager.stopBuilding(p.getUniqueId());
        p.sendMessage(ChatColor.GREEN + "Portal created!");
        p.closeInventory();
    }

    private void sendCommandList(Player p, GateBuilderData b) {
        if (b.getCommands().isEmpty()) {
            p.sendMessage(ChatColor.GRAY + "No commands.");
            return;
        }
        p.sendMessage(ChatColor.GOLD + "Commands:");

        for (int i = 0; i < b.getCommands().size(); i++) {
            p.sendMessage((i + 1) + ". /" + ChatColor.YELLOW + b.getCommands().get(i));
        }
    }

    private void reopenGUI(Player p, GateBuilderData b) {
        Bukkit.getScheduler().runTask(getInstance(), () -> openPortalEditor(p, b));
    }
}
