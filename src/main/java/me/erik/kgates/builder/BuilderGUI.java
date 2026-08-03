package me.erik.kgates.builder;

import me.erik.kgates.conditions.ConditionGUI;
import me.erik.kgates.manager.GateData;
import me.erik.kgates.manager.GateManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;

import static me.erik.kgates.KGates.getInstance;

/** Coordinates menu navigation and delegates rendering to each dedicated GUI. */
public final class BuilderGUI {
    private final GateBuilderManager builderManager;
    private final GateManager gateManager;
    private final MainEditorGUI mainMenu = new MainEditorGUI();
    private final EffectsGUI effectsMenu = new EffectsGUI();
    private final CommandsGUI commandsMenu = new CommandsGUI();
    private final TypeGUI typeMenu = new TypeGUI();
    private final PortalShapeGUI shapeMenu = new PortalShapeGUI();
    private final BrowseGUI browseMenu = new BrowseGUI();
    private final ConditionGUI conditionMenu = new ConditionGUI();

    public BuilderGUI(GateBuilderManager builderManager, GateManager gateManager) {
        this.builderManager = builderManager;
        this.gateManager = gateManager;
    }

    public void openEditor(Player player, GateBuilderData data) { mainMenu.open(player, data); }
    public void openBrowser(Player player) { browseMenu.open(player, new ArrayList<>(gateManager.getAllGates())); }

    public void handleInventoryClick(InventoryClickEvent event) {
        if (!(event.getInventory().getHolder() instanceof BuilderMenuHolder holder)) return;
        event.setCancelled(true);
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (event.getRawSlot() < 0 || event.getRawSlot() >= event.getInventory().getSize()) return;

        GateBuilderData data = builderManager.getBuilder(player.getUniqueId());
        if (holder.menu() == BuilderMenuHolder.Menu.BROWSE) {
            handleBrowse(player, event.getCurrentItem());
            return;
        }
        if (data == null) {
            player.closeInventory();
            player.sendMessage(ChatColor.RED + "Esta sessão de edição expirou.");
            return;
        }

        int slot = event.getRawSlot();
        switch (holder.menu()) {
            case MAIN -> handleMain(player, data, slot);
            case EFFECTS -> handleEffects(player, data, slot);
            case COMMANDS -> handleCommands(player, data, slot);
            case CONDITIONS -> handleConditions(player, data, slot);
            case TYPE -> handleType(player, data, slot);
            case SHAPE -> handleShape(player, data, slot);
            default -> { }
        }
    }

    public void handleBlockClick(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        UUID playerId = player.getUniqueId();
        if (!builderManager.isBuilding(playerId) || !builderManager.isWaitingForBlockClick(playerId)
                || event.getAction() != Action.LEFT_CLICK_BLOCK) return;
        event.setCancelled(true);
        GateBuilderData data = builderManager.getBuilder(playerId);
        if (data == null) return;
        Location location = Objects.requireNonNull(event.getClickedBlock()).getLocation();
        if (builderManager.isWaitingForPointA(playerId)) {
            data.setLocA(location);
            player.sendMessage(ChatColor.GREEN + "Ponto A definido.");
        } else {
            data.setLocB(location);
            player.sendMessage(ChatColor.GREEN + "Ponto B definido.");
        }
        builderManager.setWaitingForBlockClick(playerId, false);
        reopen(player, data);
    }

    private void handleMain(Player player, GateBuilderData data, int slot) {
        switch (slot) {
            case MainEditorGUI.TYPE -> typeMenu.open(player);
            case MainEditorGUI.POINT_A -> promptPoint(player, true);
            case MainEditorGUI.POINT_B -> promptPoint(player, false);
            case MainEditorGUI.SHAPE -> shapeMenu.open(player, data);
            case MainEditorGUI.COMMANDS -> commandsMenu.open(player, data);
            case MainEditorGUI.CONDITIONS -> conditionMenu.open(player, data);
            case MainEditorGUI.EFFECTS -> effectsMenu.open(player, data);
            case MainEditorGUI.COOLDOWN -> promptNumber(player, "cooldown");
            case MainEditorGUI.FINISH -> finish(player, data);
            default -> { }
        }
    }

    private void handleEffects(Player player, GateBuilderData data, int slot) {
        switch (slot) {
            case EffectsGUI.AMBIENT_PARTICLE -> promptParticle(player, GateBuilderData.ParticleStage.AMBIENT);
            case EffectsGUI.AMBIENT_COUNT -> promptParticleNumber(player, GateBuilderData.ParticleStage.AMBIENT, true);
            case EffectsGUI.AMBIENT_SPEED -> promptParticleNumber(player, GateBuilderData.ParticleStage.AMBIENT, false);
            case EffectsGUI.ENTRY_PARTICLE -> promptParticle(player, GateBuilderData.ParticleStage.ENTRY);
            case EffectsGUI.ENTRY_COUNT -> promptParticleNumber(player, GateBuilderData.ParticleStage.ENTRY, true);
            case EffectsGUI.ENTRY_SPEED -> promptParticleNumber(player, GateBuilderData.ParticleStage.ENTRY, false);
            case EffectsGUI.EXIT_PARTICLE -> promptParticle(player, GateBuilderData.ParticleStage.EXIT);
            case EffectsGUI.EXIT_COUNT -> promptParticleNumber(player, GateBuilderData.ParticleStage.EXIT, true);
            case EffectsGUI.EXIT_SPEED -> promptParticleNumber(player, GateBuilderData.ParticleStage.EXIT, false);
            case EffectsGUI.AMBIENT_SOUND -> promptSound(player, true);
            case EffectsGUI.ACTIVATION_SOUND -> promptSound(player, false);
            case EffectsGUI.BACK -> mainMenu.open(player, data);
            default -> { }
        }
    }

    private void handleCommands(Player player, GateBuilderData data, int slot) {
        switch (slot) {
            case CommandsGUI.ADD -> beginChat(player, data, true);
            case CommandsGUI.REMOVE -> beginChat(player, data, false);
            case CommandsGUI.LIST -> sendCommandList(player, data);
            case CommandsGUI.CLEAR -> { data.clearCommands(); commandsMenu.open(player, data); }
            case CommandsGUI.BACK -> mainMenu.open(player, data);
            default -> { }
        }
    }

    private void handleConditions(Player player, GateBuilderData data, int slot) {
        switch (slot) {
            case ConditionGUI.ADD -> {
                data.setAwaitingConditionInput(true);
                player.closeInventory();
                player.sendMessage(ChatColor.YELLOW + "Digite a condição no chat (ou 'cancelar').");
                player.sendMessage(ChatColor.GRAY + "Exemplo: %player_health% >= 10");
            }
            case ConditionGUI.CLEAR -> { data.clearConditions(); conditionMenu.open(player, data); }
            case ConditionGUI.BACK -> mainMenu.open(player, data);
            default -> { }
        }
    }

    private void handleType(Player player, GateBuilderData data, int slot) {
        if (slot == TypeGUI.TWO_WAY) data.setType("TWO_WAY");
        else if (slot == TypeGUI.ONE_WAY) data.setType("ONE_WAY");
        else if (slot == TypeGUI.BACK) { mainMenu.open(player, data); return; }
        else return;
        player.sendMessage(ChatColor.GREEN + "Tipo do portal atualizado.");
        mainMenu.open(player, data);
    }

    private void handleShape(Player player, GateBuilderData data, int slot) {
        switch (slot) {
            case PortalShapeGUI.SPHERE -> data.setShape(GateData.PortalShape.SPHERE);
            case PortalShapeGUI.CYLINDER -> data.setShape(GateData.PortalShape.CYLINDER);
            case PortalShapeGUI.RECTANGLE -> data.setShape(GateData.PortalShape.RECTANGLE);
            case PortalShapeGUI.SIZE_X -> { promptNumber(player, "size_x"); return; }
            case PortalShapeGUI.SIZE_Y -> { promptNumber(player, "size_y"); return; }
            case PortalShapeGUI.SIZE_Z -> { promptNumber(player, "size_z"); return; }
            case PortalShapeGUI.BACK -> { mainMenu.open(player, data); return; }
            default -> { return; }
        }
        player.sendMessage(ChatColor.GREEN + "Formato atualizado.");
        shapeMenu.open(player, data);
    }

    private void handleBrowse(Player player, ItemStack clicked) {
        if (clicked == null || !clicked.hasItemMeta() || !clicked.getItemMeta().hasDisplayName()) return;
        String id = ChatColor.stripColor(clicked.getItemMeta().getDisplayName());
        GateData gate = gateManager.getGate(id);
        if (gate == null) return;
        GateBuilderData data = GateBuilderData.fromGate(player.getUniqueId(), gate);
        builderManager.startBuilding(data);
        mainMenu.open(player, data);
    }

    private void promptPoint(Player player, boolean pointA) {
        builderManager.setWaitingForBlockClick(player.getUniqueId(), true);
        builderManager.setWaitingForPointA(player.getUniqueId(), pointA);
        player.closeInventory();
        player.sendMessage(ChatColor.YELLOW + "Clique em um bloco para definir o ponto " + (pointA ? "A" : "B") + ".");
    }

    private void promptNumber(Player player, String type) { builderManager.getInputHandler().beginNumberInput(player, type); player.closeInventory(); }
    private void promptParticle(Player player, GateBuilderData.ParticleStage stage) { builderManager.getInputHandler().beginParticleInput(player, stage); player.closeInventory(); }
    private void promptParticleNumber(Player player, GateBuilderData.ParticleStage stage, boolean count) { builderManager.getInputHandler().beginParticleNumberInput(player, stage, count); player.closeInventory(); }
    private void promptSound(Player player, boolean ambient) { builderManager.getInputHandler().beginSoundInput(player, ambient); player.closeInventory(); }

    private void beginChat(Player player, GateBuilderData data, boolean add) {
        data.setAwaitingCommandInput(add);
        data.setAwaitingCommandRemoval(!add);
        player.closeInventory();
        player.sendMessage(ChatColor.YELLOW + (add ? "Digite o comando sem a barra inicial." : "Digite o número do comando a remover."));
    }

    private void finish(Player player, GateBuilderData data) {
        if (!data.isComplete()) {
            player.sendMessage(ChatColor.RED + "Defina os pontos A e B em mundos carregados antes de salvar.");
            return;
        }
        gateManager.addGateFromBuilder(data);
        builderManager.stopBuilding(player.getUniqueId());
        player.closeInventory();
        player.sendMessage(ChatColor.GREEN + "Portal salvo com sucesso!");
    }

    private void sendCommandList(Player player, GateBuilderData data) {
        player.sendMessage(ChatColor.GOLD + "Comandos do portal:");
        if (data.getCommands().isEmpty()) player.sendMessage(ChatColor.GRAY + "Nenhum comando configurado.");
        for (int i = 0; i < data.getCommands().size(); i++)
            player.sendMessage(ChatColor.YELLOW + "" + (i + 1) + ". " + ChatColor.WHITE + "/" + data.getCommands().get(i));
    }

    private void reopen(Player player, GateBuilderData data) {
        Bukkit.getScheduler().runTask(getInstance(), () -> mainMenu.open(player, data));
    }
}
