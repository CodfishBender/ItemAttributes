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
    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {

        // First arg
        if (args.length == 1) {
            return ia.autoList1;
        }
        if (Objects.equals(args[0], "giveitem")) {
            if (args.length == 2) {
                return null;
            }
            if (args.length == 3) {
                return ItemsConfig.instance.items;
            }
        }
        if (Objects.equals(args[0], "give")) {
            // Second arg
            if (args.length == 2) {
                List<String> names = new ArrayList<>();
                Player[] players = new Player[Bukkit.getServer().getOnlinePlayers().size()];
                Bukkit.getServer().getOnlinePlayers().toArray(players);
                for (Player player : players) {
                    if (player.getName().contains(args[args.length - 1])) {
                        names.add(player.getName());
                    }
                }
                return names;
            }
            // Third arg
            if (args.length == 3) {
                ia.materialNames.clear();
                for (Material mat : Material.values()) {
                    String name = mat.name().toLowerCase();
                    if (name.contains(args[args.length - 1])) {
                        ia.materialNames.add(mat.name().toLowerCase());
                    }
                }
                return ia.materialNames;
            }
            // Fourth+ arg
            ia.autoList2.clear();
            String[] subArgs = args[args.length - 1].split(":", -1);
            if (subArgs.length == 1) {
                if (args[args.length - 1].contains(":")) {
                    ia.autoList2.add(subArgs[0] + ":<number>");
                    return ia.autoList2;
                }
            } else if (subArgs.length == 2) {
                switch (subArgs[0]) {
                    case "name":
                        ia.autoList2.add(subArgs[0] + ":&a&lExample_name");
                        return ia.autoList2;
                    case "lore":
                        ia.autoList2.add(subArgs[0] + ":&cExample_lore|&7New_line");
                        return ia.autoList2;
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
                            ia.autoList2.add(subArgs[0] + ":<min>~<max>");
                            return ia.autoList2;
                    }
                }
                ia.autoList2.add(subArgs[0] + ":<number>");
                return ia.autoList2;
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
                        ia.autoList2.add(subArgs[0] + ":" + subArgs[1]);
                        return ia.autoList2;
                }
                ia.autoList2.add(subArgs[0] + ":" + subArgs[1] + ":head");
                ia.autoList2.add(subArgs[0] + ":" + subArgs[1] + ":chest");
                ia.autoList2.add(subArgs[0] + ":" + subArgs[1] + ":legs");
                ia.autoList2.add(subArgs[0] + ":" + subArgs[1] + ":feet");
                ia.autoList2.add(subArgs[0] + ":" + subArgs[1] + ":hand");
                ia.autoList2.add(subArgs[0] + ":" + subArgs[1] + ":offhand");
                return ia.autoList2;
            }
            // Other
            if ("name:".contains(args[args.length - 1])) {
                ia.autoList2.add("name:");
            }
            if ("lore:".contains(args[args.length - 1])) {
                ia.autoList2.add("lore:");
            }
            // Enchants
            for (String s : ia.enchNames) {
                if (s.contains(args[args.length - 1])) {
                    ia.autoList2.add(s);
                }
            }
            // Attributes
            for (String s : ia.enchNames) {
                if (s.contains(args[args.length - 1])) {
                    ia.autoList2.add(s);
                }
            }
            for (int i = 0; i < ia.attrNames.length; i++) {
                if (ia.attrNames[i][0].contains(args[args.length - 1])) {
                    ia.autoList2.add(ia.attrNames[i][0]);
                }
            }
            // Flags
            for (String s : ia.flagNames) {
                if (s.contains(args[args.length - 1])) {
                    ia.autoList2.add(s);
                }
            }
            // AE Support
            if (ia.aeSupport) {
                for (String s : AEAPI.getAllEnchantments()) {
                    if (s.contains(args[args.length - 1])) {
                        ia.autoList2.add(s);
                    }
                }
            }
            return ia.autoList2;
        }

        // Catch unknown args
        List<String> c = new ArrayList<>();
        c.add("");
        return c;
    }
}
