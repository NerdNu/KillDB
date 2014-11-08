package com.c45y.KillDB;

import org.bukkit.entity.Player;
import com.c45y.KillDB.database.DeathStat;
import org.bukkit.ChatColor;

class DataRunnable extends Thread
{
	KillDB plugin = null;
	Player player = null;
	Player killer = null;
	
	DataRunnable(KillDB p, Player pl, Player kl) {
		this.plugin = p;
		this.player = pl;
		this.killer = kl;
	}
	
    @Override
    public void run ()
	{
        DeathStat stat = new DeathStat();
        stat.setPlayerName(this.player.getName());
        stat.setKillerName(this.killer.getName());
        stat.setKillerItem(this.killer.getItemInHand().getType().toString());
        stat.setUsedInRating(1);
        stat.setTimestamp(System.currentTimeMillis());
        int killerRating = this.plugin.pvpRatingTable.getPlayerRating(this.killer.getName());
        int playerRating = this.plugin.pvpRatingTable.getPlayerRating(this.player.getName());
        int[] gsArray = (new GearScore(this.player,playerRating,this.killer,killerRating)).getChange();
        this.plugin.pvpRatingTable.updatePlayerRating(this.killer.getName(), gsArray[1]); // Updates victim rating
        this.plugin.pvpRatingTable.updatePlayerRating(this.player.getName(), gsArray[0]); // Updates killer rating
        stat.setRatingChange(gsArray[2]);
        this.plugin.deathStatTable.save(stat);
        int decayChange = this.plugin.deathStatTable.cleanup(this.player,this.killer);
        if((gsArray[2]-decayChange)==1){
            this.killer.sendMessage(ChatColor.GREEN + "You gained 1 point from your kill!");
            this.player.sendMessage(ChatColor.RED + "You lost 1 point from your death!");
        }else if((gsArray[2]-decayChange)>1){
            this.killer.sendMessage(ChatColor.GREEN + "You gained "
                    + (gsArray[2]-decayChange) + " points from your kill!");
            this.player.sendMessage(ChatColor.RED + "You lost "
                    + (gsArray[2]-decayChange) + " points from your death!");
        }else{
            this.killer.sendMessage(ChatColor.YELLOW + "That kill was not "
                    + "beneficial to your rating. Try fighting other players!");
            this.player.sendMessage(ChatColor.YELLOW + "Luckily, that death "
                    + "did not harm your rating. Be more careful next time!");
        }
        this.plugin.pvpRatingTable.cleanupTop5();
	}
}