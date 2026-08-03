package me.erik.kgates;

import me.erik.kgates.builder.BuilderGUIListener;
import me.erik.kgates.builder.GateBuilderManager;
import me.erik.kgates.conditions.ConditionChatListener;
import me.erik.kgates.commands.WarpCommand;
import me.erik.kgates.listeners.PortalListener;
import me.erik.kgates.manager.GateManager;
import me.erik.kgates.manager.WarpManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class KGates extends JavaPlugin {

    private static KGates instance;

    private GateManager gateManager;
    private static GateBuilderManager builderManager;
    private BuilderGUIListener builderGUI;
    private WarpManager warpManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        instance = this;

        gateManager = new GateManager(this);
        builderManager = new GateBuilderManager();
        builderGUI = new BuilderGUIListener(builderManager, gateManager);
        warpManager = new WarpManager(this);

        // Registrar comandos
        Objects.requireNonNull(getCommand("kgate"))
                .setExecutor(new Commands(gateManager, builderManager, builderGUI));
        Objects.requireNonNull(getCommand("kgate"))
                .setTabCompleter(new Commands(gateManager, builderManager, builderGUI));
        WarpCommand warpCommand = new WarpCommand(warpManager);
        Objects.requireNonNull(getCommand("warp")).setExecutor(warpCommand);
        Objects.requireNonNull(getCommand("warp")).setTabCompleter(warpCommand);

        // Registrar listeners
        PortalListener portalListener = new PortalListener(gateManager);
        getServer().getPluginManager().registerEvents(portalListener, this);
        portalListener.startAmbientEffects(this);
        getServer().getPluginManager().registerEvents(
                new ConditionChatListener(builderManager, gateManager, builderGUI), this);
        getServer().getPluginManager().registerEvents(builderGUI, this);

        getLogger().info("KGates carregado com sucesso!");
    }

    @Override
    public void onDisable() {
        if (gateManager != null) {
            gateManager.saveAll();
        } else {
            getLogger().warning("GateManager não estava inicializado, pulando salvamento.");
        }
        if (warpManager != null) warpManager.save();
    }

    public static KGates getInstance() {
        return instance;
    }

    public static GateBuilderManager getBuilderManager() {
        return builderManager;
    }
}
