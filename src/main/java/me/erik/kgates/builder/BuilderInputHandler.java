package me.erik.kgates.builder;

import me.erik.kgates.KGates;
import me.erik.kgates.conditions.SimpleGateCondition;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class BuilderInputHandler {

    // ============================
    // MÉTODOS beginXXX()
    // ============================

    public void beginNumberInput(Player p, String type) {
        GateBuilderManager manager = getManager();
        GateBuilderData data = manager.getBuilder(p.getUniqueId());
        if (data == null) return;

        switch (type.toLowerCase()) {
            case "radius" -> data.setAwaitingRadius(true);
            case "cooldown" -> data.setAwaitingCooldown(true);
            default -> {
                p.sendMessage("§cTipo de número desconhecido: " + type);
                return;
            }
        }

        p.sendMessage("§eDigite um número no chat.");
    }

    public void beginParticleInput(Player p, boolean ambient) {
        GateBuilderManager manager = getManager();
        GateBuilderData data = manager.getBuilder(p.getUniqueId());
        if (data == null) return;

        data.setAwaitingParticleInput(true);
        data.setSettingAmbient(ambient);

        p.sendMessage("§eDigite o nome da partícula.");
    }

    public void beginSoundInput(Player p, boolean ambient) {
        GateBuilderManager manager = getManager();
        GateBuilderData data = manager.getBuilder(p.getUniqueId());
        if (data == null) return;

        data.setAwaitingSoundInput(true);
        data.setSettingAmbient(ambient);

        p.sendMessage("§eDigite o nome do som.");
    }

    // ---------------------------------------------------------
    // (se quiser posso trocar isso por um singleton)
    private GateBuilderManager getManager() {
        return KGates.getBuilderManager();
    }
    // ---------------------------------------------------------

    // =========================================================
    //               HANDLER CENTRAL DE INPUT
    // =========================================================

    public static boolean handle(Player player, GateBuilderData data, String msg) {

        if (data.isAwaitingRadius()) return handleRadius(player, data, msg);
        if (data.isAwaitingCooldown()) return handleCooldown(player, data, msg);
        if (data.isAwaitingCommandInput()) return handleAddCommand(player, data, msg);
        if (data.isAwaitingCommandRemoval()) return handleRemoveCommand(player, data, msg);
        if (data.isAwaitingParticleInput()) return handleParticle(player, data, msg);
        if (data.isAwaitingSoundInput()) return handleSound(player, data, msg);
        if (data.isAwaitingConditionInput()) return handleCondition(player, data, msg);

        return false;
    }

    // ---------------- HANDLERS (iguais aos seus) ----------------

    private static boolean handleRadius(Player player, GateBuilderData data, String msg) {
        try {
            double radius = Double.parseDouble(msg);
            data.setDetectionRadius(radius);
            player.sendMessage("§aRaio definido para: §f" + radius);
        } catch (NumberFormatException e) {
            player.sendMessage("§cValor inválido. Digite um número.");
        }
        data.setAwaitingRadius(false);
        return true;
    }

    private static boolean handleCooldown(Player player, GateBuilderData data, String msg) {
        try {
            long ticks = Long.parseLong(msg);
            data.setCooldownTicks(ticks);
            player.sendMessage("§aCooldown definido para: §f" + ticks + " ticks");
        } catch (NumberFormatException e) {
            player.sendMessage("§cValor inválido. Digite um número inteiro.");
        }
        data.setAwaitingCooldown(false);
        return true;
    }

    public static boolean handleAddCommand(Player player, GateBuilderData data, String msg) {
        data.addCommand(msg);
        player.sendMessage("§aComando adicionado!");
        data.setAwaitingCommandInput(false);
        return true;
    }

    private static boolean handleRemoveCommand(Player player, GateBuilderData data, String msg) {
        try {
            int index = Integer.parseInt(msg);
            data.removeCommand(index);
            player.sendMessage("§eComando removido.");
        } catch (Exception e) {
            player.sendMessage("§cÍndice inválido.");
        }
        data.setAwaitingCommandRemoval(false);
        return true;
    }

    private static boolean handleParticle(Player player, GateBuilderData data, String msg) {
        try {
            Particle p = Particle.valueOf(msg.toUpperCase());
            if (data.isSettingAmbient()) data.setAmbientParticle(p);
            else data.setActivationParticle(p);

            player.sendMessage("§aPartícula definida: §f" + p);
        } catch (IllegalArgumentException e) {
            player.sendMessage("§cPartícula inválida.");
        }
        data.setAwaitingParticleInput(false);
        return true;
    }

    private static boolean handleSound(Player player, GateBuilderData data, String msg) {
        try {
            Sound s = Sound.valueOf(msg.toUpperCase());
            if (data.isSettingAmbient()) data.setAmbientSound(s);
            else data.setActivationSound(s);

            player.sendMessage("§aSom definido: §f" + s);
        } catch (IllegalArgumentException e) {
            player.sendMessage("§cSom inválido.");
        }
        data.setAwaitingSoundInput(false);
        return true;
    }

    private static boolean handleCondition(Player player, GateBuilderData data, String msg) {
        data.addCondition(new SimpleGateCondition(msg));
        player.sendMessage("§aCondição adicionada.");
        data.setAwaitingConditionInput(false);
        return true;
    }
}
