package com.budgienet.itemattributes.configs;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

import com.budgienet.itemattributes.ItemAttributes;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class ItemsConfig {
    public List<String> items = new ArrayList<>();
    private final ItemAttributes ia = ItemAttributes.getPlugin(ItemAttributes.class);
    public static ItemsConfig instance = new ItemsConfig();
    private final String fileName = "items.yml";
    private File file;

    public void loadValues() {

        items.clear();

        FileConfiguration config;
        ConfigurationSection tempConf;
        Set<String> tempKeys;

        try {
            config = YamlConfiguration.loadConfiguration(file);
            tempConf = config.getConfigurationSection("items");
            assert tempConf != null;
            tempKeys = tempConf.getKeys(false);
            items.addAll(tempKeys);
        } catch (Exception ex) {
            ia.log(Level.SEVERE, "Could not load items.yml.");
        }

        if (items.size() > 0) {
            ia.log(Level.INFO, "Loaded " + items.size() + " custom items.");
        } else {
            ia.log(Level.INFO, "No custom items loaded.");
        }
    }
    public void setup(File dir) {
        if (!dir.exists()) dir.mkdirs();
        file = new File(dir + File.separator + fileName);

        if (!file.exists()) {
            ia.log(Level.WARNING, "items.yml is missing, generating a new one.");
            FileConfiguration config = YamlConfiguration.loadConfiguration(file);

            config.set("items.ExampleItem.Material", "CHAINMAIL_HELMET");
            config.set("items.ExampleItem.Name", "&fAged_Chainlets");
            config.set("items.ExampleItem.Lore", "&7&oCommon|&7\"Old_but_reliable.\"");
            config.set("items.ExampleItem.Enchants", "protection:2 fire_protection:2");
            config.set("items.ExampleItem.Attributes", "armor:2-4:head movespeed:3:head");
            config.set("items.ExampleItem.Flags", "hide_enchants hide_dye");

            try {
                config.save(file);
            } catch (Exception e) {
                e.printStackTrace();
            }
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
