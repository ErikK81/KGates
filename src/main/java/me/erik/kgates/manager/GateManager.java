package me.erik.kgates.manager;

import me.erik.kgates.KGates;
import me.erik.kgates.builder.GateBuilderData;
import me.erik.kgates.conditions.SimpleGateCondition;
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
        if (!file.exists()) {
            try {
                file.getParentFile().mkdirs();
                file.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe("Erro ao criar gates.yml");
                e.printStackTrace();
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

        gates.put(gate.getId().toLowerCase(), gate);
        saveAll();
    }

    public GateData getGate(String id) {
        return gates.get(id.toLowerCase());
    }

    public void removeGate(String id) {
        gates.remove(id.toLowerCase());
        config.set("portals." + id.toLowerCase(), null);
        saveFile();
    }

    public Collection<GateData> getAllGates() {
        return gates.values();
    }

    public void saveAll() {
        for (GateData gate : gates.values()) {
            // Keep the existing YAML untouched when an old portal references a world
            // that is not currently loaded. It can be recovered after that world loads.
            if (!gate.hasResolvedLocations()) continue;
            config.set("portals." + gate.getId(), gate.serialize());
        }
        saveFile();
    }

    private void saveFile() {
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
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

    /**
     * Método para linkar dois portais (útil se quiser criar ida/volta)
     */
    public void linkGates(GateData from, GateData to, GateData.PortalType type) {
        from.setType(type);

        if (type == GateData.PortalType.TWO_WAY) {
            to.setType(type);
        }
    }
}
