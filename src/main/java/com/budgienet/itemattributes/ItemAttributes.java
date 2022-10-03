package com.budgienet.itemattributes;

import com.budgienet.itemattributes.commands.ConstructTabCompleter;
import com.budgienet.itemattributes.commands.ItemAttributesCommand;
import com.budgienet.itemattributes.configs.ItemsConfig;
import com.budgienet.itemattributes.configs.LangConfig;
import com.budgienet.itemattributes.configs.MainConfig;
import com.jeff_media.armorequipevent.ArmorEquipEvent;
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

    public String basePermission,givePermission,giveItemPermission;
    public String[][] attrNames = {{"armor", "GENERIC_ARMOR"},{"toughness", "GENERIC_ARMOR_TOUGHNESS"},{"damage", "GENERIC_ATTACK_DAMAGE"},{"knockback", "GENERIC_ATTACK_KNOCKBACK"},{"attackspeed", "GENERIC_ATTACK_SPEED"},{"knockbackres", "GENERIC_KNOCKBACK_RESISTANCE"},{"luck", "GENERIC_LUCK"},{"health","GENERIC_MAX_HEALTH"},{"movespeed", "GENERIC_MOVEMENT_SPEED"}};
    public List<String> enchNames = new ArrayList<>();
    public List<String> flagNames = new ArrayList<>();
    public List<String> autoList1 = new ArrayList<>();
    public List<String> autoList1temp = new ArrayList<>();
    public List<String> autoList2 = new ArrayList<>();
    public List<String> materialNames = new ArrayList<>(Material.values().length);
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
        basePermission = "itemattributes";
        givePermission = basePermission + ".give";
        giveItemPermission = basePermission + ".giveitem";
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

        // Plugin support
        if (Bukkit.getPluginManager().isPluginEnabled("AdvancedEnchantments")) {
            if (aeSupport) {
                this.log(Level.INFO,"AdvancedEnchantments support active.");
            } else {
                this.log(Level.INFO,"AdvancedEnchantments detected, but support is disable in config.");
            }
        }
    }

    public void log(Level level, String string) {
        getLogger().log(level, string);
    }
}