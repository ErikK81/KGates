package me.erik.kgates.builder;

import me.erik.kgates.conditions.SimpleGateCondition;
import me.erik.kgates.manager.GateData;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class GateBuilderData {

    public enum ParticleStage { AMBIENT, ENTRY, EXIT }

    private final UUID playerId;
    private final String id;

    private String name;
    private String type = "TWO_WAY";

    private Location locA;
    private Location locB;

    private double detectionRadius = 1.5;
    private GateData.PortalShape shape = GateData.PortalShape.SPHERE;
    private double sizeX = 3.0;
    private double sizeY = 3.0;
    private double sizeZ = 3.0;
    private long cooldownTicks = 20;

    // --- Particles ---
    private Particle ambientParticle = Particle.FLAME;
    private Particle entryParticle = Particle.FLAME;
    private Particle exitParticle = Particle.FLAME;
    private int ambientParticleCount = 10;
    private int entryParticleCount = 10;
    private int exitParticleCount = 10;
    private double ambientParticleSpeed = 0.1;
    private long ambientParticleIntervalTicks = 10;
    private double entryParticleSpeed = 0.1;
    private double exitParticleSpeed = 0.1;

    // --- Sounds ---
    private Sound ambientSound = Sound.ENTITY_ENDERMAN_TELEPORT;
    private Sound activationSound = Sound.ENTITY_ENDERMAN_TELEPORT;
    private float soundVolume = 1.0f;
    private float soundPitch = 1.0f;

    // --- Builder Input Flags (apenas estados, sem lógica) ---
    private boolean awaitingRadius = false;
    private boolean awaitingCooldown = false;
    private boolean awaitingAmbientParticleInterval = false;
    private boolean awaitingSizeX = false;
    private boolean awaitingSizeY = false;
    private boolean awaitingSizeZ = false;

    private boolean awaitingCommandInput = false;
    private boolean awaitingCommandRemoval = false;

    private boolean awaitingParticleInput = false;
    private boolean awaitingParticleCount = false;
    private boolean awaitingParticleSpeed = false;
    private boolean awaitingSoundInput = false;

    private ParticleStage particleStage = ParticleStage.AMBIENT;
    private boolean settingAmbientSound = true;

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
    public GateData.PortalShape getShape() { return shape; }
    public void setShape(GateData.PortalShape value) { shape = value; }
    public double getSizeX() { return sizeX; }
    public void setSizeX(double value) { sizeX = value; }
    public double getSizeY() { return sizeY; }
    public void setSizeY(double value) { sizeY = value; }
    public double getSizeZ() { return sizeZ; }
    public void setSizeZ(double value) { sizeZ = value; }

    public long getCooldownTicks() { return cooldownTicks; }
    public void setCooldownTicks(long cooldownTicks) { this.cooldownTicks = cooldownTicks; }

    // -------------------- Particles --------------------
    public Particle getAmbientParticle() { return ambientParticle; }
    public void setAmbientParticle(Particle ambientParticle) { this.ambientParticle = ambientParticle; }

    public Particle getEntryParticle() { return entryParticle; }
    public void setEntryParticle(Particle entryParticle) { this.entryParticle = entryParticle; }
    public Particle getExitParticle() { return exitParticle; }
    public void setExitParticle(Particle exitParticle) { this.exitParticle = exitParticle; }
    public int getAmbientParticleCount() { return ambientParticleCount; }
    public void setAmbientParticleCount(int value) { this.ambientParticleCount = value; }
    public int getEntryParticleCount() { return entryParticleCount; }
    public void setEntryParticleCount(int value) { this.entryParticleCount = value; }
    public int getExitParticleCount() { return exitParticleCount; }
    public void setExitParticleCount(int value) { this.exitParticleCount = value; }
    public double getAmbientParticleSpeed() { return ambientParticleSpeed; }
    public void setAmbientParticleSpeed(double value) { this.ambientParticleSpeed = value; }
    public long getAmbientParticleIntervalTicks() { return ambientParticleIntervalTicks; }
    public void setAmbientParticleIntervalTicks(long value) { ambientParticleIntervalTicks = Math.max(1L, value); }
    public double getEntryParticleSpeed() { return entryParticleSpeed; }
    public void setEntryParticleSpeed(double value) { this.entryParticleSpeed = value; }
    public double getExitParticleSpeed() { return exitParticleSpeed; }
    public void setExitParticleSpeed(double value) { this.exitParticleSpeed = value; }

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
    public boolean isAwaitingAmbientParticleInterval() { return awaitingAmbientParticleInterval; }
    public void setAwaitingAmbientParticleInterval(boolean value) { awaitingAmbientParticleInterval = value; }
    public boolean isAwaitingSizeX() { return awaitingSizeX; }
    public void setAwaitingSizeX(boolean value) { awaitingSizeX = value; }
    public boolean isAwaitingSizeY() { return awaitingSizeY; }
    public void setAwaitingSizeY(boolean value) { awaitingSizeY = value; }
    public boolean isAwaitingSizeZ() { return awaitingSizeZ; }
    public void setAwaitingSizeZ(boolean value) { awaitingSizeZ = value; }

    public boolean isAwaitingCommandInput() { return awaitingCommandInput; }
    public void setAwaitingCommandInput(boolean awaitingCommandInput) { this.awaitingCommandInput = awaitingCommandInput; }

    public boolean isAwaitingCommandRemoval() { return awaitingCommandRemoval; }
    public void setAwaitingCommandRemoval(boolean awaitingCommandRemoval) { this.awaitingCommandRemoval = awaitingCommandRemoval; }

    public boolean isAwaitingParticleInput() { return awaitingParticleInput; }
    public void setAwaitingParticleInput(boolean awaitingParticleInput) { this.awaitingParticleInput = awaitingParticleInput; }

    public boolean isAwaitingParticleCount() { return awaitingParticleCount; }
    public void setAwaitingParticleCount(boolean value) { this.awaitingParticleCount = value; }
    public boolean isAwaitingParticleSpeed() { return awaitingParticleSpeed; }
    public void setAwaitingParticleSpeed(boolean value) { this.awaitingParticleSpeed = value; }

    public boolean isAwaitingSoundInput() { return awaitingSoundInput; }
    public void setAwaitingSoundInput(boolean awaitingSoundInput) { this.awaitingSoundInput = awaitingSoundInput; }

    public ParticleStage getParticleStage() { return particleStage; }
    public void setParticleStage(ParticleStage particleStage) { this.particleStage = particleStage; }
    public boolean isSettingAmbient() { return settingAmbientSound; }
    public void setSettingAmbient(boolean value) { this.settingAmbientSound = value; }

    public boolean isAwaitingConditionInput() { return awaitingConditionInput; }
    public void setAwaitingConditionInput(boolean awaitingConditionInput) { this.awaitingConditionInput = awaitingConditionInput; }


    public static GateBuilderData fromGate(UUID playerId, GateData gate) {
        GateBuilderData builder = new GateBuilderData(playerId, gate.getId());

        builder.setName(gate.getId());
        builder.setType(String.valueOf(gate.getType()));

        builder.setLocA(gate.getLoc1());
        builder.setLocB(gate.getLoc2());

        builder.setDetectionRadius(gate.getDetectionRadius());
        builder.setShape(gate.getShape());
        builder.setSizeX(gate.getSizeX());
        builder.setSizeY(gate.getSizeY());
        builder.setSizeZ(gate.getSizeZ());
        builder.setCooldownTicks(gate.getCooldownTicks());

        // Particles
        builder.setAmbientParticle(gate.getAmbientParticle());
        builder.setEntryParticle(gate.getEntryParticle());
        builder.setExitParticle(gate.getExitParticle());
        builder.setAmbientParticleCount(gate.getAmbientParticleCount());
        builder.setEntryParticleCount(gate.getEntryParticleCount());
        builder.setExitParticleCount(gate.getExitParticleCount());
        builder.setAmbientParticleSpeed(gate.getAmbientParticleSpeed());
        builder.setAmbientParticleIntervalTicks(gate.getAmbientParticleIntervalTicks());
        builder.setEntryParticleSpeed(gate.getEntryParticleSpeed());
        builder.setExitParticleSpeed(gate.getExitParticleSpeed());

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
        return awaitingRadius || awaitingCooldown || awaitingAmbientParticleInterval || awaitingSizeX || awaitingSizeY || awaitingSizeZ || awaitingCommandInput ||
                awaitingCommandRemoval || awaitingParticleInput || awaitingParticleCount ||
                awaitingParticleSpeed || awaitingSoundInput ||
                awaitingConditionInput;
    }

    public void clearAllAwaitingFlags() {
        awaitingRadius = false;
        awaitingCooldown = false;
        awaitingAmbientParticleInterval = false;
        awaitingSizeX = false;
        awaitingSizeY = false;
        awaitingSizeZ = false;
        awaitingCommandInput = false;
        awaitingCommandRemoval = false;
        awaitingParticleInput = false;
        awaitingParticleCount = false;
        awaitingParticleSpeed = false;
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
        return locA != null && locA.getWorld() != null
                && locB != null && locB.getWorld() != null
                && type != null && !type.isBlank();
    }
}
