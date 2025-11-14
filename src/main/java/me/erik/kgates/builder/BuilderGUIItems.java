package me.erik.kgates.builder;

import me.erik.kgates.conditions.SimpleGateCondition;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public final class BuilderGUIItems {

    private BuilderGUIItems() {}

    public static ItemStack item(Material mat, String name, List<String> lore) {
        ItemStack it = new ItemStack(mat);
        ItemMeta meta = it.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
            if (lore != null) meta.setLore(lore);
            it.setItemMeta(meta);
        }
        return it;
    }

    private static ItemStack simple(Material mat, String name) {
        return item(mat, name, null);
    }

    public static ItemStack typeItem(GateBuilderData b) {
        return item(Material.PAPER,
                ChatColor.AQUA + "Type: " + ChatColor.YELLOW + b.getType(),
                null);
    }

    public static ItemStack pointA(GateBuilderData b) {
        return point("Point A", b.getLocA(), Material.GREEN_WOOL);
    }

    public static ItemStack pointB(GateBuilderData b) {
        return point("Point B", b.getLocB(), Material.RED_WOOL);
    }

    private static ItemStack point(String name, Location l, Material mat) {
        List<String> lore = new ArrayList<>();
        if (l == null) lore.add("Not set");
        else lore.add("X:" + l.getBlockX() + " Y:" + l.getBlockY() + " Z:" + l.getBlockZ());
        return item(mat, ChatColor.AQUA + name, lore);
    }

    public static ItemStack detectionItem(GateBuilderData b) {
        return simple(Material.LIME_DYE,
                ChatColor.AQUA + "Detection: " + ChatColor.YELLOW + b.getDetectionRadius());
    }

    public static ItemStack nameItem(GateBuilderData b) {
        return simple(Material.NAME_TAG,
                ChatColor.AQUA + "Name: " + ChatColor.YELLOW + b.getName());
    }

    public static ItemStack cooldownItem(GateBuilderData b) {
        return simple(Material.CLOCK,
                ChatColor.AQUA + "Cooldown: " + ChatColor.YELLOW + b.getCooldownTicks());
    }

    public static ItemStack ambientParticle(GateBuilderData b) {
        String val = b.getAmbientParticle() == null ? "NONE" : b.getAmbientParticle().name();
        return simple(Material.BLAZE_POWDER, ChatColor.AQUA + "Ambient Particle: " + ChatColor.YELLOW + val);
    }

    public static ItemStack activationParticle(GateBuilderData b) {
        String val = b.getActivationParticle() == null ? "NONE" : b.getActivationParticle().name();
        return simple(Material.FIRE_CHARGE, ChatColor.AQUA + "Activation Particle: " + ChatColor.YELLOW + val);
    }

    public static ItemStack ambientSound(GateBuilderData b) {
        String val = b.getAmbientSound() == null ? "NONE" : b.getAmbientSound().toString();
        return simple(Material.NOTE_BLOCK, ChatColor.AQUA + "Ambient Sound: " + ChatColor.YELLOW + val);
    }

    public static ItemStack activationSound(GateBuilderData b) {
        String val = b.getActivationSound() == null ? "NONE" : b.getActivationSound().toString();
        return simple(Material.GOLD_BLOCK, ChatColor.AQUA + "Activation Sound: " + ChatColor.YELLOW + val);
    }

    public static ItemStack finalizeItem() {
        return simple(Material.EMERALD_BLOCK, ChatColor.GREEN + "Finalize Portal");
    }

    public static ItemStack addCommand() {
        return simple(Material.GREEN_WOOL, ChatColor.GREEN + "Add Command");
    }

    public static ItemStack removeCommand() {
        return simple(Material.RED_WOOL, ChatColor.RED + "Remove Command");
    }

    public static ItemStack listCommands() {
        return simple(Material.BOOK, ChatColor.AQUA + "List Commands");
    }

    public static ItemStack back() {
        return simple(Material.ARROW, ChatColor.YELLOW + "Back");
    }

    public static ItemStack twoWay() {
        return simple(Material.ENDER_PEARL, ChatColor.AQUA + "Two-Way (↔)");
    }

    public static ItemStack oneWay() {
        return simple(Material.ENDER_EYE, ChatColor.LIGHT_PURPLE + "One-Way (→)");
    }

    public static ItemStack commandSummary(GateBuilderData b) {
        List<String> lore = new ArrayList<>();

        if (b.getCommands().isEmpty()) lore.add(ChatColor.GRAY + "No commands");
        else {
            for (int i = 0; i < b.getCommands().size(); i++) {
                lore.add(ChatColor.GREEN + "" + (i + 1) + ". " + ChatColor.YELLOW + "/" + b.getCommands().get(i));
            }
        }

        lore.add("");
        lore.add(ChatColor.GRAY + "Click to manage");

        return item(Material.COMMAND_BLOCK, ChatColor.GOLD + "Commands", lore);
    }

    public static ItemStack conditionSummary(GateBuilderData b) {
        List<String> lore = new ArrayList<>();

        if (b.getConditions().isEmpty()) lore.add(ChatColor.GRAY + "No conditions");
        else {
            int i = 1;
            for (SimpleGateCondition c : b.getConditions()) {
                lore.add(ChatColor.GREEN + "" + i++ + ". " + ChatColor.YELLOW + c.getDisplayText());
            }
        }

        lore.add("");
        lore.add(ChatColor.GRAY + "Click to manage");

        return item(Material.IRON_BARS, ChatColor.LIGHT_PURPLE + "Conditions", lore);
    }
}
