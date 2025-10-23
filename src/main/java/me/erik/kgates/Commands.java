package me.erik.kgates;

import me.erik.kgates.builder.BuilderGUIListener;
import me.erik.kgates.builder.GateBuilderData;
import me.erik.kgates.builder.GateBuilderManager;
import me.erik.kgates.manager.GateData;
import me.erik.kgates.manager.GateManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

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
            sender.sendMessage(ChatColor.RED + "Apenas jogadores podem usar este comando!");
            return true;
        }
        if (!player.hasPermission("kgates.admin")) {
            player.sendMessage(ChatColor.RED + "Você não tem permissão para usar este comando!");
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

    // ---------------- CRIAÇÃO DE PORTAL ----------------

    private void handleCreate(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage(ChatColor.RED + "Uso: /kgate create <id>");
            return;
        }

        String id = args[1].toLowerCase();

        if (gateManager.getGate(id) != null) {
            player.sendMessage(ChatColor.RED + "Já existe um portal com o ID '" + id + "'!");
            return;
        }

        GateBuilderData builder = new GateBuilderData(player.getUniqueId(), id);
        builderManager.startBuilding(builder);

        player.sendMessage(ChatColor.GREEN + "Criação do portal '" + ChatColor.YELLOW + id + ChatColor.GREEN + "' iniciada!");
        guiListener.openBuilderGUI(player, builder);
    }


    // ---------------- REMOVER ----------------

    private void handleRemove(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage(ChatColor.RED + "Uso: /kgate remove <id>");
            return;
        }

        String id = args[1].toLowerCase();
        if (gateManager.getGate(id) == null) {
            player.sendMessage(ChatColor.RED + "Nenhum portal encontrado com o ID '" + id + "'.");
            return;
        }

        gateManager.removeGate(id);
        player.sendMessage(ChatColor.GREEN + "Portal '" + ChatColor.YELLOW + id + ChatColor.GREEN + "' removido com sucesso!");
    }

    // ---------------- EDITAR ----------------

    private void handleEdit(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage(ChatColor.RED + "Uso: /kgate edit <id>");
            return;
        }

        String id = args[1].toLowerCase();
        GateData gate = gateManager.getGate(id);

        if (gate == null) {
            player.sendMessage(ChatColor.RED + "Nenhum portal encontrado com o ID '" + id + "'.");
            return;
        }

        GateBuilderData builder = new GateBuilderData(player.getUniqueId(), id);
        builder.setType(gate.getType());
        builder.setLocA(gate.getLoc1());
        builder.setLocB(gate.getLoc2());
        builder.setCooldownTicks(gate.getCooldownTicks());
        builder.setDetectionRadius(gate.getDetectionRadius());

        builderManager.startBuilding(builder);

        player.sendMessage(ChatColor.AQUA + "Abrindo GUI para edição do portal: " + ChatColor.YELLOW + id);
        guiListener.openBuilderGUI(player, builder);
    }



    // ---------------- LISTAGEM ----------------

    private void handleBrowse(Player player) {
        player.sendMessage(ChatColor.GOLD + "Abrindo lista de portais...");
        // TODO: abrir GUI de listagem de portais
    }

    // ---------------- TELEPORTE ----------------

    private void handleGo(Player player, String[] args) {
        if (args.length < 3) {
            player.sendMessage(ChatColor.RED + "Uso: /kgate go <nome> <1/2>");
            return;
        }

        String name = args[1];
        String pointStr = args[2];

        if (!pointStr.equals("1") && !pointStr.equals("2")) {
            player.sendMessage(ChatColor.RED + "O ponto deve ser '1' ou '2'.");
            return;
        }

        GateData gate = gateManager.getGate(name);
        if (gate == null) {
            player.sendMessage(ChatColor.RED + "Nenhum portal encontrado com o nome: " + name);
            return;
        }

        int point = Integer.parseInt(pointStr);

        player.sendMessage(ChatColor.GREEN + "Teleportado para o ponto " + ChatColor.YELLOW + point + ChatColor.GREEN + " do portal " + ChatColor.YELLOW + name + ChatColor.GREEN + "!");
    }


    // ---------------- USO ----------------

    private void sendUsage(Player player) {
        player.sendMessage(ChatColor.GOLD + "Comandos do KGates:");
        player.sendMessage(ChatColor.YELLOW + "/kgate create <id> " + ChatColor.GRAY + "- Cria um novo portal");
        player.sendMessage(ChatColor.YELLOW + "/kgate remove <id> " + ChatColor.GRAY + "- Remove um portal existente");
        player.sendMessage(ChatColor.YELLOW + "/kgate edit <id> " + ChatColor.GRAY + "- Edita um portal");
        player.sendMessage(ChatColor.YELLOW + "/kgate browse " + ChatColor.GRAY + "- Lista todos os portais");
        player.sendMessage(ChatColor.YELLOW + "/kgate go <id> <1/2> " + ChatColor.GRAY + "- Vai até um dos pontos do portal");
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
                        .map(GateData::getId) // transforma GateData em String
                        .forEach(completions::add);
            }
        } else if (args.length == 3 && args[0].equalsIgnoreCase("go")) {
            completions.addAll(Arrays.asList("1", "2"));
        }

        return completions;
    }
}
