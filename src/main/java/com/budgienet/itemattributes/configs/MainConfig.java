package com.budgienet.itemattributes.configs;

import java.io.File;
import java.util.logging.Level;

import com.budgienet.itemattributes.ItemAttributes;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class MainConfig {
    private final ItemAttributes ia = ItemAttributes.getPlugin(ItemAttributes.class);
    public static MainConfig instance = new MainConfig();
    private final String fileName = "config.yml";
    private File file;

    public void loadValues() {
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        ia.aeSupport = config.getBoolean("PluginSupport.AdvancedEnchantments");
        ia.textPrefix = ChatColor.translateAlternateColorCodes('&',config.getString("TextPrefix"));
    }
    public void setup(File dir) {
        if (!dir.exists()) dir.mkdirs();
        file = new File(dir + File.separator + fileName);

        // Regenerate missing config
        if (!file.exists()) {
            ia.log(Level.WARNING, "config.yml is missing, generating a new one.");
            ia.saveDefaultConfig();
        }

        FileConfiguration config = YamlConfiguration.loadConfiguration(file);

        // Regenerate missing items
        if (!config.contains("TextPrefix")) {
            ia.log(Level.WARNING, "Adding missing TextPrefix to config.yml");
            config.set("TextPrefix", "§7[§aItemAttributes§7] §7");
        }
        if (!config.contains("PluginSupport.AdvancedEnchantments")) {
            ia.log(Level.WARNING, "Adding missing PluginSupport.AdvancedEnchantments to config.yml");
            config.set("PluginSupport.AdvancedEnchantments", true);
        }
        try {
            config.save(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
        loadValues();
    }
    public FileConfiguration getConfig() {
        return YamlConfiguration.loadConfiguration(file);
    }
    public void write(File dir, String loc, Object obj) {
        if (!dir.exists()) {
            dir.mkdirs();
        }
        file = new File(dir + File.separator + fileName);

        getConfig().set(loc, obj);
        try {
            getConfig().save(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
        loadValues();
    }
}
