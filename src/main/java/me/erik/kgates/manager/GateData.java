package me.erik.kgates.manager;

import me.erik.kgates.conditions.SimpleGateCondition;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;

import java.util.*;

public class GateData {

    private final String id;
    private String type;
    private final Location loc1;
    private final Location loc2;
    private double detectionRadius = 1.5;
    private long cooldownTicks = 20;
    private final List<SimpleGateCondition> conditions = new ArrayList<>();

    public GateData(String id, Location loc1, Location loc2) {
        this.id = id;
        this.loc1 = loc1;
        this.loc2 = loc2;
    }

    public String getId() { return id; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public Location getLoc1() { return loc1; }
    public Location getLoc2() { return loc2; }
    public double getDetectionRadius() { return detectionRadius; }
    public void setDetectionRadius(double detectionRadius) { this.detectionRadius = detectionRadius; }
    public long getCooldownTicks() { return cooldownTicks; }
    public void setCooldownTicks(long cooldownTicks) { this.cooldownTicks = cooldownTicks; }
    public List<SimpleGateCondition> getConditions() { return conditions; }
    public void addCondition(SimpleGateCondition condition) { conditions.add(condition); }

    public boolean canActivate(org.bukkit.entity.Player player) {
        for (SimpleGateCondition condition : conditions) {
            if (!condition.canActivate(player)) return false;
        }
        return true;
    }

    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        map.put("type", type);
        map.put("loc1", locToMap(loc1));
        map.put("loc2", locToMap(loc2));
        map.put("detectionRadius", detectionRadius);
        map.put("cooldownTicks", cooldownTicks);

        List<Map<String, Object>> condList = new ArrayList<>();
        for (SimpleGateCondition cond : conditions) condList.add(cond.serialize());
        map.put("conditions", condList);

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
        gate.setType(section.getString("type"));
        gate.setDetectionRadius(section.getDouble("detectionRadius", 1.5));
        gate.setCooldownTicks(section.getLong("cooldownTicks", 20));

        List<Map<String, Object>> condList = (List<Map<String, Object>>) section.getList("conditions");
        if (condList != null) {
            for (Map<String, Object> map : condList) {
                gate.addCondition(SimpleGateCondition.deserialize(map));
            }
        }
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
