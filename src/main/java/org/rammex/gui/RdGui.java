package org.rammex.gui;

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


public class RdGui implements InventoryHolder, Listener {

    private final Inventory inventory;
    Main plugin;

    public RdGui(Main plugin) {
        this.inventory = Bukkit.createInventory(this, 27, ChatColor.BLUE + "Reward Journalier");
        initializeItems();
        this.plugin = plugin;
    }


    @Override
    public Inventory getInventory() {
        return inventory;
    }

    private void initializeItems() {
        ItemStack item = new ItemStack(Material.GOLD_INGOT, 1);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.GOLD + "Récompense Gratuite");
        item.setItemMeta(meta);

        inventory.setItem(12, item);

        ItemStack items = new ItemStack(Material.DIAMOND, 1);
        ItemMeta metas = items.getItemMeta();
        metas.setDisplayName(ChatColor.AQUA + "Récompense Premium");
        items.setItemMeta(metas);

        inventory.setItem(14, items);
    }

    public void openInventory(Player p) {
        p.openInventory(inventory);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        e.setCancelled(true);

        final ItemStack clickedItem = e.getCurrentItem();

        if(clickedItem.getType() == Material.GOLD_INGOT && clickedItem.hasItemMeta() && clickedItem.getItemMeta().getDisplayName().equals(ChatColor.GOLD + "Récompense Gratuite")){
            e.getWhoClicked().closeInventory();
            Player player = (Player) e.getWhoClicked();
            BuildFreeReward buildFreeReward = new BuildFreeReward(plugin, player);
            buildFreeReward.openInventory2(player);
        }else if(clickedItem.getType() == Material.DIAMOND && clickedItem.hasItemMeta() && clickedItem.getItemMeta().getDisplayName().equals(ChatColor.AQUA + "Récompense Premium")) {
            e.getWhoClicked().closeInventory();
            if(e.getWhoClicked().hasPermission(this.plugin.getConfig().getString("premiumperm"))) {
                Player player = (Player) e.getWhoClicked();
                BuildPremiumReward buildPremiumReward = new BuildPremiumReward(plugin, player);
                buildPremiumReward.openInventory1(player);
            } else {
                e.getWhoClicked().sendMessage(ChatColor.RED + "Vous n'avez pas la permission d'ouvrir cette récompense.");
            }
        }
        if(clickedItem.getType() == Material.STAINED_GLASS_PANE) {
            return;
        }

        Player p = (Player) e.getWhoClicked();
        if (clickedItem.hasItemMeta() && clickedItem.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', this.plugin.getConfig().getString("rewards." + plugin.getPlayerData().getInt("players." + p.getName() + ".day") + ".name")))){
            p.closeInventory();
            ((RewardCommand) this.plugin.getCommand("rd").getExecutor()).checkReward(p, this.plugin.getPlayerData().getInt("players." + p.getName() + ".day"));
        }
        if (clickedItem.hasItemMeta() && clickedItem.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', this.plugin.getConfig().getString("rewardspremium." + plugin.getPlayerData().getInt("players." + p.getName() + ".day") + ".name")))){
            p.closeInventory();
            ((RewardCommand) this.plugin.getCommand("rd").getExecutor()).checkRewardPremium(p, this.plugin.getPlayerData().getInt("players." + p.getName() + ".day")   );
        }


    }
}