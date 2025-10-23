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

    /**
     * Adiciona ou atualiza um portal a partir do builder.
     */
    public void addGateFromBuilder(GateBuilderData builder) {
        Objects.requireNonNull(builder, "GateBuilderData cannot be null");

        GateData gate = new GateData(builder.getId(), builder.getLocA(), builder.getLocB());
        gate.setType(builder.getType());
        gate.setDetectionRadius(builder.getDetectionRadius());
        gate.setCooldownTicks(builder.getCooldownTicks());
        builder.getConditions().forEach(gate::addCondition);

        gates.put(gate.getId().toLowerCase(), gate);
        saveAll();
    }

    public GateData getGate(String id) {
        return id == null ? null : gates.get(id.toLowerCase());
    }

    public void removeGate(String id) {
        if (id == null) return;
        String key = id.toLowerCase();
        gates.remove(key);
        config.set("portals." + key, null);
        saveFile();
    }

    public Collection<GateData> getAllGates() {
        return Collections.unmodifiableCollection(gates.values());
    }

    /**
     * Salva todos os portais no arquivo YAML.
     */
    public void saveAll() {
        gates.forEach((id, gate) -> config.set("portals." + id, gate.serialize()));
        saveFile();
    }

    private void saveFile() {
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Carrega todos os portais do arquivo YAML.
     */
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
