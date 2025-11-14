package me.erik.kgates.builder;

import me.erik.kgates.conditions.SimpleGateCondition;
import me.erik.kgates.manager.GateData;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.data.type.Gate;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class GateBuilderData {

    private final UUID playerId;
    private final String id;

    private String name;
    private String type = "default";

    private Location locA;
    private Location locB;

    private double detectionRadius = 1.5;
    private long cooldownTicks = 20;

    // --- Particles ---
    private Particle ambientParticle = Particle.FLAME;
    private Particle activationParticle = Particle.FLAME;
    private int particleCount = 10;
    private double particleSpeed = 0.1;

    // --- Sounds ---
    private Sound ambientSound = Sound.ENTITY_ENDERMAN_TELEPORT;
    private Sound activationSound = Sound.ENTITY_ENDERMAN_TELEPORT;
    private float soundVolume = 1.0f;
    private float soundPitch = 1.0f;

    // --- Builder Input Flags (apenas estados, sem lógica) ---
    private boolean awaitingRadius = false;
    private boolean awaitingCooldown = false;

    private boolean awaitingCommandInput = false;
    private boolean awaitingCommandRemoval = false;

    private boolean awaitingParticleInput = false;
    private boolean awaitingSoundInput = false;

    private boolean settingAmbient = true;

    private boolean awaitingConditionInput = false;

    // --- Conditions & Commands ---
    private final List<SimpleGateCondition> conditions = new ArrayList<>();
    private final List<String> commands = new ArrayList<>();


    public GateBuilderData(UUID playerId, String id) {
        this.playerId = playerId;
        this.id = id;
    }

    // -------------------- Basic Info --------------------
    public UUID getPlayerId() { return playerId; }
    public String getId() { return id; }

    public String getName() { return name != null ? name : id; }
    public void setName(String name) { this.name = name; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public Location getLocA() { return locA; }
    public void setLocA(Location locA) { this.locA = locA; }

    public Location getLocB() { return locB; }
    public void setLocB(Location locB) { this.locB = locB; }

    public double getDetectionRadius() { return detectionRadius; }
    public void setDetectionRadius(double detectionRadius) { this.detectionRadius = detectionRadius; }

    public long getCooldownTicks() { return cooldownTicks; }
    public void setCooldownTicks(long cooldownTicks) { this.cooldownTicks = cooldownTicks; }

    // -------------------- Particles --------------------
    public Particle getAmbientParticle() { return ambientParticle; }
    public void setAmbientParticle(Particle ambientParticle) { this.ambientParticle = ambientParticle; }

    public Particle getActivationParticle() { return activationParticle; }
    public void setActivationParticle(Particle activationParticle) { this.activationParticle = activationParticle; }

    public int getParticleCount() { return particleCount; }
    public void setParticleCount(int particleCount) { this.particleCount = particleCount; }

    public double getParticleSpeed() { return particleSpeed; }
    public void setParticleSpeed(double particleSpeed) { this.particleSpeed = particleSpeed; }

    // -------------------- Sounds --------------------
    public Sound getAmbientSound() { return ambientSound; }
    public void setAmbientSound(Sound ambientSound) { this.ambientSound = ambientSound; }

    public Sound getActivationSound() { return activationSound; }
    public void setActivationSound(Sound activationSound) { this.activationSound = activationSound; }

    public float getSoundVolume() { return soundVolume; }
    public void setSoundVolume(float soundVolume) { this.soundVolume = soundVolume; }

    public float getSoundPitch() { return soundPitch; }
    public void setSoundPitch(float soundPitch) { this.soundPitch = soundPitch; }

    // -------------------- Flags --------------------
    public boolean isAwaitingRadius() { return awaitingRadius; }
    public void setAwaitingRadius(boolean awaitingRadius) { this.awaitingRadius = awaitingRadius; }

    public boolean isAwaitingCooldown() { return awaitingCooldown; }
    public void setAwaitingCooldown(boolean awaitingCooldown) { this.awaitingCooldown = awaitingCooldown; }

    public boolean isAwaitingCommandInput() { return awaitingCommandInput; }
    public void setAwaitingCommandInput(boolean awaitingCommandInput) { this.awaitingCommandInput = awaitingCommandInput; }

    public boolean isAwaitingCommandRemoval() { return awaitingCommandRemoval; }
    public void setAwaitingCommandRemoval(boolean awaitingCommandRemoval) { this.awaitingCommandRemoval = awaitingCommandRemoval; }

    public boolean isAwaitingParticleInput() { return awaitingParticleInput; }
    public void setAwaitingParticleInput(boolean awaitingParticleInput) { this.awaitingParticleInput = awaitingParticleInput; }

    public boolean isAwaitingSoundInput() { return awaitingSoundInput; }
    public void setAwaitingSoundInput(boolean awaitingSoundInput) { this.awaitingSoundInput = awaitingSoundInput; }

    public boolean isSettingAmbient() { return settingAmbient; }
    public void setSettingAmbient(boolean settingAmbient) { this.settingAmbient = settingAmbient; }

    public boolean isAwaitingConditionInput() { return awaitingConditionInput; }
    public void setAwaitingConditionInput(boolean awaitingConditionInput) { this.awaitingConditionInput = awaitingConditionInput; }


    public static GateBuilderData fromGate(UUID playerId, GateData gate) {
        GateBuilderData builder = new GateBuilderData(playerId, gate.getId());

        builder.setName(gate.getId());
        builder.setType(String.valueOf(gate.getType()));

        builder.setLocA(gate.getLoc1());
        builder.setLocB(gate.getLoc2());

        builder.setDetectionRadius(gate.getDetectionRadius());
        builder.setCooldownTicks(gate.getCooldownTicks());

        // Particles
        builder.setAmbientParticle(gate.getAmbientParticle());
        builder.setActivationParticle(gate.getActivationParticle());
        builder.setParticleCount(gate.getActivationParticleCount());
        builder.setParticleSpeed(gate.getActivationParticleSpeed());

        // Sounds
        builder.setAmbientSound(gate.getAmbientSound());
        builder.setActivationSound(gate.getActivationSound());
        builder.setSoundVolume(gate.getActivationSoundVolume());
        builder.setSoundPitch(gate.getActivationSoundPitch());

        // Conditions
        builder.getConditions().clear();
        builder.getConditions().addAll(gate.getConditions());

        // Commands
        builder.getCommands().clear();
        builder.getCommands().addAll(gate.getCommands());

        return builder;
    }
    public boolean isAwaitingAnyInput() {
        return awaitingRadius || awaitingCooldown || awaitingCommandInput ||
                awaitingCommandRemoval || awaitingParticleInput || awaitingSoundInput ||
                awaitingConditionInput;
    }

    public void clearAllAwaitingFlags() {
        awaitingRadius = false;
        awaitingCooldown = false;
        awaitingCommandInput = false;
        awaitingCommandRemoval = false;
        awaitingParticleInput = false;
        awaitingSoundInput = false;
        awaitingConditionInput = false;
    }


    // -------------------- Conditions --------------------
    public List<SimpleGateCondition> getConditions() { return conditions; }

    public void addCondition(SimpleGateCondition condition) {
        if (condition != null && condition.getExpression() != null && !condition.getExpression().isBlank()) {
            conditions.add(condition);
        }
    }

    public void removeCondition(int index) {
        if (index >= 0 && index < conditions.size()) {
            conditions.remove(index);
        }
    }

    public void clearConditions() { conditions.clear(); }

    // -------------------- Commands --------------------
    public List<String> getCommands() { return commands; }

    public void addCommand(String command) {
        if (command != null && !command.isBlank()) {
            commands.add(command);
        }
    }

    public void removeCommand(int index) {
        if (index >= 0 && index < commands.size()) {
            commands.remove(index);
        }
    }

    public void clearCommands() { commands.clear(); }

    // -------------------- Validation --------------------
    public boolean isComplete() {
        return locA != null && locB != null && type != null && !type.isBlank();
    }
}
