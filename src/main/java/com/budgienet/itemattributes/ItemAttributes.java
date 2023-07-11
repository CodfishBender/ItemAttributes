package com.budgienet.itemattributes;

import com.budgienet.itemattributes.commands.ConstructTabCompleter;
import com.budgienet.itemattributes.commands.ItemAttributesCommand;
import com.budgienet.itemattributes.configs.ItemsConfig;
import com.budgienet.itemattributes.configs.LangConfig;
import com.budgienet.itemattributes.configs.MainConfig;
import com.jeff_media.armorequipevent.ArmorEquipEvent;
import org.bukkit.Bukkit;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;

public final class ItemAttributes extends JavaPlugin {

    public String basePerm,givePerm,giveItemPerm,fixItemPerm,nbtPerm,addEnchPerm;
    public String[][] attrNames = {{"armor", "GENERIC_ARMOR"},{"toughness", "GENERIC_ARMOR_TOUGHNESS"},{"damage", "GENERIC_ATTACK_DAMAGE"},{"knockback", "GENERIC_ATTACK_KNOCKBACK"},{"attackspeed", "GENERIC_ATTACK_SPEED"},{"knockbackres", "GENERIC_KNOCKBACK_RESISTANCE"},{"luck", "GENERIC_LUCK"},{"health","GENERIC_MAX_HEALTH"},{"movespeed", "GENERIC_MOVEMENT_SPEED"}};
    public List<String> enchNames = new ArrayList<>();
    public List<String> flagNames = new ArrayList<>();
    public List<String> autoList1 = new ArrayList<>();
    public List<String> autoList = new ArrayList<>();
    public boolean aeSupport = false;

    @Override
    public void onEnable() {

        loadPlugin();

        // Instantiate class listeners
        ArmorEquipEvent.registerListener(this);
        Objects.requireNonNull(this.getCommand("itemattributes")).setExecutor(new ItemAttributesCommand());
        Objects.requireNonNull(this.getCommand("itemattributes")).setTabCompleter(new ConstructTabCompleter());
        getServer().getPluginManager().registerEvents(new WeaponPerms(), this);
        getServer().getPluginManager().registerEvents(new ArmorPerms(), this);
        getServer().getPluginManager().registerEvents(new DurabilityHandler(), this);
    }

    @Override
    public void onDisable() {
    }

    public void loadPlugin() {
        // Load configs
        MainConfig.instance.setup(getDataFolder());
        ItemsConfig.instance.setup(getDataFolder());
        LangConfig.instance.setup(getDataFolder());

        // Build variables
        basePerm = "itemattributes";
        givePerm = basePerm + ".give";
        giveItemPerm = basePerm + ".giveitem";
        fixItemPerm = basePerm + ".fixitem";
        nbtPerm = basePerm + ".addnbt";
        addEnchPerm = basePerm + ".addenchant";
        enchNames.clear();
        for (Enchantment e : Enchantment.values()) {
            enchNames.add(e.getKey().getKey());
        }
        flagNames.clear();
        for (ItemFlag f : ItemFlag.values()) {
            flagNames.add(f.name().toLowerCase());
        }

        // Build auto tab complete list
        autoList1.clear();
        autoList1.add("give");
        autoList1.add("giveitem");
        autoList1.add("help");
        autoList1.add("reload");
        autoList1.add("fixitem");
        autoList1.add("addnbt");
        autoList1.add("addenchant");

        // Plugin support
        if (Bukkit.getPluginManager().isPluginEnabled("AdvancedEnchantments")) {
            if (aeSupport) {
                this.log("AdvancedEnchantments support active.");
            } else {
                this.log("AdvancedEnchantments detected, but support is disable in config.");
            }
        }
    }

    public void log(String string) {
        getLogger().log(Level.INFO, string);
    }

    public void log(String level, String string) {
        Level l = Level.INFO;
        switch(level) {
            case "WARNING": l = Level.WARNING; break;
            case "SEVERE": l = Level.SEVERE; break;
            case "ALL": l = Level.ALL; break;
        }
        getLogger().log(l, string);
    }
}