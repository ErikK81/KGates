package me.erik.kgates.manager;

import me.erik.kgates.conditions.SimpleGateCondition;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.*;

public class GateData {

    private final String id;
    private String type;
    private final Location loc1;
    private final Location loc2;
    private double detectionRadius = 1.5; // padrão
    private long cooldownTicks = 20; // padrão: 1 segundo
    private final List<SimpleGateCondition> conditions = new ArrayList<>();

    public GateData(String id, Location loc1, Location loc2) {
        this.id = id;
        this.loc1 = loc1;
        this.loc2 = loc2;
        this.type = "default";
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

    public List<SimpleGateCondition> getConditions() {
        return Collections.unmodifiableList(conditions);
    }

    public void addCondition(SimpleGateCondition condition) {
        if (condition != null) conditions.add(condition);
    }

    /**
     * Verifica se todas as condições permitem a ativação do portal para o jogador.
     */
    public boolean canActivate(Player player) {
        return conditions.stream().allMatch(c -> c.canActivate(player));
    }

    /**
     * Serializa o portal para YAML.
     */
    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        map.put("type", type);
        map.put("loc1", locToMap(loc1));
        map.put("loc2", locToMap(loc2));
        map.put("detectionRadius", detectionRadius);
        map.put("cooldownTicks", cooldownTicks);

        Map<String, Object> condMap = new LinkedHashMap<>();
        for (int i = 0; i < conditions.size(); i++) {
            condMap.put(String.valueOf(i), conditions.get(i).serialize());
        }
        map.put("conditions", condMap);

        return map;
    }

    private static Map<String, Object> locToMap(Location loc) {
        Objects.requireNonNull(loc, "Location cannot be null");
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
        Objects.requireNonNull(section, "ConfigurationSection cannot be null");
        Location loc1 = mapToLoc(Objects.requireNonNull(section.getConfigurationSection("loc1")));
        Location loc2 = mapToLoc(Objects.requireNonNull(section.getConfigurationSection("loc2")));

        GateData gate = new GateData(section.getName(), loc1, loc2);
        gate.setType(section.getString("type", "default"));
        gate.setDetectionRadius(section.getDouble("detectionRadius", 1.5));
        gate.setCooldownTicks(section.getLong("cooldownTicks", 20));

        ConfigurationSection condSection = section.getConfigurationSection("conditions");
        if (condSection != null) {
            gate.conditions.addAll(GateConditionLoader.loadConditions(condSection));
        }

        return gate;
    }

    private static Location mapToLoc(ConfigurationSection section) {
        Objects.requireNonNull(section, "Location section cannot be null");
        String worldName = section.getString("world");
        assert worldName != null;
        return new Location(
                Objects.requireNonNull(Bukkit.getWorld(worldName), "World " + worldName + " not found"),
                section.getDouble("x"),
                section.getDouble("y"),
                section.getDouble("z"),
                (float) section.getDouble("yaw"),
                (float) section.getDouble("pitch")
        );
    }
}
