package me.erik.kgates.builder;

import me.erik.kgates.conditions.ConditionGUI;
import me.erik.kgates.conditions.SimpleGateCondition;
import me.erik.kgates.manager.GateData;
import me.erik.kgates.manager.GateManager;
import org.bukkit.*;
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

import java.util.*;
import java.util.Objects;
import java.util.UUID;

import static me.erik.kgates.KGates.getInstance;

public record BuilderGUIListener(GateBuilderManager builderManager, GateManager gateManager) implements Listener {

    private static final int FINALIZE_SLOT = 26;

    // -------------------- OPEN BUILDER FOR EDIT --------------------
    public void openBuilderForEdit(Player player, GateData gate) {
        GateBuilderData builder = new GateBuilderData(player.getUniqueId(), gate.getId());
        builder.setType(String.valueOf(gate.getType()));
        builder.setLocA(gate.getLoc1());
        builder.setLocB(gate.getLoc2());
        builder.setCooldownTicks(gate.getCooldownTicks());
        builder.setDetectionRadius(gate.getDetectionRadius());
        builder.setName(gate.getId());
        builder.getCommands().addAll(gate.getCommands());
        builder.getConditions().addAll(gate.getConditions());

        // Partículas e sons como enums
        builder.setAmbientParticle(gate.getAmbientParticle());
        builder.setActivationParticle(gate.getActivationParticle());
        builder.setAmbientSound(gate.getAmbientSound());
        builder.setActivationSound(gate.getActivationSound());

        builderManager.startBuilding(builder);
        openBuilderGUI(player, builder);
    }

    // -------------------- INVENTORY CLICK --------------------
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        String title = event.getView().getTitle();
        if (event.getClickedInventory() == null) return;

        event.setCancelled(true);
        ItemStack clicked = event.getCurrentItem();
        if (clicked == null || !clicked.hasItemMeta()) return;

        if (title.equalsIgnoreCase("✦ Browse Portals")) {
            handleBrowseClick(player, clicked);
        } else if (title.equalsIgnoreCase("✦ Select Portal Type")) {
            handleTypeClick(player, event.getSlot());
        } else if (title.startsWith("⚡ Commands:")) {
            handleCommandGUIClick(player, event.getRawSlot());
        } else if (title.startsWith("✧ Portal: ")) {
            GateBuilderData builder = builderManager.getBuilder(player.getUniqueId());
            if (builder == null) return;
            handleBuilderGUIClick(player, builder, event.getRawSlot());
        }
    }

    private void handleBrowseClick(Player player, ItemStack clicked) {
        String portalName = ChatColor.stripColor(Objects.requireNonNull(clicked.getItemMeta()).getDisplayName());
        GateData gate = gateManager.getGate(portalName);
        if (gate == null) {
            player.sendMessage(ChatColor.RED + "Could not find this portal.");
            return;
        }
        player.closeInventory();
        openBuilderForEdit(player, gate);
    }

    private void handleTypeClick(Player player, int slot) {
        GateBuilderData builder = builderManager.getBuilder(player.getUniqueId());
        if (builder == null) return;

        if (slot == 3) {
            builder.setType("TWO_WAY");
            player.sendMessage(ChatColor.GREEN + "Portal type set to: Two-Way (↔)");
        } else if (slot == 5) {
            builder.setType("ONE_WAY");
            player.sendMessage(ChatColor.GREEN + "Portal type set to: One-Way (→)");
        }

        reopenGUI(player, builder);
    }

    private void handleBuilderGUIClick(Player player, GateBuilderData builder, int slot) {
        switch (slot) {
            case 10 -> openTypeSelection(player);
            case 12 -> promptPointSelection(player, builder, true);
            case 14 -> promptPointSelection(player, builder, false);
            case 16 -> promptNumericInput(player, builder, "detection");
            case 18 -> openCommandsGUI(player, builder);
            case 20 -> openConditionsGUI(player, builder);
            case 22 -> promptTextInput(player);
            case 24 -> promptNumericInput(player, builder, "cooldown");
            case 28 -> promptParticleSelection(player, builder, true);  // ambiente
            case 29 -> promptParticleSelection(player, builder, false); // ativação
            case 30 -> promptSoundSelection(player, builder, true);     // ambiente
            case 31 -> promptSoundSelection(player, builder, false);    // ativação
            case FINALIZE_SLOT -> finalizePortal(player, builder);
        }
    }

    // -------------------- BLOCK CLICK --------------------
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
        if (builderManager.isWaitingForPointA(id)) {
            builder.setLocA(loc);
            player.sendMessage(ChatColor.GREEN + "Point A set!");
        } else {
            builder.setLocB(loc);
            player.sendMessage(ChatColor.RED + "Point B set!");
        }

        builderManager.setWaitingForBlockClick(id, false);
        reopenGUI(player, builder);
    }

    // -------------------- CHAT INPUT --------------------
    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        GateBuilderData builder = builderManager.getBuilder(player.getUniqueId());
        if (builder == null) return;

        event.setCancelled(true);
        String msg = event.getMessage().trim();

        if (handleConditionInput(player, builder, msg)) return;
        handleGeneralInput(player, builder, msg);

        if (builder.isAwaitingCommandInput()) handleCommandAdd(player, builder, msg);
        else if (builder.isAwaitingCommandRemoval()) handleCommandRemove(player, builder, msg);
    }

    private boolean handleConditionInput(Player player, GateBuilderData builder, String msg) {
        var waiting = builder.getWaitingConditionType();
        if (waiting == null) return false;

        if (msg.equalsIgnoreCase("cancel")) {
            builder.setWaitingConditionType(null);
            player.sendMessage(ChatColor.RED + "Condition input canceled.");
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
            player.sendMessage(ChatColor.GREEN + "Condition " + waiting.name() + " added!");
        } catch (Exception e) {
            player.sendMessage(ChatColor.RED + "Invalid value. Use the correct format.");
        }

        builder.setWaitingConditionType(null);
        reopenGUI(player, builder);
        return true;
    }

    private void handleCommandAdd(Player player, GateBuilderData builder, String msg) {
        if (msg.equalsIgnoreCase("cancel")) {
            builder.setAwaitingCommandInput(false);
            player.sendMessage(ChatColor.RED + "Command add canceled.");
        } else {
            builder.addCommand(msg);
            builder.setAwaitingCommandInput(false);
            player.sendMessage(ChatColor.GREEN + "Command added: " + ChatColor.YELLOW + "/" + msg);
        }
        reopenGUI(player, builder);
    }

    private void handleCommandRemove(Player player, GateBuilderData builder, String msg) {
        if (msg.equalsIgnoreCase("cancel")) {
            builder.setAwaitingCommandRemoval(false);
            player.sendMessage(ChatColor.RED + "Command removal canceled.");
        } else {
            try {
                int index = Integer.parseInt(msg) - 1;
                List<String> current = builder.getCommands();
                if (index < 0 || index >= current.size()) player.sendMessage(ChatColor.RED + "Invalid index.");
                else {
                    String removed = current.get(index);
                    builder.removeCommand(index);
                    player.sendMessage(ChatColor.GREEN + "Removed command: " + ChatColor.YELLOW + "/" + removed);
                }
                builder.setAwaitingCommandRemoval(false);
            } catch (NumberFormatException e) {
                player.sendMessage(ChatColor.RED + "Type a valid number.");
            }
        }
        reopenGUI(player, builder);
    }

    private void handleCommandGUIClick(Player player, int slot) {
        GateBuilderData builder = builderManager.getBuilder(player.getUniqueId());
        if (builder == null) return;

        switch (slot) {
            case 11 -> {
                player.closeInventory();
                player.sendMessage(ChatColor.AQUA + "Type the command to add (without /). Type 'cancel' to abort.");
                builder.setAwaitingCommandInput(true);
            }
            case 13 -> {
                player.closeInventory();
                player.sendMessage(ChatColor.AQUA + "Type the command number to remove. Type 'cancel' to abort.");
                builder.setAwaitingCommandRemoval(true);
            }
            case 15 -> {
                player.sendMessage(ChatColor.GOLD + "Commands for this portal:");
                List<String> cmds = builder.getCommands();
                if (cmds.isEmpty()) player.sendMessage(ChatColor.GRAY + "No commands added.");
                else cmds.forEach(player::sendMessage);
            }
            case 26 -> reopenGUI(player, builder);
        }
    }

    // -------------------- GENERAL CHAT INPUT --------------------
    private void handleGeneralInput(Player player, GateBuilderData builder, String msg) {
        UUID id = player.getUniqueId();
        if (!builderManager.isWaitingForName(id)) return;

        try {
            if (builder.isAwaitingRadius()) {
                builder.setDetectionRadius(Double.parseDouble(msg));
                builder.setAwaitingRadius(false);
                player.sendMessage(ChatColor.GREEN + "Detection radius set!");
            } else if (builder.isAwaitingCooldown()) {
                builder.setCooldownTicks(Long.parseLong(msg));
                builder.setAwaitingCooldown(false);
                player.sendMessage(ChatColor.GREEN + "Cooldown set!");
            } else if (builder.isAwaitingParticleInput()) {
                Particle p = Particle.valueOf(msg.toUpperCase());
                if (builder.isSettingAmbient()) builder.setAmbientParticle(p);
                else builder.setActivationParticle(p);
                player.sendMessage(ChatColor.GREEN + "Particle set to: " + ChatColor.YELLOW + p.name());
                builder.setAwaitingParticleInput(false);
            } else if (builder.isAwaitingSoundInput()) {
                Sound s = Sound.valueOf(msg.toUpperCase());
                if (builder.isSettingAmbient()) builder.setAmbientSound(s);
                else builder.setActivationSound(s);
                player.sendMessage(ChatColor.GREEN + "Sound set to: " + ChatColor.YELLOW + s);
                builder.setAwaitingSoundInput(false);
            } else {
                builder.setName(msg);
                player.sendMessage(ChatColor.GREEN + "Portal name set to: " + ChatColor.YELLOW + msg);
            }
        } catch (Exception e) {
            player.sendMessage(ChatColor.RED + "Invalid input. Please try again.");
        }

        builderManager.setWaitingForName(id, false);
        reopenGUI(player, builder);
    }

    // -------------------- GUI --------------------
    public void openBuilderGUI(Player player, GateBuilderData builder) {
        Inventory inv = Bukkit.createInventory(null, 36, "✧ Portal: " + builder.getId());

        inv.setItem(10, item(Material.PAPER, ChatColor.AQUA + "Type: " + ChatColor.YELLOW + builder.getType()));
        inv.setItem(12, pointItem(ChatColor.GREEN + "Point A", builder.getLocA(), Material.GREEN_WOOL));
        inv.setItem(14, pointItem(ChatColor.RED + "Point B", builder.getLocB(), Material.RED_WOOL));
        inv.setItem(16, item(Material.LIME_DYE, ChatColor.AQUA + "Detection Radius: " + ChatColor.YELLOW + builder.getDetectionRadius()));
        inv.setItem(18, commandSummary(builder));
        inv.setItem(20, conditionSummary(builder));
        inv.setItem(22, item(Material.NAME_TAG, ChatColor.AQUA + "Name: " + ChatColor.YELLOW + builder.getName()));
        inv.setItem(24, item(Material.CLOCK, ChatColor.AQUA + "Cooldown: " + ChatColor.YELLOW + builder.getCooldownTicks() + " ticks"));

        // Particle & Sound
        inv.setItem(28, item(Material.BLAZE_POWDER, ChatColor.AQUA + "Ambient Particle: " + ChatColor.YELLOW + (builder.getAmbientParticle() != null ? builder.getAmbientParticle().name() : "NONE")));
        inv.setItem(29, item(Material.FIRE_CHARGE, ChatColor.AQUA + "Activation Particle: " + ChatColor.YELLOW + (builder.getActivationParticle() != null ? builder.getActivationParticle().name() : "NONE")));
        inv.setItem(30, item(Material.NOTE_BLOCK, ChatColor.AQUA + "Ambient Sound: " + ChatColor.YELLOW + (builder.getAmbientSound() != null ? builder.getAmbientSound() : "NONE")));
        inv.setItem(31, item(Material.GOLD_BLOCK, ChatColor.AQUA + "Activation Sound: " + ChatColor.YELLOW + (builder.getActivationSound() != null ? builder.getActivationSound() : "NONE")));

        inv.setItem(FINALIZE_SLOT, item(Material.EMERALD_BLOCK, ChatColor.GREEN + "Finalize Portal"));

        player.openInventory(inv);
    }

    public void openCommandsGUI(Player player, GateBuilderData builder) {
        Inventory inv = Bukkit.createInventory(null, 27, "⚡ Commands: " + builder.getId());
        inv.setItem(11, item(Material.GREEN_WOOL, ChatColor.GREEN + "Add Command"));
        inv.setItem(13, item(Material.RED_WOOL, ChatColor.RED + "Remove Command"));
        inv.setItem(15, item(Material.BOOK, ChatColor.AQUA + "List Commands"));
        inv.setItem(26, item(Material.ARROW, ChatColor.YELLOW + "Back"));
        player.openInventory(inv);
    }

    public void openConditionsGUI(Player player, GateBuilderData builder) {
        new ConditionGUI(builder, this).openMain(player);
    }

    // -------------------- PROMPTS --------------------
    private void promptPointSelection(Player player, GateBuilderData builder, boolean isPointA) {
        UUID id = player.getUniqueId();
        builderManager.setWaitingForBlockClick(id, true);
        builderManager.setWaitingForPointA(id, isPointA);
        player.closeInventory();
        player.sendMessage(ChatColor.YELLOW + "Click on a block to set " + (isPointA ? "Point A" : "Point B") + ".");
    }

    private void promptTextInput(Player player) {
        builderManager.setWaitingForName(player.getUniqueId(), true);
        player.closeInventory();
        player.sendMessage(ChatColor.AQUA + "Type the portal name in chat:");
    }

    private void promptNumericInput(Player player, GateBuilderData builder, String type) {
        builderManager.setWaitingForName(player.getUniqueId(), true);
        player.closeInventory();

        switch (type.toLowerCase()) {
            case "detection" -> {
                builder.setAwaitingRadius(true);
                player.sendMessage(ChatColor.AQUA + "Type the portal detection radius in chat:");
            }
            case "cooldown" -> {
                builder.setAwaitingCooldown(true);
                player.sendMessage(ChatColor.AQUA + "Type the portal cooldown (in ticks) in chat:");
            }
        }
    }

    private void promptParticleSelection(Player player, GateBuilderData builder, boolean ambient) {
        builderManager.setWaitingForName(player.getUniqueId(), true);
        builder.setSettingAmbient(ambient);
        builder.setAwaitingParticleInput(true);
        player.closeInventory();
        player.sendMessage(ChatColor.AQUA + "Type the particle name in chat (e.g., FLAME, HEART), or 'cancel' to abort:");
    }

    private void promptSoundSelection(Player player, GateBuilderData builder, boolean ambient) {
        builderManager.setWaitingForName(player.getUniqueId(), true);
        builder.setSettingAmbient(ambient);
        builder.setAwaitingSoundInput(true);
        player.closeInventory();
        player.sendMessage(ChatColor.AQUA + "Type the sound name in chat (e.g., ENTITY_ENDERMAN_TELEPORT), or 'cancel' to abort:");
    }

    private void finalizePortal(Player player, GateBuilderData builder) {
        if (!builder.isComplete()) {
            player.sendMessage(ChatColor.RED + "The portal is not complete yet!");
            return;
        }
        gateManager.addGateFromBuilder(builder);
        builderManager.stopBuilding(player.getUniqueId());
        player.sendMessage(ChatColor.GREEN + "Portal '" + builder.getName() + "' successfully created!");
        player.closeInventory();
    }

    private void reopenGUI(Player player, GateBuilderData builder) {
        Bukkit.getScheduler().runTask(getInstance(), () -> openBuilderGUI(player, builder));
    }

    private void openTypeSelection(Player player) {
        Inventory inv = Bukkit.createInventory(null, 9, "✦ Select Portal Type");
        inv.setItem(3, item(Material.ENDER_PEARL, ChatColor.AQUA + "Two-Way (↔)"));
        inv.setItem(5, item(Material.ENDER_EYE, ChatColor.LIGHT_PURPLE + "One-Way (→)"));
        player.openInventory(inv);
    }

    // -------------------- ITEM HELPERS --------------------
    public static ItemStack item(Material mat, String name) {
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
            meta.setLore(List.of(loc != null ? String.format("X: %.0f Y: %.0f Z: %.0f", loc.getX(), loc.getY(), loc.getZ()) : "Not set"));
            item.setItemMeta(meta);
        }
        return item;
    }

    private static ItemStack conditionSummary(GateBuilderData builder) {
        ItemStack item = new ItemStack(Material.IRON_BARS);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.LIGHT_PURPLE + "Conditions");
            List<String> lore = new ArrayList<>();
            if (builder.getConditions().isEmpty()) lore.add(ChatColor.GRAY + "No conditions set");
            else {
                int i = 1;
                for (SimpleGateCondition cond : builder.getConditions())
                    lore.add(ChatColor.GREEN + "" + i++ + ". " + ChatColor.YELLOW + cond.getDisplayText());
            }
            lore.add("");
            lore.add(ChatColor.GRAY + "Click to open condition GUI");
            meta.setLore(lore);
            item.setItemMeta(meta);
        }
        return item;
    }

    private static ItemStack commandSummary(GateBuilderData builder) {
        ItemStack item = new ItemStack(Material.COMMAND_BLOCK);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.GOLD + "Commands");
            List<String> lore = new ArrayList<>();
            if (builder.getCommands().isEmpty()) lore.add(ChatColor.GRAY + "No commands set");
            else for (int i = 0; i < builder.getCommands().size(); i++)
                lore.add(ChatColor.GREEN + "" + (i + 1) + ". " + ChatColor.YELLOW + "/" + builder.getCommands().get(i));
            lore.add("");
            lore.add(ChatColor.GRAY + "Click to manage commands");
            meta.setLore(lore);
            item.setItemMeta(meta);
        }
        return item;
    }
}
