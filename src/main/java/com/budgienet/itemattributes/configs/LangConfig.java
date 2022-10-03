package com.budgienet.itemattributes.configs;

import com.budgienet.itemattributes.ItemAttributes;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.logging.Level;

public class LangConfig {

    public String textPrefix,textUnknown,textNoPerm,textHelp,textGiveHelp,textNotFixable,textNotFixableAll,textNoArmourPerms,textNoWepPerms;
    private final ItemAttributes ia = ItemAttributes.getPlugin(ItemAttributes.class);
    public static LangConfig instance = new LangConfig();
    final String fileName = "lang.yml";
    private File file;

    public void loadValues() {
        // Load values
        textHelp = textPrefix + "§2Running §aItemAttributes §2v" + ia.getDescription().getVersion() +" by Eroserv" +
                "\n§7> §a/itema§f - Command alias" +
                "\n§7> §a/item help§f - Shows this help section" +
                "\n§7> §a/itema giveitem <player> <item>§f - Give an item from items.yml" +
                "\n§7> §a/itema give <player> <material> ...§f - use §2/itema give help §ffor more info";
        textGiveHelp = "§7> §a/itema give <player> <material> §2... (optional)" +
                "\n§a     name:<item-name>§f - Item name, _ for spaces" +
                "\n§a     lore:<item-lore>§f - Item lore, | for newline" +
                "\n§a     <enchant>:<number>§f - Vanilla or AE enchant" +
                "\n§a     <attribute>:<number>:<slot>§f - Vanilla item attribute" +
                "\n§a     <flag>§f - Vanilla item flag" +
                "\n§a     <perm>§f - Weapon/armor restricted to perm node" +
                "\n§f     <number> can be a random range, e.g. smite:1~5." +
                "\n§f     Leave <slot> blank to use default slot.";

        // Set variables & regen missing items
        textPrefix = tryGetString("Chat_Prefix", "&7[&aIA&7] &7");

        textUnknown = tryGetString("Unknown_Command", "&cUnknown command.");

        textNoPerm = tryGetString("No_Permission", "&cNo permission.");

        textNotFixable = tryGetString("Not_Fixable", "&cThis item cannot be fixed with /fix.");

        textNotFixableAll = tryGetString("Not_Fixable_All", "&cThere's an unfixable item in your inv.");

        textNoArmourPerms =tryGetString("No_Weapon_Permission", "&cYou cannot use this weapon at your rank.");

        textNoWepPerms = tryGetString("No_Armor_Permission", "&cYou cannot use this armor at your rank.");

        try {
            FileConfiguration config = YamlConfiguration.loadConfiguration(file);
            config.save(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void setup(File dir) {

        FileConfiguration config;
        file = new File(dir + File.separator + fileName);

        // Regernatte missing folder
        if (!dir.exists()) dir.mkdirs();

        // Regenerate missing config
        if (!file.exists()) {
            ia.log(Level.WARNING, fileName + " is missing, generating a new one.");
            ia.saveResource(fileName, false);
        }

        config = YamlConfiguration.loadConfiguration(file);

        try {
            config.save(file);
        } catch (Exception e) {
            e.printStackTrace();
        }

        loadValues();
    }

    private String tryGetString(String sField, String sDefault) {
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        // Missing field
        if (!config.contains(sField)) {
            ia.log(Level.WARNING, "Adding missing " + sField + " to " + fileName);
            config.set(sField, sDefault);
        }
        // Try load value, otherwise use default
        try {
            return ChatColor.translateAlternateColorCodes('&', config.getString(sField));
        } catch(NullPointerException e) {
            ia.log(Level.SEVERE, "Error loading value from" + sField + " in " + fileName);
            return ChatColor.translateAlternateColorCodes('&', sDefault);
        }
    }
}
