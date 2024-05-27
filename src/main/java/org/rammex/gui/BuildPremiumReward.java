package org.rammex.gui;

import me.arcaniax.hdb.api.HeadDatabaseAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.rammex.Main;
import org.rammex.commands.RewardCommand;

import static org.bukkit.Bukkit.getLogger;

public class BuildPremiumReward implements InventoryHolder, Listener {

    private Inventory inventory1 = null; // Make inventory non-static
    Main plugin;
    Player player;

    public BuildPremiumReward(Main plugin, Player player) {
        this.plugin = plugin;
        this.inventory1 = Bukkit.createInventory(this, 9, ChatColor.GOLD + "Récompenses Premium");
        this.player = player;
        initializeItems1(player); // Passez player en tant que paramètre
    }


    @Override
    public Inventory getInventory() {
        return inventory1;
    }

    private void initializeItems1(Player player) {
        Integer day = plugin.getPlayerData().getInt("players." + player.getName() + ".day");
        String type = plugin.getConfig().getString("rewardspremium." + day + ".type");
        if(type.equals("normal")){
            String materialName = this.plugin.getConfig().getString("rewardspremium."+day+".material");
            Material material = Material.valueOf(materialName);
            ItemStack item = new ItemStack(material, 1);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', this.plugin.getConfig().getString("rewardspremium."+day+".name")));
            item.setItemMeta(meta);

            inventory1.setItem(4, item);
        }
        if(type.equals("hdb")){
            HeadDatabaseAPI api = new HeadDatabaseAPI();
            try {
                ItemStack item = api.getItemHead(this.plugin.getConfig().getString("rewardspremium."+day+".headid"));
                ItemMeta meta = item.getItemMeta();
                meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', this.plugin.getConfig().getString("rewardspremium."+day+".name")));
                item.setItemMeta(meta);

                inventory1.setItem(4, item);

            } catch (NullPointerException nullPointerException) {
                getLogger().info("Could not find the head you were looking for");
            }
        }

        ItemStack items = new ItemStack(Material.STAINED_GLASS_PANE, 1);
        ItemMeta metas = items.getItemMeta();
        metas.setDisplayName(ChatColor.AQUA + "");
        items.setItemMeta(metas);

        inventory1.setItem(0, items);
        inventory1.setItem(1, items);
        inventory1.setItem(2, items);
        inventory1.setItem(3, items);
        inventory1.setItem(5, items);
        inventory1.setItem(6, items);
        inventory1.setItem(7, items);
        inventory1.setItem(8, items);
    }

    public void openInventory1(Player p) {
        p.openInventory(inventory1);
    }

    @EventHandler
    public void onInventoryClick1(InventoryClickEvent e) {
        if (e.getInventory().getHolder() != this) {
            return;
        }
        e.setCancelled(true);

        final ItemStack clickedItem = e.getCurrentItem();

        if (clickedItem == null || clickedItem.getType() == Material.AIR) return;

    }

}
