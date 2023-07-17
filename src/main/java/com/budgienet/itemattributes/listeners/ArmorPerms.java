package com.budgienet.itemattributes.listeners;

import com.budgienet.itemattributes.ItemAttributes;
import com.budgienet.itemattributes.configs.LangConfig;
import com.jeff_media.armorequipevent.ArmorEquipEvent;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class ArmorPerms implements Listener {

    ItemAttributes ia = ItemAttributes.getPlugin(ItemAttributes.class);

    @EventHandler (priority = EventPriority.HIGH)
    public void onArmorEquip(ArmorEquipEvent event) {
        ItemStack item = event.getNewArmorPiece();
        if (item == null) return;
        Player p = event.getPlayer();

        NamespacedKey key = NamespacedKey.fromString("required-perm", ia);
        if (key == null) return;

        ItemMeta itemMeta = item.getItemMeta();
        if (itemMeta == null) return;

        PersistentDataContainer container = itemMeta.getPersistentDataContainer();

        if (container.has(key , PersistentDataType.STRING)) {
            String perm = container.get(key, PersistentDataType.STRING);
            if (perm == null) return;
            if (!p.hasPermission(perm)) {
                p.sendMessage(LangConfig.instance.textNoArmourPerms);
                event.setCancelled(true);
            }
        }
    }
}
