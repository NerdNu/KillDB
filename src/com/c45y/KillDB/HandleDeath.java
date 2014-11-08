package com.c45y.KillDB;

import com.c45y.KillDB.database.PvPRating;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.ItemStack;


public class HandleDeath implements Listener
{
	public KillDB plugin;
    public boolean chatTags = true;

	public HandleDeath(KillDB instance) {
		this.plugin = instance;
	}

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onPlayerDeath(PlayerDeathEvent event) {
		Player player = (Player)event.getEntity();
		if ((player.getKiller() instanceof Player)) {
			event.setDeathMessage(""); //We want do do our own messages
			if (isArmorKill(player.getKiller(), player)) { //If they gave up spawn camping
				Location loc = new Location(player.getLocation().getWorld(), player.getLocation().getX(), player.getLocation().getY() + 5.0D, player.getLocation().getZ(), 360.0F, 0.0F);
				event.getEntity().getWorld().strikeLightningEffect(loc);
				deathMessage(player.getKiller().getName(), " took down ", player.getName(), " with a ", prettyItemName(player.getKiller().getItemInHand()));
			} else {
				deathMessage(player.getKiller().getName(), " killed ", player.getName(), " with a ", prettyItemName(player.getKiller().getItemInHand()));
			}
			DataRunnable dr = new DataRunnable(this.plugin, player, player.getKiller());
			dr.start();
		}
	}

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onPlayerChat(AsyncPlayerChatEvent event){
        String message = event.getMessage();
        Player player = event.getPlayer();
        PvPRating[] top5 = this.plugin.pvpRatingTable.top5();
        //Checks to see if the player is in the top5
        if((player.getName().equalsIgnoreCase(top5[0].getPlayerName())|| 
                player.getName().equalsIgnoreCase(top5[1].getPlayerName())||
                player.getName().equalsIgnoreCase(top5[2].getPlayerName())||
                player.getName().equalsIgnoreCase(top5[3].getPlayerName())||
                player.getName().equalsIgnoreCase(top5[4].getPlayerName()))&&
                chatTags){
            event.setCancelled(true);
            if(player.getName().equalsIgnoreCase(top5[0].getPlayerName())){
                plugin.getServer().broadcastMessage("[" + ChatColor.GREEN + "1st"
                        + ChatColor.RESET + "]" + "<" + player.getName() + "> " + message);
            }else if(player.getName().equalsIgnoreCase(top5[1].getPlayerName())){
                plugin.getServer().broadcastMessage("[" + ChatColor.GREEN + "2nd"
                        + ChatColor.RESET + "]" + "<" + player.getName() + "> " + message);
            }else if(player.getName().equalsIgnoreCase(top5[2].getPlayerName())){
                plugin.getServer().broadcastMessage("[" + ChatColor.GREEN + "3rd"
                        + ChatColor.RESET + "]" + "<" + player.getName() + "> " + message);
            }else if(player.getName().equalsIgnoreCase(top5[3].getPlayerName())){
                plugin.getServer().broadcastMessage("[" + ChatColor.GREEN + "4th"
                        + ChatColor.RESET + "]" + "<" + player.getName() + "> " + message);
            }else if(player.getName().equalsIgnoreCase(top5[4].getPlayerName())){
                plugin.getServer().broadcastMessage("[" + ChatColor.GREEN + "5th"
                        + ChatColor.RESET + "]" + "<" + player.getName() + "> " + message);
            }
        }
    }

	public boolean isArmorKill(Player attacker, Player dead_guy) {
		if (dead_guy.getInventory().getChestplate() != null && attacker.getInventory().getChestplate() != null) {
			if (dead_guy.getInventory().getChestplate().getType().getMaxDurability() > 150 && attacker.getInventory().getChestplate().getType().getMaxDurability() > 150) { //was wearing iron or dia armor
				return true;
			}
		}
		return false;
	}

	public String prettyItemName(ItemStack i) {
		String item = i.getType().toString().replace('_', ' ' ).toLowerCase();
		if(item.equals("air")) {
			item = "fist";
		}
		return item;
	}

	public void deathMessage(String killer,String action,String dead_guy,String joiner,String item) {
		plugin.getServer().broadcastMessage(ChatColor.RED + killer + action + dead_guy + joiner + item);
	}
    
    public void setChatTags(Boolean bool){
        this.chatTags = bool;
    }
}