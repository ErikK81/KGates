package me.erik.kgates;

import me.erik.kgates.builder.BuilderGUIListener;
import me.erik.kgates.builder.GateBuilderData;
import me.erik.kgates.builder.GateBuilderManager;
import me.erik.kgates.manager.GateData;
import me.erik.kgates.manager.GateManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Commands implements CommandExecutor, TabCompleter {

    private final GateManager gateManager;
    private final GateBuilderManager builderManager;
    private final BuilderGUIListener guiListener;

    private final List<String> subCommands = Arrays.asList("create", "remove", "edit", "browse", "go");

    public Commands(GateManager gateManager, GateBuilderManager builderManager, BuilderGUIListener guiListener) {
        this.gateManager = gateManager;
        this.builderManager = builderManager;
        this.guiListener = guiListener;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this command!");
            return true;
        }
        if (!player.hasPermission("kgates.admin")) {
            player.sendMessage(ChatColor.RED + "You do not have permission to use this command!");
            return true;
        }

        if (args.length == 0) {
            sendUsage(player);
            return true;
        }

        String sub = args[0].toLowerCase();
        switch (sub) {
            case "create" -> handleCreate(player, args);
            case "remove" -> handleRemove(player, args);
            case "edit" -> handleEdit(player, args);
            case "browse" -> handleBrowse(player);
            case "go" -> handleGo(player, args);
            default -> sendUsage(player);
        }

        return true;
    }

    // ---------------- CREATE GATE ----------------

    private void handleCreate(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage(ChatColor.RED + "Usage: /kgate create <id>");
            return;
        }

        String id = args[1].toLowerCase();

        if (gateManager.getGate(id) != null) {
            player.sendMessage(ChatColor.RED + "A gate with ID '" + id + "' already exists!");
            return;
        }

        GateBuilderData builder = new GateBuilderData(player.getUniqueId(), id);
        builderManager.startBuilding(builder);

        player.sendMessage(ChatColor.GREEN + "Gate '" + ChatColor.YELLOW + id + ChatColor.GREEN + "' creation started!");
        guiListener.openBuilderGUI(player, builder);
    }

    // ---------------- REMOVE ----------------

    private void handleRemove(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage(ChatColor.RED + "Usage: /kgate remove <id>");
            return;
        }

        String id = args[1].toLowerCase();
        if (gateManager.getGate(id) == null) {
            player.sendMessage(ChatColor.RED + "No gate found with ID '" + id + "'.");
            return;
        }

        gateManager.removeGate(id);
        player.sendMessage(ChatColor.GREEN + "Gate '" + ChatColor.YELLOW + id + ChatColor.GREEN + "' removed successfully!");
    }

    // ---------------- EDIT ----------------

    private void handleEdit(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage(ChatColor.RED + "Usage: /kgate edit <id>");
            return;
        }
        if (!builderManager.startEditing(args[1])) {
            player.sendMessage(ChatColor.RED + "This portal is currently being edited by someone else.");
            return;
        }

        String id = args[1].toLowerCase();
        GateData gate = gateManager.getGate(id);

        if (gate == null) {
            player.sendMessage(ChatColor.RED + "No gate found with ID '" + id + "'.");
            return;
        }

        player.sendMessage(ChatColor.AQUA + "Opening GUI to edit gate: " + ChatColor.YELLOW + id);
        guiListener.openBuilderForEdit(player, gate);
    }

    // ---------------- BROWSE ----------------

    private void handleBrowse(Player player) {
        List<GateData> gates = new ArrayList<>(gateManager.getAllGates());
        if (gates.isEmpty()) {
            player.sendMessage(ChatColor.RED + "No gates available.");
            return;
        }

        int size = ((gates.size() - 1) / 9 + 1) * 9; // múltiplo de 9
        Inventory inv = Bukkit.createInventory(null, size, "✦ Browse Portals");

        for (int i = 0; i < gates.size(); i++) {
            GateData gate = gates.get(i);
            inv.setItem(i, BuilderGUIListener.item(Material.PAPER, ChatColor.AQUA + gate.getId()));
        }

        player.openInventory(inv);
    }

    // ---------------- TELEPORT ----------------

    private void handleGo(Player player, String[] args) {
        if (args.length < 3) {
            player.sendMessage(ChatColor.RED + "Usage: /kgate go <id> <1/2>");
            return;
        }

        String name = args[1].toLowerCase();
        String pointStr = args[2];

        if (!pointStr.equals("1") && !pointStr.equals("2")) {
            player.sendMessage(ChatColor.RED + "Point must be '1' or '2'.");
            return;
        }

        GateData gate = gateManager.getGate(name);
        if (gate == null) {
            player.sendMessage(ChatColor.RED + "No gate found with ID: " + name);
            return;
        }

        int point = Integer.parseInt(pointStr);
        Location target = (point == 1) ? gate.getLoc1() : gate.getLoc2();

        if (target == null) {
            player.sendMessage(ChatColor.RED + "Point " + point + " is not set for this gate.");
            return;
        }

        player.teleport(target);
        player.sendMessage(ChatColor.GREEN + "Teleported to point " + ChatColor.YELLOW + point +
                ChatColor.GREEN + " of gate " + ChatColor.YELLOW + name + ChatColor.GREEN + "!");
    }

        // ---------------- USAGE ----------------

    private void sendUsage(Player player) {
        player.sendMessage(ChatColor.GOLD + "KGates Commands:");
        player.sendMessage(ChatColor.YELLOW + "/kgate create <id> " + ChatColor.GRAY + "- Create a new gate");
        player.sendMessage(ChatColor.YELLOW + "/kgate remove <id> " + ChatColor.GRAY + "- Remove an existing gate");
        player.sendMessage(ChatColor.YELLOW + "/kgate edit <id> " + ChatColor.GRAY + "- Edit a gate");
        player.sendMessage(ChatColor.YELLOW + "/kgate browse " + ChatColor.GRAY + "- List all gates");
        player.sendMessage(ChatColor.YELLOW + "/kgate go <id> <1/2> " + ChatColor.GRAY + "- Teleport to a gate point");
    }

    // ---------------- TAB COMPLETE ----------------

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            for (String sub : subCommands) {
                if (sub.startsWith(args[0].toLowerCase())) {
                    completions.add(sub);
                }
            }
        } else if (args.length == 2) {
            String sub = args[0].toLowerCase();
            if (sub.equals("remove") || sub.equals("edit") || sub.equals("go")) {
                gateManager.getAllGates().stream()
                        .map(GateData::getId)
                        .forEach(completions::add);
            }
        } else if (args.length == 3 && args[0].equalsIgnoreCase("go")) {
            completions.addAll(Arrays.asList("1", "2"));
        }

        return completions;
    }
}
