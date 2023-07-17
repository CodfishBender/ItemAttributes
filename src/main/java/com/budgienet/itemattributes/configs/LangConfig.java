package com.budgienet.itemattributes.configs;

import com.budgienet.itemattributes.ItemAttributes;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class LangConfig {

    public String textPrefix,textUnknown,textNoPerm,textHelp,textGiveHelp,textNotFixable,textNotFixableAll,textNoArmourPerms,textNoWepPerms,textRepairFail,textRepairSuccess,textPlayerNotFound,textMissingArg;
    private final ItemAttributes ia = ItemAttributes.getPlugin(ItemAttributes.class);
    public static LangConfig instance = new LangConfig();
    final String fileName = "lang.yml";
    private File file;

    public void loadValues() {
        // Set variables & regen missing items
        textPrefix = tryGetString("Chat_Prefix", "&7[&aIA&7] &7");

        textUnknown = tryGetString("Unknown_Command", "&cUnknown command.");

        textNoPerm = tryGetString("No_Permission", "&cNo permission.");

        textNotFixable = tryGetString("Not_Fixable", "&cThis item cannot be fixed with /fix.");

        textNotFixableAll = tryGetString("Not_Fixable_All", "&cThere's an unfixable item in your inv.");

        textNoArmourPerms = tryGetString("No_Weapon_Permission", "&cYou cannot use this weapon at your rank.");

        textNoWepPerms = tryGetString("No_Armor_Permission", "&cYou cannot use this armor at your rank.");

        textRepairFail = tryGetString("Repair_Fail", "&cThis item couldn't be repaired.");

        textRepairSuccess = tryGetString("Repair_Success", "&aItem repaired.");

        textPlayerNotFound = tryGetString("Player_Not_Found", "&cPlayer not found.");

        textMissingArg = tryGetString("Missing_Arg", "&cMissing argument.");

        textHelp = textPrefix + "§7ItemAttributes v" + ia.getDescription().getVersion() +" by Eroserv" +
                "\n§7> §a/itema§7 - Command alias" +
                "\n§7> §a/itema reload§7 - Reload the plugin and items" +
                "\n§7> §a/itema help§7 - Shows this help section" +
                "\n§7> §a/itema addnbt§7 - Add an nbt property to a held item" +
                "\n§7> §a/itema fixitem§7 - Repair a held item" +
                "\n§7> §a/itema giveitem <player> <item>§7 - Give an item from items.yml" +
                "\n§7> §a/itema give <player> <material> ...§7 - See §2/itema give help";
        textGiveHelp = "§7> §a/itema give <player> <material> §2... (optional)" +
                "\n§a     name:<name>§7 - Items name, _ for spaces" +
                "\n§a     lore:<lore>§7 - Items lore, | for newline" +
                "\n§a     <enchant>:<number>§7 - Vanilla or AE enchant" +
                "\n§a     <attribute>:<number>:<slot>§7 - Vanilla item attribute" +
                "\n§a     <flag>§7 - Vanilla item flag" +
                "\n§a     <perm>§7 - Weapon/armor restricted to perm node" +
                "\n§7     <number> can be a random range, e.g. smite:1~5." +
                "\n§7     Leave <slot> blank to use default slot.";

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

        // Regenerate missing folder
        if (!dir.exists()) dir.mkdirs();

        // Regenerate missing config
        if (!file.exists()) {
            ia.log("WARNING", fileName + " is missing, generating a new one.");
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
            ia.log("WARNING", "Adding missing " + sField + " to " + fileName);
            config.set(sField, sDefault);
            try {
                config.save(file);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        // Try load value, otherwise use default
        try {
            return ChatColor.translateAlternateColorCodes('&', config.getString(sField));
        } catch(NullPointerException e) {
            ia.log("SEVERE", "Error loading value from" + sField + " in " + fileName);
            return ChatColor.translateAlternateColorCodes('&', sDefault);
        }
    }
}
