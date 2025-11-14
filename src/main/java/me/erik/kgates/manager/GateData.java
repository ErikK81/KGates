package me.erik.kgates.manager;

import me.erik.kgates.conditions.SimpleGateCondition;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;

import java.util.*;

public class GateData {

    public enum PortalType {
        DEFAULT,
        ONE_WAY,
        TWO_WAY
    }

    private final String id;
    private PortalType type = PortalType.TWO_WAY;
    private final Location loc1;
    private final Location loc2;
    private double detectionRadius = 1.5;
    private long cooldownTicks = 20;
    private final List<SimpleGateCondition> conditions = new ArrayList<>();
    private List<String> commands = new ArrayList<>();

    // --- Ambient Effects ---
    private Particle ambientParticle = Particle.FLAME;
    private int ambientParticleCount = 10;
    private double ambientParticleSpeed = 0.5;
    private Sound ambientSound = Sound.ENTITY_ENDERMAN_TELEPORT;
    private float ambientSoundVolume = 1.0f;
    private float ambientSoundPitch = 1.0f;

    // --- Activation Effects ---
    private Particle activationParticle = Particle.FLAME;
    private int activationParticleCount = 10;
    private double activationParticleSpeed = 0.5;
    private Sound activationSound = Sound.ENTITY_ENDERMAN_TELEPORT;
    private float activationSoundVolume = 1.0f;
    private float activationSoundPitch = 1.0f;

    public GateData(String id, Location loc1, Location loc2) {
        this.id = id;
        this.loc1 = loc1;
        this.loc2 = loc2;
    }

    // -------------------- Getters / Setters --------------------
    public String getId() { return id; }
    public PortalType getType() { return type; }
    public void setType(PortalType type) { this.type = type; }
    public Location getLoc1() { return loc1; }
    public Location getLoc2() { return loc2; }
    public double getDetectionRadius() { return detectionRadius; }
    public void setDetectionRadius(double detectionRadius) { this.detectionRadius = detectionRadius; }
    public long getCooldownTicks() { return cooldownTicks; }
    public void setCooldownTicks(long cooldownTicks) { this.cooldownTicks = cooldownTicks; }
    public List<SimpleGateCondition> getConditions() { return conditions; }
    public void addCondition(SimpleGateCondition condition) { conditions.add(condition); }
    public List<String> getCommands() { return commands; }
    public void setCommands(List<String> commands) { this.commands = commands; }
    public void addCommand(String cmd) { this.commands.add(cmd); }
    public void removeCommand(int index) { if (index >= 0 && index < commands.size()) commands.remove(index); }

    // -------------------- Ambient Effects --------------------
    public Particle getAmbientParticle() { return ambientParticle; }
    public void setAmbientParticle(Particle ambientParticle) { this.ambientParticle = ambientParticle; }
    public int getAmbientParticleCount() { return ambientParticleCount; }
    public void setAmbientParticleCount(int ambientParticleCount) { this.ambientParticleCount = ambientParticleCount; }
    public double getAmbientParticleSpeed() { return ambientParticleSpeed; }
    public void setAmbientParticleSpeed(double ambientParticleSpeed) { this.ambientParticleSpeed = ambientParticleSpeed; }
    public Sound getAmbientSound() { return ambientSound; }
    public void setAmbientSound(Sound ambientSound) { this.ambientSound = ambientSound; }
    public float getAmbientSoundVolume() { return ambientSoundVolume; }
    public void setAmbientSoundVolume(float ambientSoundVolume) { this.ambientSoundVolume = ambientSoundVolume; }
    public float getAmbientSoundPitch() { return ambientSoundPitch; }
    public void setAmbientSoundPitch(float ambientSoundPitch) { this.ambientSoundPitch = ambientSoundPitch; }

    // -------------------- Activation Effects --------------------
    public Particle getActivationParticle() { return activationParticle; }
    public void setActivationParticle(Particle activationParticle) { this.activationParticle = activationParticle; }
    public int getActivationParticleCount() { return activationParticleCount; }
    public void setActivationParticleCount(int activationParticleCount) { this.activationParticleCount = activationParticleCount; }
    public double getActivationParticleSpeed() { return activationParticleSpeed; }
    public void setActivationParticleSpeed(double activationParticleSpeed) { this.activationParticleSpeed = activationParticleSpeed; }
    public Sound getActivationSound() { return activationSound; }
    public void setActivationSound(Sound activationSound) { this.activationSound = activationSound; }
    public float getActivationSoundVolume() { return activationSoundVolume; }
    public void setActivationSoundVolume(float activationSoundVolume) { this.activationSoundVolume = activationSoundVolume; }
    public float getActivationSoundPitch() { return activationSoundPitch; }
    public void setActivationSoundPitch(float activationSoundPitch) { this.activationSoundPitch = activationSoundPitch; }

    // -------------------- Serialization --------------------
    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        map.put("type", type.name());
        map.put("loc1", locToMap(loc1));
        map.put("loc2", locToMap(loc2));
        map.put("detectionRadius", detectionRadius);
        map.put("cooldownTicks", cooldownTicks);

        List<Map<String, Object>> condList = new ArrayList<>();
        for (SimpleGateCondition cond : conditions) condList.add(cond.serialize());
        map.put("conditions", condList);

        map.put("commands", commands);

        // ambient
        map.put("ambientParticle", ambientParticle.name());
        map.put("ambientParticleCount", ambientParticleCount);
        map.put("ambientParticleSpeed", ambientParticleSpeed);
        map.put("ambientSound", ambientSound != null ? ambientSound.name() : null);
        map.put("ambientSoundVolume", ambientSoundVolume);
        map.put("ambientSoundPitch", ambientSoundPitch);

        // activation
        map.put("activationParticle", activationParticle.name());
        map.put("activationParticleCount", activationParticleCount);
        map.put("activationParticleSpeed", activationParticleSpeed);
        map.put("activationSound", activationSound != null ? activationSound.name() : null);
        map.put("activationSoundVolume", activationSoundVolume);
        map.put("activationSoundPitch", activationSoundPitch);

        return map;
    }

    private Map<String, Object> locToMap(Location loc) {
        Map<String, Object> map = new HashMap<>();
        map.put("world", Objects.requireNonNull(loc.getWorld()).getName());
        map.put("x", loc.getX());
        map.put("y", loc.getY());
        map.put("z", loc.getZ());
        map.put("yaw", loc.getYaw());
        map.put("pitch", loc.getPitch());
        return map;
    }

    public static GateData deserialize(ConfigurationSection section) {
        Location loc1 = mapToLoc(Objects.requireNonNull(section.getConfigurationSection("loc1")));
        Location loc2 = mapToLoc(Objects.requireNonNull(section.getConfigurationSection("loc2")));
        GateData gate = new GateData(section.getName(), loc1, loc2);

        String typeName = section.getString("type", "TWO_WAY");
        try { gate.setType(PortalType.valueOf(typeName.toUpperCase())); }
        catch (IllegalArgumentException e) { gate.setType(PortalType.TWO_WAY); }

        gate.setDetectionRadius(section.getDouble("detectionRadius", 1.5));
        gate.setCooldownTicks(section.getLong("cooldownTicks", 20));

        List<Map<String, Object>> condList = (List<Map<String, Object>>) section.getList("conditions");
        if (condList != null)
            for (Map<String, Object> map : condList)
                gate.addCondition(SimpleGateCondition.deserialize(map));

        gate.setCommands(section.getStringList("commands"));

        // ambient
        try {
            gate.setAmbientParticle(Particle.valueOf(section.getString("ambientParticle", "FLAME")));
        } catch (Exception ignored) {}
        gate.setAmbientParticleCount(section.getInt("ambientParticleCount", 10));
        gate.setAmbientParticleSpeed(section.getDouble("ambientParticleSpeed", 0.5));

        String ambientSoundName = section.getString("ambientSound");
        if (ambientSoundName != null && !ambientSoundName.isEmpty()) {
            try {
                gate.setAmbientSound(Sound.valueOf(ambientSoundName));
            } catch (IllegalArgumentException ignored) {}
        }

        gate.setAmbientSoundVolume((float) section.getDouble("ambientSoundVolume", 1.0));
        gate.setAmbientSoundPitch((float) section.getDouble("ambientSoundPitch", 1.0));

        // activation
        try {
            gate.setActivationParticle(Particle.valueOf(section.getString("activationParticle", "FLAME")));
        } catch (Exception ignored) {}
        gate.setActivationParticleCount(section.getInt("activationParticleCount", 10));
        gate.setActivationParticleSpeed(section.getDouble("activationParticleSpeed", 0.5));

        String activationSoundName = section.getString("activationSound");
        if (activationSoundName != null && !activationSoundName.isEmpty()) {
            try {
                gate.setActivationSound(Sound.valueOf(activationSoundName));
            } catch (IllegalArgumentException ignored) {}
        }

        gate.setActivationSoundVolume((float) section.getDouble("activationSoundVolume", 1.0));
        gate.setActivationSoundPitch((float) section.getDouble("activationSoundPitch", 1.0));

        return gate;
    }

    private static Location mapToLoc(ConfigurationSection section) {
        return new Location(
                Bukkit.getWorld(Objects.requireNonNull(section.getString("world"))),
                section.getDouble("x"),
                section.getDouble("y"),
                section.getDouble("z"),
                (float) section.getDouble("yaw"),
                (float) section.getDouble("pitch")
        );
    }
}
