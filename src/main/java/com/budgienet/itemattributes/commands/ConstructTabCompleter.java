package com.budgienet.itemattributes.commands;

import com.budgienet.itemattributes.ItemAttributes;
import com.budgienet.itemattributes.configs.ItemsConfig;
import net.advancedplugins.ae.api.AEAPI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ConstructTabCompleter implements TabCompleter {

    private final ItemAttributes ia = ItemAttributes.getPlugin(ItemAttributes.class);

    List<String> GetPlayerList(String[] args) {
        List<String> names = new ArrayList<>();

        Player[] players = new Player[Bukkit.getServer().getOnlinePlayers().size()];
        for (Player player : Bukkit.getServer().getOnlinePlayers().toArray(players)) {
            if (player.getName().toLowerCase().contains(args[args.length - 1])) {
                names.add(player.getName());
            }
        }
        return names;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {

        // Clear autocomplete list
        ia.autoList.clear();
        String lastArg = args[args.length - 1].toLowerCase();

        // Make all args lowercase
        for (int i = 0; i < args.length; i++) {
            args[i] = args[i].toLowerCase();
        }

        if (args.length == 1) {
            for (String i : ia.autoList1) {
                if (i.toLowerCase().contains(args[0])) {
                    ia.autoList.add(i);
                }
            }
            return ia.autoList;
        }
        else if (Objects.equals(args[0].toLowerCase(), "giveitem")) {
            if (args.length == 2) {
                return null;
            }
            else if (args.length == 3) {
                for (String i : ItemsConfig.instance.items) {
                    if (i.toLowerCase().contains(args[2].toLowerCase())) {
                        ia.autoList.add(i);
                    }
                }
                return ia.autoList;
            }
        }
        else if (Objects.equals(args[0].toLowerCase(), "addenchant")) {
            if (args.length == 2) {
                return GetPlayerList(args);
            }
            else if (args.length == 3) {
                /*
                for (String i:ia.enchNames) {
                    if (i.toLowerCase().contains(lastArg)) ia.autoList.add(i);
                }
                for (String i:ia.flagNames) {
                    if (i.toLowerCase().contains(lastArg)) ia.autoList.add(i);
                }
                for (String[] i:ia.attrNames) {
                    if (i[0].toLowerCase().contains(lastArg)) ia.autoList.add(i[0]);
                }
                */
                 if (ia.aeSupport) {
                    for (String i:AEAPI.getAllEnchantments()) {
                        if (i.toLowerCase().contains(lastArg)) ia.autoList.add(i);
                    }
                }
            }
            String[] subArgs = lastArg.split(":", -1);
            if (subArgs.length > 1) {
                ia.autoList.clear();
                ia.autoList.add(subArgs[0] + ":<number>");
                return ia.autoList;
            }
            return ia.autoList;
        }
        else if (Objects.equals(args[0].toLowerCase(), "fixitem")) {
            if (args.length == 2) {
                return GetPlayerList(args);
            }
            return ia.autoList;
        }
        else if (Objects.equals(args[0].toLowerCase(), "addnbt")) {
            if (args.length == 2) {
                return GetPlayerList(args);
            } else if (args.length == 3){
                ia.autoList.add("string");
                ia.autoList.add("int");
                return ia.autoList;
            } else if (args.length == 4) {
                ia.autoList.add("key:value");
                return ia.autoList;
            }
            return ia.autoList;
        }
        else if (Objects.equals(args[0].toLowerCase(), "give")) {
            // Second arg - Players
            if (args.length == 2) {
                ia.autoList.addAll(GetPlayerList(args));
                ia.autoList.add("help");
                return ia.autoList;
            }
            // Third arg - Materials
            if (args.length == 3) {
                for (Material mat : Material.values()) {
                    String name = mat.name().toLowerCase();
                    if (name.contains(lastArg)) {
                        ia.autoList.add(mat.name().toLowerCase());
                    }
                }
                return ia.autoList;
            }
            // Fourth+ arg - Enchants/Attributes
            String[] subArgs = lastArg.split(":", -1);
            if (subArgs.length == 1) {
                if (lastArg.contains(":")) {
                    ia.autoList.add(subArgs[0] + ":<number>");
                    return ia.autoList;
                }
            } else if (subArgs.length == 2) {
                switch (subArgs[0]) {
                    case "name":
                        ia.autoList.add(subArgs[0] + ":&a&lExample_name");
                        return ia.autoList;
                    case "lore":
                        ia.autoList.add(subArgs[0] + ":&cExample_lore|&7New_line");
                        return ia.autoList;
                    case "perm":
                        ia.autoList.add(subArgs[0] + ":perm.node");
                        return ia.autoList;
                    case "nbtstring":
                    case "nbtint":
                        ia.autoList.add(subArgs[0] + ":<key>:<value>");
                        return ia.autoList;
                    default:
                        break;
                }
                if (subArgs[1].contains("~")) {
                    switch (subArgs[0]) {
                        case "armor":
                        case "movespeed":
                        case "toughness":
                        case "damage":
                        case "health":
                        case "knockback":
                        case "attackspeed":
                        case "knockbackres":
                        case "luck":
                            ia.autoList.add(subArgs[0] + ":1~2");
                            return ia.autoList;
                    }
                }
                ia.autoList.add(subArgs[0] + ":1");
                return ia.autoList;
            } else if (subArgs.length == 3) {
                switch (subArgs[0]) {
                    case "armor":
                    case "movespeed":
                    case "toughness":
                    case "damage":
                    case "health":
                    case "knockback":
                    case "attackspeed":
                    case "knockbackres":
                    case "luck":
                        break;
                    default:
                        ia.autoList.add(subArgs[0] + ":" + subArgs[1]);
                        return ia.autoList;
                }
                ia.autoList.add(subArgs[0] + ":" + subArgs[1] + ":head");
                ia.autoList.add(subArgs[0] + ":" + subArgs[1] + ":chest");
                ia.autoList.add(subArgs[0] + ":" + subArgs[1] + ":legs");
                ia.autoList.add(subArgs[0] + ":" + subArgs[1] + ":feet");
                ia.autoList.add(subArgs[0] + ":" + subArgs[1] + ":hand");
                ia.autoList.add(subArgs[0] + ":" + subArgs[1] + ":offhand");
                return ia.autoList;
            }
            // Other
            if ("name:".contains(lastArg)) {
                ia.autoList.add("name:");
            }
            if ("nbtstring:".contains(lastArg)) {
                ia.autoList.add("nbtstring:");
            }
            if ("nbtstring:".contains(lastArg)) {
                ia.autoList.add("nbtint:");
            }
            if ("lore:".contains(lastArg)) {
                ia.autoList.add("lore:");
            }
            if ("perm:".contains(lastArg)) {
                ia.autoList.add("perm:");
            }
            if ("maxduradamage:".contains(lastArg)) {
                ia.autoList.add("maxduradamage:");
            }
            // Enchants
            for (String s : ia.enchNames) {
                if (s.toLowerCase().contains(lastArg)) ia.autoList.add(s);
            }
            // Flags
            for (String s : ia.flagNames) {
                if (s.toLowerCase().contains(lastArg)) ia.autoList.add(s);
            }
            // Attributes
            for (String[] att:ia.attrNames) {
                if (att[0].toLowerCase().contains(lastArg)) ia.autoList.add(att[0]);
            }
            // AE Support
            if (ia.aeSupport) {
                for (String s : AEAPI.getAllEnchantments()) {
                    if (s.contains(lastArg)) {
                        ia.autoList.add(s);
                    }
                }
            }
            return ia.autoList;
        }

        // Catch unknown args
        List<String> c = new ArrayList<>();
        c.add("");
        return c;
    }
}
