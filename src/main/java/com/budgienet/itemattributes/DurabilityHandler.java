package com.budgienet.itemattributes;

import com.budgienet.itemattributes.configs.LangConfig;
import com.budgienet.itemattributes.configs.MainConfig;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.Objects;

public class DurabilityHandler implements Listener {

    ItemAttributes ia = ItemAttributes.getPlugin(ItemAttributes.class);
    LangConfig lang = LangConfig.instance;

    @EventHandler (priority = EventPriority.HIGH)
    public void onFixCommand(PlayerCommandPreprocessEvent event) {

        String cmd = event.getMessage();
        String[] fixCmds = {"/repair","/erepair","/fix","/efix","/essentials:repair","/essentials:erepair","/essentials:fix","/essentials:efix"};

        for (final String fixCmd : fixCmds) {
            if (cmd.contains(fixCmd)) {

                Player p = event.getPlayer();
                ItemStack itemInMainHand = p.getInventory().getItemInMainHand();

                if (cmd.contains(" all")) {

                    ItemStack[] allItems = p.getInventory().getContents();
                    for (final ItemStack item : allItems) {
                        if (item == null) continue;
                        if (notFixable(item)) {
                            String msg = lang.textNotFixableAll;
                            msg = msg.replace("{item}",item.getItemMeta().getDisplayName());
                            p.sendMessage(msg);
                            event.setCancelled(true);
                            return;
                        }
                    }
                } else {
                    if (notFixable(itemInMainHand)) {
                        p.sendMessage(lang.textNotFixable);
                        event.setCancelled(true);
                    }
                }
            }
        }
    }

    private boolean notFixable(ItemStack item) {

        if (item.getItemMeta() == null) return false;

        // If item name is on fix blacklist
        if (item.getItemMeta().hasDisplayName()) {
            for (final String name : MainConfig.instance.itemFixBlacklist) {
                if (item.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&',name))) {
                    return true;
                }
            }
        }

        // If item has fixable tag set to false
        NamespacedKey key = NamespacedKey.fromString("fixable", ia);
        if (key == null) return false;
        ItemMeta itemMeta = item.getItemMeta();
        if (itemMeta == null) return false;
        PersistentDataContainer container = itemMeta.getPersistentDataContainer();
        if (container.has(key, PersistentDataType.STRING)) {
            return Objects.equals(container.get(key, PersistentDataType.STRING), "false");
        }
        return false;
    }

    @EventHandler (priority = EventPriority.HIGH)
    public void onDuraLost(PlayerItemDamageEvent event) {
        ItemStack item = event.getItem();

        NamespacedKey key = NamespacedKey.fromString("maxduradamage", ia);
        if (key == null) return;

        ItemMeta itemMeta = item.getItemMeta();
        if (itemMeta == null) return;

        PersistentDataContainer container = itemMeta.getPersistentDataContainer();

        if (container.has(key , PersistentDataType.STRING)) {
            String maxDuraDamage = container.get(key, PersistentDataType.STRING);
            try {
                int n = Integer.parseInt(maxDuraDamage);
                if (event.getDamage() >= n) event.setDamage(n);
            }
            catch(Exception e){
                ia.log("SEVERE", "maxduradamage ");
            }
        }
    }
}
