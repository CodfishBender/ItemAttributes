package com.budgienet.itemattributes.commands;

import com.budgienet.itemattributes.ItemAttributes;
import com.budgienet.itemattributes.configs.ItemsConfig;
import com.budgienet.itemattributes.configs.LangConfig;
import com.budgienet.itemattributes.utils.HexEditor;
import com.budgienet.itemattributes.utils.NBTEditor;
import net.advancedplugins.ae.api.AEAPI;
import org.bukkit.Bukkit;
import net.md_5.bungee.api.ChatColor;
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
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;
import java.util.logging.Level;

public class ItemAttributesCommand implements CommandExecutor {

    private enum DataType {eString, eInt}

    private ItemAttributes ia = ItemAttributes.getPlugin(ItemAttributes.class);
    final private LangConfig lang = LangConfig.instance;

    public boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {

        // Handle player command and console command separately
        if ((sender instanceof Player)) {
            final Player player = (Player) sender;
            // Permission checking
            if (!player.hasPermission(ia.basePermission)) {
                player.sendMessage(lang.textNoPerm);
                return false;
            }

            // Argument handling
            if (args.length == 1) {
                if (args[0].equals("help")) {
                    showHelp(player);
                    return true;
                } else if (args[0].equals("reload")) {
                    player.sendMessage(lang.textPrefix + "Reloading plugin...");
                    try {
                        ia.loadPlugin();
                    } catch(Exception e) {
                        player.sendMessage(lang.textPrefix + ChatColor.RED + "An error occured while reloading, please check the console!");
                        e.printStackTrace();
                        return true;
                    }
                    player.sendMessage(lang.textPrefix + "Reload done!");
                    return true;
                }
            } else { // More than 1 argument
                if (args[0].equals("give")) {
                    if (!player.hasPermission(ia.givePermission)) {
                        player.sendMessage(lang.textNoPerm);
                        return true;
                    }
                    if (args[1].equals("help")) {
                        player.sendMessage(lang.textGiveHelp);
                        return true;
                    }
                    give(args, player);
                    return true;
                } else if (args[0].equals("giveitem")) {
                    if (!player.hasPermission(ia.giveItemPermission)) {
                        player.sendMessage(lang.textNoPerm);
                        return true;
                    }
                    giveItem(args, player);
                    return true;
                } else if (args[0].equals("fixitem")) {
                    if (!player.hasPermission(ia.fixItemPermission)) {
                        player.sendMessage(lang.textNoPerm);
                        return true;
                    }
                    repairItem(args[1], sender);
                    return true;
                } else if (args[0].equals("addnbt")) {
                    if (!player.hasPermission(ia.nbtPermission)) {
                        player.sendMessage(lang.textNoPerm);
                        return true;
                    }
                    if (args[2].equals("string")) {
                        addNBTdata(args, sender, DataType.eString);
                        return true;
                    } else if (args[2].equals("int")) {
                        addNBTdata(args, sender, DataType.eInt);
                        return true;
                    }
                    player.sendMessage(lang.textPrefix + ChatColor.RED + "Invalid data type: " + args[2]);
                    return true;
                }
            }
            // Catch unknown arg
            player.sendMessage(lang.textUnknown);
            showHelp(player);
        } else { // Executed by console
            if (args.length == 1) {
                if (args[0].equals("help")) {
                    showHelpConsole();
                    return true;
                } else if (args[0].equals("reload")) {
                    ia.log(Level.INFO,"Reloading plugin...");
                    try {
                        ia.loadPlugin();
                    } catch(Exception e) {
                        ia.log(Level.INFO, lang.textPrefix + e);
                        e.printStackTrace();
                        return true;
                    }
                    ia.log(Level.INFO, "Reload done!");
                    return true;
                }
            } else {
                if (args[0].equals("give")) {
                    if (args[1].equals("help")) {
                        ia.log(Level.INFO, lang.textGiveHelp);
                        return true;
                    }
                    give(args, null);
                    return true;
                } else if (args[0].equals("giveitem")) {
                    giveItem(args, null);
                    return true;
                } else if (args[0].equals("fixitem")) {
                    repairItem(args[1], null);
                    return true;
                } else if (args[0].equals("addnbt")) {
                    if (args[1].equals("string")) {
                        addNBTdata(args, sender, DataType.eString);
                        return true;
                    } else if (args[1].equals("int")) {
                        addNBTdata(args, sender, DataType.eInt);
                        return true;
                    }
                    ia.log(Level.WARNING, "Invalid data type: " + args[2]);
                    return true;
                }
            }
            // Catch unknown arg
            showHelpConsole();
        }
        return true;
    }

    private void repairItem(final String name, CommandSender sender) {

        Player target = Bukkit.getPlayer(name);

        if (target != null) {
            ItemStack handItemStack = target.getInventory().getItemInMainHand();

            if (handItemStack.hasItemMeta()) {
                Damageable itemMeta = (Damageable) handItemStack.getItemMeta();

                if (itemMeta != null) {
                    itemMeta.setDamage(0);
                    handItemStack.setItemMeta(itemMeta);
                    if (sender != null) sender.sendMessage(lang.textRepairSuccess);
                    ia.log(Level.INFO, "Item repaired for " + name);
                    return;
                }
            }
        } else {
            if (sender != null) sender.sendMessage(lang.textPlayerNotFound + " (" + name + ")");
            ia.log(Level.WARNING, "itemfix player not found: " + name);
            return;
        }
        if (sender != null) sender.sendMessage(lang.textRepairFail);
        ia.log(Level.WARNING, "Item repair failed for " + target.getName());
    }

    private void showHelp(final Player player) {
        player.sendMessage(lang.textHelp);
    }

    private void showHelpConsole() {
        Bukkit.getServer().getConsoleSender().sendMessage(lang.textHelp);
    }

    private void addNBTdata(final String[] args, CommandSender sender, DataType type) {

        // 0 = addnbt, 1 = Player, 2 = key:value

        if (args.length < 3) {
            String msg = "Not enough arguments!";
            if (sender != null)  sender.sendMessage(lang.textPrefix + ChatColor.RED + msg);
            ia.log(Level.INFO, msg);
            return;
        }

        // Get player
        Player targetPlayer = Bukkit.getPlayer(args[1]);
        if (targetPlayer == null) {
            String msg = "Cannot find player: " + args[1];
            ia.log(Level.SEVERE, msg);
            if (sender != null) { sender.sendMessage(lang.textPrefix + ChatColor.RED + msg); }
            return;
        }

        // Get item in player hand
        if (targetPlayer.getInventory().getItemInMainHand().getType() == Material.AIR) {
            String msg = "Cannot apply nbt, " + targetPlayer.getName() + " is not holding an item!";
            if (sender != null)  sender.sendMessage(lang.textPrefix + ChatColor.RED + msg);
            ia.log(Level.INFO, msg);
            return;
        }

        ItemStack itemStack = targetPlayer.getInventory().getItemInMainHand();

        String[] subArgs = args[3].split(":", -1);   // Define array of sub arguments
        ItemStack newItem = null;

        if (subArgs.length == 2) {
            if (type == DataType.eInt) {
                try {
                    newItem = NBTEditor.set(itemStack, Integer.parseInt(subArgs[1]), subArgs[0]);
                } catch (Exception e) {
                    String msg = "Invalid argument - not a number: " + args[3];
                    if (sender != null) {
                        sender.sendMessage(lang.textPrefix + ChatColor.RED + msg);
                    } else {
                        ia.log(Level.INFO, msg);
                    }
                }
            } else {
                newItem = NBTEditor.set(itemStack, subArgs[1], subArgs[0]);
            }
            targetPlayer.getInventory().setItem(EquipmentSlot.HAND, newItem);
        } else {
            String msg = "Invalid arguments: " + args[3];
            if (sender != null) {
                sender.sendMessage(lang.textPrefix + ChatColor.RED + msg);
            } else {
                ia.log(Level.INFO, msg);
            }
        }
    }

    private void giveItem(final String[] args, Player player) {

        ia = ItemAttributes.getPlugin(ItemAttributes.class);

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
                    if (player != null) { player.sendMessage(lang.textPrefix + ChatColor.RED + msg); }
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

                // Permission
                String perm = config.getString("items." + item + ".Perm");   // Get current argument
                if (perm != null) {
                    newArg.add("perm:" + perm); // Arg[3+] >> Lore
                }

                // Fixable
                String fixable = config.getString("items." + item + ".Fixable");   // Get current argument
                if (fixable != null) {
                    newArg.add("fixable:" + fixable); // Arg[3+]
                }

                // Item ID
                newArg.add("itemid:" + item); // Arg[3+]

                // Maxduradamage
                String maxduradmg = config.getString("items." + item + ".MaxDuraDamage");   // Get current argument
                if (maxduradmg != null) {
                    newArg.add("perm:" + maxduradmg); // Arg[3+]
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

                // NBT data
                try {
                    String[] nbtstring = config.getString("items." + item + ".Tags").split(" ");   // Get data
                    if (nbtstring.length > 0) {
                        for (String i:nbtstring) {
                            newArg.add("nbtstring:" + i);
                        }
                    }
                } catch(NullPointerException ignored) {}
                try {
                    String[] nbtint = config.getString("items." + item + ".TagsInt").split(" ");   // Get data
                    if (nbtint.length > 0) {
                        for (String i:nbtint) {
                            newArg.add("nbtint:" + i);
                        }
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
            if (player != null) { player.sendMessage(lang.textPrefix + ChatColor.RED + msg); }
            return;
        }
        // Create item stack
        Material mat = Material.matchMaterial(args[2]);
        if (mat == null) {
            String msg = "Unknown material: " + args[2];
            ia.log(Level.SEVERE, msg);
            if (player != null) { player.sendMessage(lang.textPrefix + ChatColor.RED + msg); }
            return;
        }

        // Get item stack
        ItemStack itemStack = new ItemStack(mat);

        // Custom NBT Tags
        for (int i = 3; i < args.length; i++) {

            String arg = args[i];   // Get current argument
            String[] subArgs = arg.split(":", -1);   // Define array of sub arguments

            if (arg.toLowerCase().contains("nbtstring:")) {
                if (subArgs.length == 3) {
                    itemStack = NBTEditor.set(itemStack, subArgs[2], subArgs[1]);
                } else {
                    String msg = "Invalid nbt arguments: " + arg;
                    player.sendMessage(lang.textPrefix + ChatColor.RED + msg);
                    return;
                }
            } else if (arg.toLowerCase().contains("nbtint:")) {
                if (subArgs.length == 3) {
                    try {
                        itemStack = NBTEditor.set(itemStack, Integer.parseInt(subArgs[2]), subArgs[1]);
                    } catch(Exception e) {
                        String msg = "Invalid nbt integer: " + arg + ". Value must be an integer.";
                        player.sendMessage(lang.textPrefix + ChatColor.RED + msg);
                        return;
                    }
                } else {
                    String msg = "Invalid nbt arguments: " + arg;
                    player.sendMessage(lang.textPrefix + ChatColor.RED + msg);
                    return;
                }
            }
        }

        // Grab meta data after assigning nbt tags
        ItemMeta meta = itemStack.getItemMeta();
        assert meta != null;

        // Set item meta based on args
        for (int i = 3; i < args.length; i++) {

            // 0 = give, 1 = Player, 2 = Item, 3+ = name/lore/unbreaking/attributes/enchants

            String arg = args[i];   // Get current argument
            String[] subArgs = arg.split(":", -1);   // Define array of sub arguments

            // Skip AE Items
            if (ia.aeSupport) {
                if (AEAPI.getAllEnchantments().contains(subArgs[0])) { continue; }
            }

            // Skip nbt arg
            if (arg.toLowerCase().contains("nbtstring:") || arg.toLowerCase().contains("nbtint:")) { continue; }

            // Check if sub arguments are provided
            if (subArgs.length >= 2) {
                // Set item name
                if (arg.toLowerCase().contains("name:")) {
                    // Set the items name
                    String name = HexEditor.translateHexColorCodes("&#","",arg.replace("name:", "").replace("_", " "));
                    meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
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


            // Set Permission tag
            // Set perm as persistent data container
            if (arg.toLowerCase().contains("perm:")) {
                NamespacedKey key = new NamespacedKey(ia, "required-perm");
                meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, arg.replace("perm:", ""));
                continue;
            }

            // Set Fixable tag
            // Set perm as persistent data container
            if (arg.toLowerCase().contains("fixable:")) {
                String input = arg.replace("fixable:", "");
                if (!input.equals("true") && !input.equals("false")) {
                    String msg = "Fixable must be true or false: " + arg;
                    ia.log(Level.SEVERE, msg);
                    if (player != null) { player.sendMessage(lang.textPrefix + ChatColor.RED + msg); }
                    return;
                }
                NamespacedKey key = new NamespacedKey(ia, "fixable");
                meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, input);
                continue;
            }


            // Set Max Dura
            // Set the maximum durability loss from a single hit
            if (arg.toLowerCase().contains("maxduradamage:")) {
                try {
                    Integer.parseInt(subArgs[1]); // Test if a number
                    NamespacedKey key = new NamespacedKey(ia, "maxduradamage");
                    meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, arg.replace("maxduradamage:", ""));
                    continue;
                }
                catch (NumberFormatException ex) {
                    String msg = "Not a number: " + arg;
                    ia.log(Level.SEVERE, msg);
                    if (player != null) { player.sendMessage(lang.textPrefix + ChatColor.RED + msg); }
                    return;
                }
            }


            // Set Enchantments
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
                    if (player != null) { player.sendMessage(lang.textPrefix + ChatColor.RED + msg); }
                    return;
                }
            }

            // Set Attributes
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
                float num;

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
                        if (player != null) { player.sendMessage(lang.textPrefix + ChatColor.RED + msg); }
                        return;
                    }
                    // Verify numbers in range
                    for (int ii = 0; ii < argRange.length; ii++) {
                        try {
                            range[ii] = (float) Double.parseDouble(argRange[ii]);
                        } catch (NumberFormatException ex) {
                            String msg = "Cannot parse number: " + argRange[ii];
                            ia.log(Level.SEVERE, msg);
                            if (player != null) { player.sendMessage(lang.textPrefix + ChatColor.RED + msg); }
                            return;
                        }
                    }
                    // 1 - 10
                    // Get random number
                    if (range[0] >= range[1]) {
                        String msg = "Min cannot be higher than max: " + Arrays.toString(argRange);
                        ia.log(Level.SEVERE, msg);
                        if (player != null) { player.sendMessage(lang.textPrefix + ChatColor.RED + msg); }
                        return;
                    }
                    num = ((float)Math.random() * (range[1] - range[0])) + range[0];
                } else {
                    try { // Verify number
                        num = (float) Double.parseDouble(numArg);
                    } catch (NumberFormatException ex) {
                        String msg = "Cannot parse number: " + numArg;
                        ia.log(Level.SEVERE, msg);
                        if (player != null) { player.sendMessage(lang.textPrefix + ChatColor.RED + msg); }
                        return;
                    }
                }

                EquipmentSlot slot = EquipmentSlot.HAND;
                AttributeModifier modifier;

                // Check if slot provided
                if (subArgs.length > 2) {
                    switch (subArgs[2].toLowerCase()) {
                        case "hand": break;
                        case "chest": slot = EquipmentSlot.CHEST; break;
                        case "feet": slot = EquipmentSlot.FEET; break;
                        case "head": slot = EquipmentSlot.HEAD; break;
                        case "legs": slot = EquipmentSlot.LEGS; break;
                        case "offhand": slot = EquipmentSlot.OFF_HAND; break;
                        default:
                            String msg = "Unknown slot: " + subArgs[2];
                            ia.log(Level.SEVERE, msg);
                            if (player != null) { player.sendMessage(lang.textPrefix + ChatColor.RED + msg); }
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
                    else if (iName.contains("elytra")) slot = EquipmentSlot.CHEST;
                }

                modifier = new AttributeModifier(UUID.randomUUID(), subArgs[0], num, attMod, slot);
                meta.addAttributeModifier(attribute,modifier);
                continue;
            }

            // Set item ID
            // Set ID as persistent data container
            if (arg.toLowerCase().contains("itemid:")) {
                String input = arg.replace("itemid:", "");
                NamespacedKey key = new NamespacedKey(ia, "itemID");
                meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, input);
                continue;
            }

            // Catch all non-recognized arguments
            String msg ="Unknown argument: " + arg;
            ia.log(Level.SEVERE, msg);
            if (player != null) { player.sendMessage(lang.textPrefix + ChatColor.RED + msg); }
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
                        itemStack = NBTEditor.set(itemStack, Integer.parseInt(subArgs[1]), "ae_enchantment;" + subArgs[0]);
                    }
                    catch (ArrayIndexOutOfBoundsException ex) {
                        AEAPI.applyEnchant(ea, 1, itemStack);
                        itemStack = NBTEditor.set(itemStack, 1, "ae_enchantment;" + subArgs[0]);
                    }
                    catch (NumberFormatException ex) {
                        String msg = "Not a number: " + args[i];
                        ia.log(Level.SEVERE, msg);
                        if (player != null) { player.sendMessage(lang.textPrefix + ChatColor.RED + msg); }
                        return;
                    }
                }
            }
        } // End AE Support

        // Construct console message
        String totalModifiers = (meta.getAttributeModifiers() == null) ? "" : " Attributes:" + meta.getAttributeModifiers().size();
        String totalEnchantments = (itemStack.getEnchantments().size() == 0) ? "" : " Enchants:" + itemStack.getEnchantments().size();
        String itemName = (meta.hasDisplayName()) ? meta.getDisplayName() + ChatColor.GOLD + " (" + itemStack.getType().name() + ")" : itemStack.getType().name();

        String msg = "Gave " + targetPlayer.getName() + ": " + itemName + totalEnchantments + totalModifiers;
        ia.log(Level.INFO, msg);

        // Send player message
        if (player != null) { player.sendMessage(lang.textPrefix + ChatColor.GOLD + msg); }

        // Give item to player
        // Check if inventory is full
        if (targetPlayer.getInventory().firstEmpty() == -1) {
            // Drop item on player
            targetPlayer.getWorld().dropItem(targetPlayer.getLocation(), itemStack);
            // Send message to recipient
            targetPlayer.sendMessage(ChatColor.YELLOW + "No inventory space! " + meta.getDisplayName() + ChatColor.YELLOW + " was dropped on the ground!");
        } else {
            // Send item to player inventory
            targetPlayer.getInventory().addItem(itemStack);
            // Send message to recipient
            if (meta.hasDisplayName()) {
                targetPlayer.sendMessage(ChatColor.YELLOW + "You've recieved a " + meta.getDisplayName());
            } else {
                targetPlayer.sendMessage(ChatColor.YELLOW + "You've recieved a " + itemStack.getType().toString().replace("_", " ").toLowerCase());
            }
        }

    } // End Method
} // End Class
