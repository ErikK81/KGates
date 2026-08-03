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

    public enum PortalShape { SPHERE, CYLINDER, RECTANGLE }

    private final String id;
    private PortalType type = PortalType.TWO_WAY;
    private final Location loc1;
    private final Location loc2;
    private double detectionRadius = 1.5;
    private PortalShape shape = PortalShape.SPHERE;
    private double sizeX = 3.0;
    private double sizeY = 3.0;
    private double sizeZ = 3.0;
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

    // --- Teleport Effects ---
    private Particle entryParticle = Particle.FLAME;
    private int entryParticleCount = 10;
    private double entryParticleSpeed = 0.5;
    private Particle exitParticle = Particle.FLAME;
    private int exitParticleCount = 10;
    private double exitParticleSpeed = 0.5;
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
    public boolean hasResolvedLocations() {
        return loc1 != null && loc1.getWorld() != null && loc2 != null && loc2.getWorld() != null;
    }
    public double getDetectionRadius() { return detectionRadius; }
    public void setDetectionRadius(double detectionRadius) { this.detectionRadius = detectionRadius; }
    public PortalShape getShape() { return shape; }
    public void setShape(PortalShape shape) { this.shape = shape; }
    public double getSizeX() { return sizeX; }
    public void setSizeX(double value) { sizeX = value; }
    public double getSizeY() { return sizeY; }
    public void setSizeY(double value) { sizeY = value; }
    public double getSizeZ() { return sizeZ; }
    public void setSizeZ(double value) { sizeZ = value; }
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
    public Particle getEntryParticle() { return entryParticle; }
    public void setEntryParticle(Particle value) { this.entryParticle = value; }
    public int getEntryParticleCount() { return entryParticleCount; }
    public void setEntryParticleCount(int value) { this.entryParticleCount = value; }
    public double getEntryParticleSpeed() { return entryParticleSpeed; }
    public void setEntryParticleSpeed(double value) { this.entryParticleSpeed = value; }
    public Particle getExitParticle() { return exitParticle; }
    public void setExitParticle(Particle value) { this.exitParticle = value; }
    public int getExitParticleCount() { return exitParticleCount; }
    public void setExitParticleCount(int value) { this.exitParticleCount = value; }
    public double getExitParticleSpeed() { return exitParticleSpeed; }
    public void setExitParticleSpeed(double value) { this.exitParticleSpeed = value; }
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
        map.put("shape", shape.name());
        map.put("sizeX", sizeX);
        map.put("sizeY", sizeY);
        map.put("sizeZ", sizeZ);
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
        map.put("entryParticle", entryParticle.name());
        map.put("entryParticleCount", entryParticleCount);
        map.put("entryParticleSpeed", entryParticleSpeed);
        map.put("exitParticle", exitParticle.name());
        map.put("exitParticleCount", exitParticleCount);
        map.put("exitParticleSpeed", exitParticleSpeed);
        map.put("activationSound", activationSound != null ? activationSound.name() : null);
        map.put("activationSoundVolume", activationSoundVolume);
        map.put("activationSoundPitch", activationSoundPitch);

        return map;
    }

    private Map<String, Object> locToMap(Location loc) {
        if (loc == null || loc.getWorld() == null) {
            throw new IllegalStateException("Cannot serialize a gate location without a loaded world");
        }
        Map<String, Object> map = new HashMap<>();
        map.put("world", loc.getWorld().getName());
        map.put("worldUuid", loc.getWorld().getUID().toString());
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
        try {
            gate.setShape(PortalShape.valueOf(section.getString("shape", "SPHERE").toUpperCase()));
        } catch (IllegalArgumentException ignored) { }
        double legacySize = gate.getDetectionRadius() * 2.0;
        gate.setSizeX(section.getDouble("sizeX", legacySize));
        gate.setSizeY(section.getDouble("sizeY", legacySize));
        gate.setSizeZ(section.getDouble("sizeZ", legacySize));
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
            String legacyParticle = section.getString("activationParticle", "FLAME");
            gate.setEntryParticle(Particle.valueOf(section.getString("entryParticle", legacyParticle)));
            gate.setExitParticle(Particle.valueOf(section.getString("exitParticle", legacyParticle)));
        } catch (Exception ignored) {}
        int legacyCount = section.getInt("activationParticleCount", 10);
        double legacySpeed = section.getDouble("activationParticleSpeed", 0.5);
        gate.setEntryParticleCount(section.getInt("entryParticleCount", legacyCount));
        gate.setEntryParticleSpeed(section.getDouble("entryParticleSpeed", legacySpeed));
        gate.setExitParticleCount(section.getInt("exitParticleCount", legacyCount));
        gate.setExitParticleSpeed(section.getDouble("exitParticleSpeed", legacySpeed));

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
        org.bukkit.World world = null;
        String worldUuid = section.getString("worldUuid");
        if (worldUuid != null) {
            try {
                world = Bukkit.getWorld(UUID.fromString(worldUuid));
            } catch (IllegalArgumentException ignored) { }
        }
        if (world == null) {
            String worldName = section.getString("world");
            if (worldName != null) world = Bukkit.getWorld(worldName);
        }
        return new Location(
                world,
                section.getDouble("x"),
                section.getDouble("y"),
                section.getDouble("z"),
                (float) section.getDouble("yaw"),
                (float) section.getDouble("pitch")
        );
    }
}
