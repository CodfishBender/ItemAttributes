package com.budgienet.itemattributes.configs;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import com.budgienet.itemattributes.ItemAttributes;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class MainConfig {
    private final ItemAttributes ia = ItemAttributes.getPlugin(ItemAttributes.class);
    public static MainConfig instance = new MainConfig();
    final String fileName = "config.yml";
    public List<String> itemFixBlacklist = new ArrayList<>();
    private File file;

    public void loadValues() {
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        ia.aeSupport = config.getBoolean("PluginSupport.AdvancedEnchantments");
        itemFixBlacklist = config.getStringList("ItemFixBlacklist");
        if (!itemFixBlacklist.isEmpty()) ia.log(Level.INFO,itemFixBlacklist.size() + " names blacklisted from /fix.");
    }
    public void setup(File dir) {
        if (!dir.exists()) dir.mkdirs();

        // Regenerate missing config
        file = new File(dir + File.separator + fileName);
        if (!file.exists()) {
            ia.log(Level.WARNING, "config.yml is missing, generating a new one.");
            ia.saveDefaultConfig();
        }

        FileConfiguration config = YamlConfiguration.loadConfiguration(file);

        try {
            config.save(file);
        } catch (Exception e) {
            e.printStackTrace();
        }

        loadValues();

        try {
            config.save(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
