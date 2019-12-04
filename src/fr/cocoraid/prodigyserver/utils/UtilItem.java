package fr.cocoraid.prodigyserver.utils;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import java.util.Arrays;

public class UtilItem {


    private ItemStack itemStack;
    public UtilItem(Material material, String displayname) {
        this.itemStack = new ItemStack(material);
        ItemMeta meta = itemStack.getItemMeta();
        meta.setDisplayName(displayname);
        itemStack.setItemMeta(meta);
    }

    public UtilItem setLore(String... lore) {
        ItemMeta meta = itemStack.getItemMeta();
        meta.setLore(Arrays.asList(lore));
        itemStack.setItemMeta(meta);
        return this;
    }

    public ItemStack build() {
        return itemStack;
    }


    public static ItemStack setDisplayName(ItemStack item, String name) {
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        item.setItemMeta(meta);
        return item;
    }


    public static ItemStack getColorArmor(Material m, Color c) {
        ItemStack i = new ItemStack(m, 1);
        LeatherArmorMeta meta = (LeatherArmorMeta) i.getItemMeta();
        meta.setColor(c);
        i.setItemMeta(meta);
        return i;
    }

    public static int getAmount(Player p, Material m) {
        int amount = 0;
        for (int i = 0; i < 64; i++) {
            ItemStack slot = p.getInventory().getItem(i);
            if (slot == null || slot.getType() != m)
                continue;
            amount += slot.getAmount();
        }
        return amount;
    }



    public static void removeItemAmount(Player p, int amount , Material m) {
        for(int i = 0; i < p.getInventory().getSize(); i++){
            ItemStack itm = p.getInventory().getItem(i);
            if(itm != null && itm.getType().equals(m)){
                int amt = itm.getAmount() - amount;
                itm.setAmount(amt);
                p.getInventory().setItem(i, amt > 0 ? itm : null);
                p.updateInventory();
                break;
            }
        }
    }

}
