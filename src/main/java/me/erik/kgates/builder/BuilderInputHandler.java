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
            case "size_x" -> data.setAwaitingSizeX(true);
            case "size_y" -> data.setAwaitingSizeY(true);
            case "size_z" -> data.setAwaitingSizeZ(true);
            default -> {
                p.sendMessage("§cTipo de número desconhecido: " + type);
                return;
            }
        }

        p.sendMessage("§eDigite um número no chat.");
    }

    public void beginParticleInput(Player p, GateBuilderData.ParticleStage stage) {
        GateBuilderManager manager = getManager();
        GateBuilderData data = manager.getBuilder(p.getUniqueId());
        if (data == null) return;

        data.setAwaitingParticleInput(true);
        data.setParticleStage(stage);

        p.sendMessage("§eDigite o nome da partícula.");
    }

    public void beginParticleNumberInput(Player p, GateBuilderData.ParticleStage stage, boolean count) {
        GateBuilderData data = getManager().getBuilder(p.getUniqueId());
        if (data == null) return;
        data.setParticleStage(stage);
        data.setAwaitingParticleCount(count);
        data.setAwaitingParticleSpeed(!count);
        p.sendMessage(count
                ? "§eDigite a quantidade de partículas (0 a 1000)."
                : "§eDigite a velocidade das partículas (0 a 10)." );
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
        if (data.isAwaitingSizeX()) return handlePortalSize(player, data, msg, 'x');
        if (data.isAwaitingSizeY()) return handlePortalSize(player, data, msg, 'y');
        if (data.isAwaitingSizeZ()) return handlePortalSize(player, data, msg, 'z');
        if (data.isAwaitingCommandInput()) return handleAddCommand(player, data, msg);
        if (data.isAwaitingCommandRemoval()) return handleRemoveCommand(player, data, msg);
        if (data.isAwaitingParticleInput()) return handleParticle(player, data, msg);
        if (data.isAwaitingParticleCount()) return handleParticleCount(player, data, msg);
        if (data.isAwaitingParticleSpeed()) return handleParticleSpeed(player, data, msg);
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
            int index = Integer.parseInt(msg) - 1;
            if (index < 0 || index >= data.getCommands().size()) throw new NumberFormatException();
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
            switch (data.getParticleStage()) {
                case AMBIENT -> data.setAmbientParticle(p);
                case ENTRY -> data.setEntryParticle(p);
                case EXIT -> data.setExitParticle(p);
            }

            player.sendMessage("§aPartícula definida: §f" + p);
        } catch (IllegalArgumentException e) {
            player.sendMessage("§cPartícula inválida.");
        }
        data.setAwaitingParticleInput(false);
        return true;
    }

    private static boolean handlePortalSize(Player player, GateBuilderData data, String msg, char axis) {
        try {
            double value = Double.parseDouble(msg);
            if (!Double.isFinite(value) || value < 0.5 || value > 128.0) throw new NumberFormatException();
            if (axis == 'x') data.setSizeX(value);
            else if (axis == 'y') data.setSizeY(value);
            else data.setSizeZ(value);
            player.sendMessage("§aTamanho " + Character.toUpperCase(axis) + " definido para §f" + value);
        } catch (NumberFormatException exception) {
            player.sendMessage("§cUse um tamanho entre 0.5 e 128 blocos.");
        }
        if (axis == 'x') data.setAwaitingSizeX(false);
        else if (axis == 'y') data.setAwaitingSizeY(false);
        else data.setAwaitingSizeZ(false);
        return true;
    }

    private static boolean handleParticleCount(Player player, GateBuilderData data, String msg) {
        try {
            int value = Integer.parseInt(msg);
            if (value < 0 || value > 1000) throw new NumberFormatException();
            switch (data.getParticleStage()) {
                case AMBIENT -> data.setAmbientParticleCount(value);
                case ENTRY -> data.setEntryParticleCount(value);
                case EXIT -> data.setExitParticleCount(value);
            }
            player.sendMessage("§aQuantidade definida: §f" + value);
        } catch (NumberFormatException exception) {
            player.sendMessage("§cUse um número inteiro entre 0 e 1000.");
        }
        data.setAwaitingParticleCount(false);
        return true;
    }

    private static boolean handleParticleSpeed(Player player, GateBuilderData data, String msg) {
        try {
            double value = Double.parseDouble(msg);
            if (!Double.isFinite(value) || value < 0 || value > 10) throw new NumberFormatException();
            switch (data.getParticleStage()) {
                case AMBIENT -> data.setAmbientParticleSpeed(value);
                case ENTRY -> data.setEntryParticleSpeed(value);
                case EXIT -> data.setExitParticleSpeed(value);
            }
            player.sendMessage("§aVelocidade definida: §f" + value);
        } catch (NumberFormatException exception) {
            player.sendMessage("§cUse um número entre 0 e 10.");
        }
        data.setAwaitingParticleSpeed(false);
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
