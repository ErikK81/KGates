package me.erik.kgates.manager;

import me.erik.kgates.KGates;
import me.erik.kgates.builder.GateBuilderData;
import me.erik.kgates.conditions.SimpleGateCondition;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;

public class GateManager {

    private final Map<String, GateData> gates = new HashMap<>();
    private final File file;
    private final YamlConfiguration config;
    private final KGates plugin;

    public GateManager(KGates plugin) {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), "gates.yml");
        if (!file.exists()) {
            try {
                if (!file.getParentFile().exists() && !file.getParentFile().mkdirs()) {
                    throw new IOException("Could not create plugin data directory");
                }
                if (!file.createNewFile()) throw new IOException("Could not create gates.yml");
            } catch (IOException e) {
                plugin.getLogger().log(Level.SEVERE, "Erro ao criar gates.yml", e);
            }
        }
        this.config = YamlConfiguration.loadConfiguration(file);
        loadAll();
    }


    public void addGateFromBuilder(GateBuilderData builder) {
        if (!builder.isComplete()) {
            throw new IllegalArgumentException("Gate points must reference loaded worlds");
        }
        GateData gate = new GateData(builder.getId(), builder.getLocA(), builder.getLocB());

        // Define o tipo do portal usando a enum
        gate.setType(GateData.PortalType.valueOf(builder.getType()));

        gate.setDetectionRadius(builder.getDetectionRadius());
        gate.setShape(builder.getShape());
        gate.setSizeX(builder.getSizeX());
        gate.setSizeY(builder.getSizeY());
        gate.setSizeZ(builder.getSizeZ());
        gate.setCooldownTicks(builder.getCooldownTicks());

        gate.setAmbientParticle(builder.getAmbientParticle());
        gate.setEntryParticle(builder.getEntryParticle());
        gate.setEntryParticleCount(builder.getEntryParticleCount());
        gate.setEntryParticleSpeed(builder.getEntryParticleSpeed());
        gate.setExitParticle(builder.getExitParticle());
        gate.setExitParticleCount(builder.getExitParticleCount());
        gate.setExitParticleSpeed(builder.getExitParticleSpeed());
        gate.setAmbientParticleCount(builder.getAmbientParticleCount());
        gate.setAmbientParticleSpeed(builder.getAmbientParticleSpeed());
        gate.setAmbientParticleIntervalTicks(builder.getAmbientParticleIntervalTicks());
        gate.setAmbientSound(builder.getAmbientSound());
        gate.setActivationSound(builder.getActivationSound());
        gate.setActivationSoundVolume(builder.getSoundVolume());
        gate.setActivationSoundPitch(builder.getSoundPitch());

        // Adiciona condições
        for (SimpleGateCondition cond : builder.getConditions()) {
            gate.addCondition(cond);
        }

        // Adiciona comandos
        if (builder.getCommands() != null && !builder.getCommands().isEmpty()) {
            gate.setCommands(builder.getCommands());
        }

        gates.put(normalize(gate.getId()), gate);
        saveAll();
    }

    public GateData getGate(String id) {
        return id == null ? null : gates.get(normalize(id));
    }

    public void removeGate(String id) {
        if (id == null) return;
        String normalizedId = normalize(id);
        gates.remove(normalizedId);
        config.set("portals." + normalizedId, null);
        saveFile();
    }

    public Collection<GateData> getAllGates() {
        return Collections.unmodifiableCollection(gates.values());
    }

    public void saveAll() {
        for (GateData gate : gates.values()) {
            // Keep the existing YAML untouched when an old portal references a world
            // that is not currently loaded. It can be recovered after that world loads.
            if (!gate.hasResolvedLocations()) continue;
            config.set("portals." + normalize(gate.getId()), gate.serialize());
        }
        saveFile();
    }

    private void saveFile() {
        try {
            config.save(file);
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Erro ao salvar gates.yml", e);
        }
    }

    private void loadAll() {
        ConfigurationSection portalsSection = config.getConfigurationSection("portals");
        if (portalsSection == null) return;

        for (String key : portalsSection.getKeys(false)) {
            ConfigurationSection gateSection = portalsSection.getConfigurationSection(key);
            if (gateSection != null) {
                try {
                    gates.put(normalize(key), GateData.deserialize(gateSection));
                } catch (RuntimeException exception) {
                    plugin.getLogger().log(Level.WARNING, "Portal invalido ignorado: " + key, exception);
                }
            }
        }
    }

    /**
     * Método para linkar dois portais (útil se quiser criar ida/volta)
     */
    public void linkGates(GateData from, GateData to, GateData.PortalType type) {
        from.setType(type);

        if (type == GateData.PortalType.TWO_WAY) {
            to.setType(type);
        }
    }

    private static String normalize(String id) {
        return id.toLowerCase(Locale.ROOT);
    }
}
