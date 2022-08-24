package com.budgienet.itemattributes;

import com.budgienet.itemattributes.configs.MainConfig;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class WeaponPerms implements Listener {

    // Cancel damage event if damaged by the player without the weapon permission
    @EventHandler (priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onDamage(EntityDamageByEntityEvent event) {

        Player p;
        if (event.getDamager() instanceof Player) { p = (Player) event.getDamager(); } else { return; }

        ItemStack itemStack = p.getInventory().getItem(EquipmentSlot.HAND);
        if (itemStack == null) return;

        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null) return;

        NamespacedKey key = NamespacedKey.fromString("required-perm", ItemAttributes.getPlugin(ItemAttributes.class));
        if (key == null) return;

        PersistentDataContainer container = itemMeta.getPersistentDataContainer();

        if (container.has(key , PersistentDataType.STRING)) {
            String perm = container.get(key, PersistentDataType.STRING);
            if (perm == null) return;
            if (!p.hasPermission(perm)) {
                event.setCancelled(true);
                String msg = MainConfig.instance.getConfig().getString("ItemPermissionMessage");
                if (msg == null) msg = ChatColor.RED + "You cannot use this weapon at your rank.";
                p.sendMessage(msg);
            }
        }
    }

}
