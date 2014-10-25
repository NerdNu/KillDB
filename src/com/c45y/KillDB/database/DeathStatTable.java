package com.c45y.KillDB.database;

import com.avaje.ebean.Query;
import com.c45y.KillDB.KillDB;

public class DeathStatTable {

	KillDB plugin;
	
	public DeathStatTable(KillDB plugin) {
		this.plugin = plugin;
	}
	
	/*
	public int getNumKills(String username) {
		int retVal = 0;
		Query<DeathStat> query = plugin.getDatabase().find(DeathStat.class).where().ieq("killerName", username).query();
		
		if (query != null) {
			retVal = query.findRowCount();
		}
		
		return retVal;
	}
	*/
	
	/*
	public int getNumDeaths(String username) {
		int retVal = 0;
		Query<DeathStat> query = plugin.getDatabase().find(DeathStat.class).where().ieq("playerName", username).query();
		
		if (query != null) {
			retVal = query.findRowCount();
		}
		
		return retVal;
	}
	*/
	
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
			Query<DeathStat> killQuery = plugin.getDatabase().find(DeathStat.class).where().ieq("killerName", killer).ieq("victimName", victim).ieq("usedInRating", true).query();
			if(killQuery != null){
				if(killQuery.findRowCount() > 10){
					Map<Long,DeathStat> killMap = killQuery.setMapKey("timestamp").findMap();
					long earliest = System.currentTimeMillis();
					for(Long time : killMap.keySet()){
						if(time < earliest){
							earliest = time;
						}
					}
					DeathStat earlyStat = killMap.get(earliest);
					this.plugin.pvpRatingTable.updatePlayerRating(earlyStat.getKillerName(),
							(this.plugin.pvpRatingTable.getPlayerRating(earlyStat.getKillerName())-earlyStat.getRatingChange());
					this.plugin.pvpRatingTable.updatePlayerRating(earlyStat.getVictimrName(),
							(this.plugin.pvpRatingTable.getPlayerRating(earlyStat.getVictimName())+earlyStat.getRatingChange());
					earlyStat.setUsedInRating(false);
					this.save(stat);
				}else{
					break;
				}
			}else{
				break;
			}
		}
		while(true){
			Query<DeathStat> killQuery = plugin.getDatabase().find(DeathStat.class).where().ieq("usedInRating", true).query();
			if(killQuery != null){
				Map<Long,DeathStat> timeMap = killQuery.setMapKey("timestamp").findMap();
				long limit = (System.currentTimeMillis() - 1209600000L);
				for(Long time : killMap.keySet()){
					if(time < limit){
						DeathStat timeStat = timeMap.get(time);
						this.plugin.pvpRatingTable.updatePlayerRating(timeStat.getKillerName(),
								(this.plugin.pvpRatingTable.getPlayerRating(timeStat.getKillerName())-timeStat.getRatingChange());
						this.plugin.pvpRatingTable.updatePlayerRating(timeStat.getVictimrName(),
								(this.plugin.pvpRatingTable.getPlayerRating(timeStat.getVictimName())+timeStat.getRatingChange());
						timeStat.setUsedInRating(false);
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
