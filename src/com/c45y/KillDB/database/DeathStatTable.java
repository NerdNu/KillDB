package com.c45y.KillDB.database;

import com.avaje.ebean.Query;
import com.c45y.KillDB.KillDB;
import java.util.List;
import java.util.Map;
import org.bukkit.entity.Player;

public class DeathStatTable {

	KillDB plugin;
	
	public DeathStatTable(KillDB plugin) {
		this.plugin = plugin;
	}
	
	public DeathStat getRequest(int id) {
		DeathStat retVal = null;
		
		Query<DeathStat> query = plugin.getDatabase().find(DeathStat.class).where().eq("id", id).query();
		
		if (query != null) {
			retVal = query.findUnique();
		}
		
		return retVal;
	}
	
	public void cleanup(Player victim, Player killer){
		while(true){
			Query<DeathStat> killQuery = plugin.getDatabase().find(DeathStat.class).where().ieq("killerName", killer.getName()).ieq("victimName", victim.getName()).ieq("usedInRating", "true").query();
			if(killQuery != null){
				if(killQuery.findRowCount() > 10){
					List<DeathStat> killList = killQuery.findList();
					DeathStat earliest = new DeathStat();
                                        earliest.setTimestamp(System.currentTimeMillis());
					for(DeathStat stat : killList){
						if(stat.getTimestamp() < earliest.getTimestamp()){
							earliest = stat;
						}
					}
					this.plugin.pvpRatingTable.updatePlayerRating(earliest.getKillerName(),
							(this.plugin.pvpRatingTable.getPlayerRating(earliest.getKillerName())-earliest.getRatingChange()));
					this.plugin.pvpRatingTable.updatePlayerRating(earliest.getPlayerName(),
							(this.plugin.pvpRatingTable.getPlayerRating(earliest.getPlayerName())+earliest.getRatingChange()));
					earliest.setUsedInRating(false);
					this.save(earliest);
				}else{
					break;
				}
			}else{
				break;
			}
		}
		while(true){
			Query<DeathStat> killQuery = plugin.getDatabase().find(DeathStat.class).where().ieq("usedInRating", "true").query();
			if(killQuery != null){
				List<DeathStat> timeList = killQuery.findList();
				DeathStat limit = new DeathStat();
                                limit.setTimestamp(System.currentTimeMillis() - 1209600000L);
				for(DeathStat stat : timeList){
					if(stat.getTimestamp() < limit.getTimestamp()){
						this.plugin.pvpRatingTable.updatePlayerRating(stat.getKillerName(),
								(this.plugin.pvpRatingTable.getPlayerRating(stat.getKillerName())-stat.getRatingChange()));
						this.plugin.pvpRatingTable.updatePlayerRating(stat.getPlayerName(),
								(this.plugin.pvpRatingTable.getPlayerRating(stat.getPlayerName())+stat.getRatingChange()));
						stat.setUsedInRating(false);
					}
				}
			}else{
				break;
			}
		}
	}
	
	public void save(DeathStat deathstat) {
		plugin.getDatabase().save(deathstat);
	}
	
}
