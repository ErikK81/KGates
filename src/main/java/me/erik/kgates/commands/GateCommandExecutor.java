package me.erik.kgates.commands;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class GateCommandExecutor {

    public static void execute(Player player, String command) {
        if (command == null || command.isEmpty()) return;

        String parsed = command.replace("%player%", player.getName());

        if (parsed.startsWith("console:")) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), parsed.substring("console:".length()).trim());
        } else {
            player.performCommand(parsed);
        }
    }
}
