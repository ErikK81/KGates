package me.erik.kgates.builder;

import me.erik.kgates.conditions.SimpleGateCondition;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public final class BuilderGUIItems {
    private BuilderGUIItems() { }

    public static ItemStack item(Material material, String name, List<String> lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
            meta.setLore(lore);
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            item.setItemMeta(meta);
        }
        return item;
    }

    public static void fill(Inventory inventory, Material material) {
        ItemStack filler = item(material, " ", null);
        for (int slot = 0; slot < inventory.getSize(); slot++) inventory.setItem(slot, filler);
    }

    public static ItemStack header(GateBuilderData data) {
        return item(Material.NETHER_STAR, ChatColor.GOLD + "Portal " + data.getName(), List.of(
                ChatColor.GRAY + "Configure cada categoria abaixo.",
                ChatColor.DARK_GRAY + "ID: " + data.getId()));
    }

    public static ItemStack typeItem(GateBuilderData data) {
        return action(Material.COMPASS, "Tipo", friendlyType(data.getType()));
    }

    public static ItemStack pointA(GateBuilderData data) { return point("Ponto A", data.getLocA(), Material.LIME_WOOL); }
    public static ItemStack pointB(GateBuilderData data) { return point("Ponto B", data.getLocB(), Material.RED_WOOL); }

    private static ItemStack point(String name, Location location, Material material) {
        String value = location == null ? ChatColor.RED + "Não definido"
                : location.getWorld() == null ? ChatColor.RED + "Mundo indisponível"
                : ChatColor.WHITE
                + location.getWorld().getName() + "  " + location.getBlockX() + ", "
                + location.getBlockY() + ", " + location.getBlockZ();
        return item(material, ChatColor.AQUA + name, List.of(value, "", ChatColor.YELLOW + "Clique e selecione um bloco"));
    }

    public static ItemStack detectionItem(GateBuilderData data) {
        return action(Material.SPYGLASS, "Raio de detecção", data.getDetectionRadius() + " blocos");
    }

    public static ItemStack shapeItem(GateBuilderData data) {
        String shape = switch (data.getShape()) {
            case SPHERE -> "Esfera";
            case CYLINDER -> "Cilindro";
            case RECTANGLE -> "Retângulo";
        };
        return item(Material.ARMOR_STAND, ChatColor.AQUA + "Formato: " + ChatColor.WHITE + shape, List.of(
                ChatColor.GRAY + "X: " + data.getSizeX() + "  Y: " + data.getSizeY() + "  Z: " + data.getSizeZ(),
                "", ChatColor.YELLOW + "Clique para editar formato e tamanho"));
    }

    public static ItemStack cooldownItem(GateBuilderData data) {
        return action(Material.CLOCK, "Tempo de espera", data.getCooldownTicks() + " ticks");
    }

    public static ItemStack ambientParticle(GateBuilderData data) {
        return action(Material.BLAZE_POWDER, "Partícula ambiente", data.getAmbientParticle().name());
    }

    public static ItemStack entryParticle(GateBuilderData data) {
        return action(Material.FIRE_CHARGE, "Partícula de entrada", data.getEntryParticle().name());
    }

    public static ItemStack exitParticle(GateBuilderData data) {
        return action(Material.FIREWORK_STAR, "Partícula de saída", data.getExitParticle().name());
    }

    public static ItemStack particleCount(String label, int value) {
        return action(Material.GLOWSTONE_DUST, "Quantidade " + label, Integer.toString(value));
    }

    public static ItemStack particleSpeed(String label, double value) {
        return action(Material.SUGAR, "Velocidade " + label, Double.toString(value));
    }

    public static ItemStack ambientSound(GateBuilderData data) {
        return action(Material.NOTE_BLOCK, "Som ambiente", data.getAmbientSound().toString());
    }

    public static ItemStack activationSound(GateBuilderData data) {
        return action(Material.JUKEBOX, "Som de ativação", data.getActivationSound().toString());
    }

    public static ItemStack finalizeItem(GateBuilderData data) {
        boolean ready = data.isComplete();
        return item(ready ? Material.EMERALD_BLOCK : Material.BARRIER,
                (ready ? ChatColor.GREEN : ChatColor.RED) + "Salvar portal",
                List.of(ready ? ChatColor.YELLOW + "Clique para concluir" : ChatColor.GRAY + "Defina os pontos A e B primeiro"));
    }

    public static ItemStack addCommand() { return action(Material.LIME_WOOL, "Adicionar comando", "Entrada pelo chat"); }
    public static ItemStack removeCommand() { return action(Material.RED_WOOL, "Remover comando", "Informe o número pelo chat"); }
    public static ItemStack clearCommands() { return item(Material.TNT, ChatColor.RED + "Limpar comandos", List.of(ChatColor.GRAY + "Remove todos os comandos")); }
    public static ItemStack listCommands(GateBuilderData data) {
        List<String> lore = commandLines(data);
        lore.add(0, ChatColor.GRAY + "Total: " + ChatColor.WHITE + data.getCommands().size());
        return item(Material.WRITABLE_BOOK, ChatColor.AQUA + "Comandos atuais", lore);
    }
    public static ItemStack back() { return item(Material.ARROW, ChatColor.YELLOW + "Voltar", List.of(ChatColor.GRAY + "Retornar ao editor")); }
    public static ItemStack twoWay() { return item(Material.ENDER_PEARL, ChatColor.AQUA + "Duas direções", List.of(ChatColor.GRAY + "Permite viajar nos dois sentidos")); }
    public static ItemStack oneWay() { return item(Material.ENDER_EYE, ChatColor.LIGHT_PURPLE + "Direção única", List.of(ChatColor.GRAY + "Permite apenas A → B")); }

    public static ItemStack commandSummary(GateBuilderData data) {
        List<String> lore = commandLines(data);
        lore.add(""); lore.add(ChatColor.YELLOW + "Clique para gerenciar");
        return item(Material.COMMAND_BLOCK, ChatColor.GOLD + "Comandos (" + data.getCommands().size() + ")", lore);
    }

    public static ItemStack conditionSummary(GateBuilderData data) {
        List<String> lore = new ArrayList<>();
        for (SimpleGateCondition condition : data.getConditions())
            lore.add(ChatColor.GRAY + "• " + ChatColor.WHITE + condition.getDisplayText());
        if (lore.isEmpty()) lore.add(ChatColor.DARK_GRAY + "Nenhuma condição");
        lore.add(""); lore.add(ChatColor.YELLOW + "Clique para gerenciar");
        return item(Material.IRON_BARS, ChatColor.LIGHT_PURPLE + "Condições (" + data.getConditions().size() + ")", lore);
    }

    private static ItemStack action(Material material, String label, String value) {
        return item(material, ChatColor.AQUA + label, List.of(ChatColor.WHITE + value, "", ChatColor.YELLOW + "Clique para alterar"));
    }

    private static List<String> commandLines(GateBuilderData data) {
        List<String> lore = new ArrayList<>();
        for (int i = 0; i < data.getCommands().size(); i++)
            lore.add(ChatColor.GRAY + "" + (i + 1) + ". " + ChatColor.WHITE + "/" + data.getCommands().get(i));
        if (lore.isEmpty()) lore.add(ChatColor.DARK_GRAY + "Nenhum comando");
        return lore;
    }

    private static String friendlyType(String type) {
        return "TWO_WAY".equals(type) ? "Duas direções" : "ONE_WAY".equals(type) ? "Direção única" : "Não definido";
    }
}
