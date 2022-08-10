package com.budgienet.itemattributes.commands;

import com.budgienet.itemattributes.ItemAttributes;
import com.budgienet.itemattributes.configs.ItemsConfig;
import net.advancedplugins.ae.api.AEAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.enchantments.Enchantment;

import java.util.*;
import java.util.logging.Level;

/* When I programed this, I was the only person in
 * the world that understood it. Now only God knows.
 * Many comments have been left behind to try explain
 * this abomanation of a class.
 */

public class ItemAttributesCommand implements CommandExecutor {

    private final ItemAttributes ia = ItemAttributes.getPlugin(ItemAttributes.class);

    public boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {

        String arg = args[0].toLowerCase();

        // Handle player command and console command separately
        if ((sender instanceof Player)) {
            final Player player = (Player) sender;
            // Permission checking
            if (!player.hasPermission(ia.basePermission)) {
                player.sendMessage(ia.textUnknown);
                return false;
            }

            // Argument handling
            if (arg.equals("help")) {
                showHelp(player);
                return true;
            }
            if (args.length > 2) {
                if (arg.equals("give")) {
                    if (!player.hasPermission(ia.givePermission)) player.sendMessage(ChatColor.RED + "No permission.");
                    give(args, player);
                    return true;
                }
                if (arg.equals("giveitem")) {
                    if (!player.hasPermission(ia.giveItemPermission)) player.sendMessage(ChatColor.RED + "No permission.");
                    giveItem(args, player);
                    return true;
                }
            }
            // Catch unknown arg
            showHelp(player);
        } else { // Executed by console
            if (args.length > 2) {
                if (arg.equals("give")) {
                    give(args, null);
                    return true;
                }
                if (arg.equals("giveitem")) {
                    giveItem(args, null);
                    return true;
                }
            }
            // Catch unknown arg
            showHelpConsole();
        }
        return true;
    }

    private void showHelp(final Player player) {
        player.sendMessage(ia.textHelp);
    }

    private void showHelpConsole() {
        Bukkit.getServer().getConsoleSender().sendMessage(ia.textHelp);
    }
    private void giveItem(final String[] args, Player player) {

        // Construct a string of arguments to be used by give()

        List<String> newArg = new ArrayList<>();
        newArg.add(args[0]);
        newArg.add(args[1]);

        String item = args[2]; // Item name from arguments
        List<String> items = ItemsConfig.instance.items; // Custom item list

        for (String str : items) {
            if (Objects.equals(str, item)) {

                // Define config
                Configuration config = ItemsConfig.instance.getConfig();

                // Verify material
                String m = config.getString("items." + item + ".Material");
                if (m == null) {
                    String msg = "Missing material: " + item;
                    ia.log(Level.SEVERE, msg);
                    if (player != null) { player.sendMessage(ia.textPrefix + ChatColor.RED + msg); }
                    return;
                }
                newArg.add(m); // Arg[2] >> Material

                // Name
                String name = config.getString("items." + item + ".Name");   // Get data
                if (name != null) {
                    newArg.add("name:" + name); // Arg[2] >> Material
                }

                // Lore
                String lore = config.getString("items." + item + ".Lore");   // Get data
                if (lore != null) {
                    newArg.add("lore:" + lore); // Arg[3+] >> Lore
                }

                // Enchantments
                try {
                    String[] enchants = config.getString("items." + item + ".Enchants").split(" ");   // Get current argument
                    if (enchants.length > 0) {
                        Collections.addAll(newArg, enchants); // Arg[3+] >> Enchants
                    }
                } catch(NullPointerException ignored) {}

                // Attributes
                try {
                    String[] attributes = config.getString("items." + item + ".Attributes").split(" ");   // Get current argument
                    if (attributes.length > 0) {
                        Collections.addAll(newArg, attributes); // Arg[3+] >> Attributes
                    }
                } catch(NullPointerException ignored) {}

                // Flags
                try {
                    String[] flags = config.getString("items." + item + ".Flags").split(" ");   // Get current argument
                    if (flags.length > 0) {
                        Collections.addAll(newArg, flags); // Arg[3+] >> Attributes
                    }
                } catch(NullPointerException ignored) {}

                // Parse values as a command for give()
                give(newArg.toArray(new String[0]), player);
            }
        }
    }
    private void give(final String[] args, Player player) {

        // 0 = give, 1 = Player, 2 = Item, 3+ = name/lore/unbreaking/attributes/enchants

        // Get player
        Player targetPlayer = Bukkit.getPlayer(args[1]);
        if (targetPlayer == null) {
            String msg = "Cannot find player: " + args[1];
            ia.log(Level.SEVERE, msg);
            if (player != null) { player.sendMessage(ia.textPrefix + ChatColor.RED + msg); }
            return;
        }
        // Create item stack
        Material mat = Material.matchMaterial(args[2]);
        if (mat == null) {
            String msg = "Unknown material: " + args[2];
            ia.log(Level.SEVERE, msg);
            if (player != null) { player.sendMessage(ia.textPrefix + ChatColor.RED + msg); }
            return;
        }
        ItemStack itemStack = new ItemStack(mat);
        ItemMeta meta = itemStack.getItemMeta();
        assert meta != null;

        // Set item meta based on args
        for (int i = 3; i < args.length; i++) {

            // 0 = give, 1 = Player, 2 = Item, 3+ = name/lore/unbreaking/attributes/enchants

            String arg = args[i];   // Get current argument
            String[] subArgs = arg.split(":", -1);   // Define array of sub arguments

            // Check if sub arguments are provided
            if (subArgs.length >= 2) {
                // Set item name
                if (arg.toLowerCase().contains("name:")) {
                    // Set the items name
                    meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', arg.replace("name:", "").replace("_", " ")));
                    continue;
                }
                // Set item lore
                else if (arg.toLowerCase().contains("lore:")) {
                    // Convert String into List with pipe breaks
                    List<String> loreList = Arrays.asList(ChatColor.translateAlternateColorCodes('&', arg.replace("lore:", "")).replace("_", " ").split("\\|", -1));
                    meta.setLore(loreList);
                    continue;
                }
            }

            // Set unbreakable
            if (arg.equals("unbreakable")) {
                meta.setUnbreakable(true);
                continue;
            }
            // Set item flags
            ItemFlag flag = null;
            try {
                flag = ItemFlag.valueOf(arg.toUpperCase());
            } catch (IllegalArgumentException ex) {
                try {
                    flag = ItemFlag.valueOf(arg.toUpperCase());
                } catch (IllegalArgumentException ignored) {}
            }

            if (flag != null) {
                meta.addItemFlags(flag);
                continue;
            }




            // Enchantments
            // Attempt to find the Enchant enum
            Enchantment enchant = Enchantment.getByKey(NamespacedKey.fromString(subArgs[0].toLowerCase()));
            // Enchant was found
            if (enchant != null) {
                // Check if amount is int
                try {
                    int amount = Integer.parseInt(subArgs[1]);
                    meta.addEnchant(enchant, amount, true);
                    continue;
                }
                catch (ArrayIndexOutOfBoundsException ex) {
                    meta.addEnchant(enchant, 1, true);
                    continue;
                }
                catch (NumberFormatException ex) {
                    String msg = "Not a number: " + arg;
                    ia.log(Level.SEVERE, msg);
                    if (player != null) { player.sendMessage(ia.textPrefix + ChatColor.RED + msg); }
                    return;
                }
            }

            // Attributes
            org.bukkit.attribute.Attribute attribute = null;  // Instantiate attribute

            // Attempt to find the Attribute enum
            try {
                // Try use bukkit API
                attribute = org.bukkit.attribute.Attribute.valueOf(subArgs[0].toUpperCase());
            } catch (IllegalArgumentException ex) {
                // Try to use alternate names
                for (int ii = 0; ii < ia.attrNames.length; ii++) {
                    if (Objects.equals(subArgs[0], ia.attrNames[ii][0])) {
                        // Found enum
                        attribute = org.bukkit.attribute.Attribute.valueOf(ia.attrNames[ii][1]);
                        break;
                    }
                }
            }


            // Attribute was found
            if (attribute != null) {
                String numArg = subArgs[1];
                float[] range = {0f, 0f};
                float num = 0f;

                // Check if percentage
                AttributeModifier.Operation attMod = (numArg.contains("%")) ? AttributeModifier.Operation.MULTIPLY_SCALAR_1 : AttributeModifier.Operation.ADD_NUMBER;
                numArg = numArg.replace("%","");

                // Verify numbers and get random number from range (e.g. 10-20)
                if (numArg.contains("~")) {
                    // Convert range into array
                    String[] argRange = numArg.split("~", -1);
                    // Make sure range is 2 numbers
                    if (argRange.length > 2) {
                        String msg = "Cannot recognize range: " + numArg;
                        ia.log(Level.SEVERE, msg);
                        if (player != null) { player.sendMessage(ia.textPrefix + ChatColor.RED + msg); }
                        return;
                    }
                    // Verify numbers in range
                    for (int ii = 0; ii < argRange.length; ii++) {
                        try {
                            range[ii] = (float) Double.parseDouble(argRange[ii]);
                        } catch (NumberFormatException ex) {
                            String msg = "Cannot parse number: " + argRange[ii];
                            ia.log(Level.SEVERE, msg);
                            if (player != null) { player.sendMessage(ia.textPrefix + ChatColor.RED + msg); }
                            return;
                        }
                    }
                    // 1 - 10
                    // Get random number
                    if (range[0] >= range[1]) {
                        String msg = "Min cannot be higher than max: " + Arrays.toString(argRange);
                        ia.log(Level.SEVERE, msg);
                        if (player != null) { player.sendMessage(ia.textPrefix + ChatColor.RED + msg); }
                        return;
                    }
                    num = ((float)Math.random() * (range[1] - range[0])) + range[0];
                } else {
                    try { // Verify number
                        num = (float) Double.parseDouble(numArg);
                    } catch (NumberFormatException ex) {
                        String msg = "Cannot parse number: " + numArg;
                        ia.log(Level.SEVERE, msg);
                        if (player != null) { player.sendMessage(ia.textPrefix + ChatColor.RED + msg); }
                        return;
                    }
                }

                EquipmentSlot slot = EquipmentSlot.HAND;
                AttributeModifier modifier = null;

                // Check if slot provided
                if (subArgs.length > 2) {
                    switch (subArgs[2].toLowerCase()) {
                        case "chest": slot = EquipmentSlot.CHEST; break;
                        case "feet": slot = EquipmentSlot.FEET; break;
                        case "hand": slot = EquipmentSlot.HAND; break;
                        case "head": slot = EquipmentSlot.HEAD; break;
                        case "legs": slot = EquipmentSlot.LEGS; break;
                        case "offhand": slot = EquipmentSlot.OFF_HAND; break;
                        default:
                            String msg = "Unknown slot: " + subArgs[2];
                            ia.log(Level.SEVERE, msg);
                            if (player != null) { player.sendMessage(ia.textPrefix + ChatColor.RED + msg); }
                            return;
                    }
                } else {
                    // Auto assign slot based on material
                    String iName = mat.name().toLowerCase();
                    if (iName.contains("helmet")) slot = EquipmentSlot.HEAD;
                    else if (iName.contains("chestplate")) slot = EquipmentSlot.CHEST;
                    else if (iName.contains("leggings")) slot = EquipmentSlot.LEGS;
                    else if (iName.contains("boots")) slot = EquipmentSlot.FEET;
                    else if (iName.contains("shield")) slot = EquipmentSlot.OFF_HAND;
                }

                modifier = new AttributeModifier(UUID.randomUUID(), subArgs[0], num, attMod, slot);
                meta.addAttributeModifier(attribute,modifier);
                continue;
            }

            // AE Support
            if (AEAPI.getAllEnchantments().contains(subArgs[0])) continue;

            // Catch all non-recognized arguments
            String msg ="Unknown argument: " + arg;
            ia.log(Level.SEVERE, msg);
            if (player != null) { player.sendMessage(ia.textPrefix + ChatColor.RED + msg); }
            return;
        } // End For

        // Set item meta data
        itemStack.setItemMeta(meta);

        // AE Support
        if (ia.aeSupport) {
            for (int i = 3; i < args.length; i++) {
                String[] subArgs = args[i].split(":", -1);   // Define array of sub arguments
                // Check if amount is int
                String ea = subArgs[0];
                if (AEAPI.isAnEnchantment(ea)) {
                    try {
                        AEAPI.applyEnchant(ea, Integer.parseInt(subArgs[1]), itemStack);
                    }
                    catch (ArrayIndexOutOfBoundsException ex) {
                        AEAPI.applyEnchant(ea, 1, itemStack);
                    }
                    catch (NumberFormatException ex) {
                        String msg = "Not a number: " + args[i];
                        ia.log(Level.SEVERE, msg);
                        if (player != null) { player.sendMessage(ia.textPrefix + ChatColor.RED + msg); }
                        return;
                    }
                }
            }
        } // End AE Support

        // Give item to player
        targetPlayer.getInventory().addItem(itemStack);

        // Construct console message
        String totalModifiers = (meta.getAttributeModifiers() == null) ? "" : " Attributes:" + meta.getAttributeModifiers().size();
        String totalEnchantments = (itemStack.getEnchantments().size() == 0) ? "" : " Enchants:" + itemStack.getEnchantments().size();
        String itemName = (meta.hasDisplayName()) ? meta.getDisplayName() + ChatColor.GOLD + " (" + itemStack.getType().name() + ")" : itemStack.getType().name();
        String aeEnchants = (AEAPI.getEnchantmentsOnItem(itemStack).size() == 0) ? "" : " AE:" + AEAPI.getEnchantmentsOnItem(itemStack).size();

        String msg = "Gave " + targetPlayer.getName() + ": " + itemName + totalEnchantments + totalModifiers + aeEnchants;
        ia.log(Level.INFO, msg);
        if (player != null) { player.sendMessage(ia.textPrefix + ChatColor.GOLD + msg); }
    } // End Method
} // End Class
