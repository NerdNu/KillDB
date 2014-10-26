package com.c45y.KillDB;

import org.bukkit.entity.Player;
import com.c45y.KillDB.database.PvPRating;
import com.c45y.KillDB.database.DeathStat;

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
	public void run ()
	{
		DeathStat stat = new DeathStat();
		stat.setPlayerName(this.player.getName());
		stat.setKillerName(this.killer.getName());
		stat.setKillerItem(this.killer.getItemInHand().getType().toString());
		stat.setUsedInRating(true);
        stat.setTimestamp(System.currentTimeMillis());
        int killerRating = this.plugin.pvpRatingTable.getPlayerRating(this.killer.getName());
	int playerRating = this.plugin.pvpRatingTable.getPlayerRating(this.player.getName());
        int[] gsArray = (new GearScore(this.player,playerRating,this.killer,killerRating)).getChange();
        this.plugin.pvpRatingTable.updatePlayerRating(this.player.getName(), gsArray[0]); // Updates killer rating
        this.plugin.pvpRatingTable.updatePlayerRating(this.killer.getName(), gsArray[1]); // Updates victim rating
        stat.setRatingChange(gsArray[2]);
        this.plugin.deathStatTable.save(stat);
        this.plugin.deathStatTable.cleanup(this.player,this.killer);
        
		
	}
}