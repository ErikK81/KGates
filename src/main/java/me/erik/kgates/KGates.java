package me.erik.kgates;

import me.erik.kgates.builder.BuilderGUIListener;
import me.erik.kgates.builder.GateBuilderManager;
import me.erik.kgates.conditions.ConditionChatListener;
import me.erik.kgates.commands.WarpCommand;
import me.erik.kgates.listeners.PortalListener;
import me.erik.kgates.manager.GateManager;
import me.erik.kgates.manager.WarpManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.command.PluginCommand;

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

        // Reuse the same command instance so executor and tab completion share state.
        Commands gateCommand = new Commands(gateManager, builderManager, builderGUI);
        PluginCommand kgate = Objects.requireNonNull(getCommand("kgate"), "kgate missing from plugin.yml");
        kgate.setExecutor(gateCommand);
        kgate.setTabCompleter(gateCommand);
        WarpCommand warpCommand = new WarpCommand(warpManager);
        PluginCommand warp = Objects.requireNonNull(getCommand("warp"), "warp missing from plugin.yml");
        warp.setExecutor(warpCommand);
        warp.setTabCompleter(warpCommand);

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
        instance = null;
        builderManager = null;
    }

    public static KGates getInstance() {
        return instance;
    }

    public static GateBuilderManager getBuilderManager() {
        return builderManager;
    }
}
