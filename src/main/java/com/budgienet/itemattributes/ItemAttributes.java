package com.budgienet.itemattributes;

import com.budgienet.itemattributes.commands.ConstructTabCompleter;
import com.budgienet.itemattributes.commands.ItemAttributesCommand;
import com.budgienet.itemattributes.configs.ItemsConfig;
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

    public String basePermission,givePermission,giveItemPermission,textPrefix,textUnknown,textNoPerm,textHelp,textGiveHelp;
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
        textHelp = textPrefix + "§2Running §aItemAttributes §2v" + getDescription().getVersion() +" by Eroserv" +
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
        textUnknown = "§cUnknown command.";
        textNoPerm = "§cNo permission.";
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

        // Instantiate class listeners
        ArmorEquipEvent.registerListener(this);
        Objects.requireNonNull(this.getCommand("itemattributes")).setExecutor(new ItemAttributesCommand());
        Objects.requireNonNull(this.getCommand("itemattributes")).setTabCompleter(new ConstructTabCompleter());
        getServer().getPluginManager().registerEvents(new WeaponPerms(), this);
        getServer().getPluginManager().registerEvents(new ArmorPerms(), this);
    }

    @Override
    public void onDisable() {
    }

    public void log(Level level, String string) {
        this.getLogger().log(level, string);
    }
}