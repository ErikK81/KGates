package me.erik.kgates.manager;

import me.erik.kgates.KGates;
import me.erik.kgates.builder.GateBuilderData;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class GateManager {

    private final Map<String, GateData> gates = new HashMap<>();
    private final File file;
    private final YamlConfiguration config;

    public GateManager(KGates plugin) {
        this.file = new File(plugin.getDataFolder(), "gates.yml");
        this.config = YamlConfiguration.loadConfiguration(file);
        loadAll();
    }

    public void addGateFromBuilder(GateBuilderData builder) {
        GateData gate = new GateData(builder.getId(), builder.getLocA(), builder.getLocB());
        gate.setType(builder.getType());
        gate.setDetectionRadius(builder.getDetectionRadius());
        gate.setCooldownTicks(builder.getCooldownTicks());
        gates.put(gate.getId().toLowerCase(), gate);
        saveAll();
    }

    public GateData getGate(String id) { return gates.get(id.toLowerCase()); }

    public void removeGate(String id) {
        gates.remove(id.toLowerCase());
        config.set("portals." + id.toLowerCase(), null);
        saveFile();
    }

    public Collection<GateData> getAllGates() { return gates.values(); }

    public void saveAll() {
        for (GateData gate : gates.values()) {
            config.set("portals." + gate.getId(), gate.serialize());
        }
        saveFile();
    }

    private void saveFile() {
        try { config.save(file); }
        catch (IOException e) { e.printStackTrace(); }
    }

    private void loadAll() {
        ConfigurationSection portalsSection = config.getConfigurationSection("portals");
        if (portalsSection == null) return;
        for (String key : portalsSection.getKeys(false)) {
            ConfigurationSection gateSection = portalsSection.getConfigurationSection(key);
            if (gateSection != null) {
                gates.put(key.toLowerCase(), GateData.deserialize(gateSection));
            }
        }
    }
}
