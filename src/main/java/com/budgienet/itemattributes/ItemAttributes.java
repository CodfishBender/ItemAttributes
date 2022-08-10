package com.budgienet.itemattributes;

import com.budgienet.itemattributes.commands.ConstructTabCompleter;
import com.budgienet.itemattributes.commands.ItemAttributesCommand;
import com.budgienet.itemattributes.configs.ItemsConfig;
import com.budgienet.itemattributes.configs.MainConfig;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;

public final class ItemAttributes extends JavaPlugin {

    public String basePermission;
    public String givePermission;
    public String giveItemPermission;
    public String textPrefix;
    public String textUnknown;
    public String textHelp;
    public String[][] attrNames = {{"armor", "GENERIC_ARMOR"},{"toughness", "GENERIC_ARMOR_TOUGHNESS"},{"damage", "GENERIC_ATTACK_DAMAGE"},{"knockback", "GENERIC_ATTACK_KNOCKBACK"},{"attackspeed", "GENERIC_ATTACK_SPEED"},{"knockbackres", "GENERIC_KNOCKBACK_RESISTANCE"},{"luck", "GENERIC_LUCK"},{"health","GENERIC_MAX_HEALTH"},{"movespeed", "GENERIC_MOVEMENT_SPEED"}};
    public List<String> enchNames = new ArrayList<>();
    public List<String> flagNames = new ArrayList<>();
    public List<String> autoList1 = new ArrayList<>();
    public List<String> autoList2 = new ArrayList<>();
    public List<String> materialNames = new ArrayList<>(Material.values().length);
    public boolean aeSupport = false;

    @Override
    public void onEnable() {
        // Load configs
        MainConfig.instance.setup(getDataFolder());
        ItemsConfig.instance.setup(getDataFolder());

        // Build variables
        basePermission = "itemattributes";
        givePermission = basePermission + ".give";
        giveItemPermission = basePermission + ".giveitem";
        textHelp = textPrefix + "§fHelp:\n> /itema - Command alias\n> /itemattributes - Shows this help section\n> /itemattributes give <player> - Command to give an item";
        textUnknown = "§cUnknown command.";
        for (Enchantment e : Enchantment.values()) {
            enchNames.add(e.getKey().getKey());
        }
        for (ItemFlag f : ItemFlag.values()) {
            flagNames.add(f.name().toLowerCase());
        }
        autoList1.add("give");
        autoList1.add("giveitem");
        autoList1.add("help");

        // Plugin support
        if (Bukkit.getPluginManager().isPluginEnabled("AdvancedEnchantments")) {
            if (aeSupport) {
                this.log(Level.INFO,"AdvancedEnchantments support active.");
            } else {
                this.log(Level.INFO,"AdvancedEnchantments detected, but support is disable in config.");
            }
        }

        Objects.requireNonNull(this.getCommand("itemattributes")).setExecutor(new ItemAttributesCommand());
        Objects.requireNonNull(this.getCommand("itemattributes")).setTabCompleter(new ConstructTabCompleter());
    }

    @Override
    public void onDisable() {
    }

    public void log(Level level, String string) {
        this.getLogger().log(level, string);
    }
}