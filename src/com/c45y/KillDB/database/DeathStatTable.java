package com.c45y.KillDB.database;

import com.avaje.ebean.Query;
import com.c45y.KillDB.KillDB;
import java.util.Map;
import org.bukkit.entity.Player;
import com.c45y.KillDB.GearScore;

public class DeathStatTable {

	KillDB plugin;
    private int decayDays = 14;
	
	public DeathStatTable(KillDB plugin) {
		this.plugin = plugin;
	}
	
	public void setDecayDays(int days){
        this.decayDays = days;
    }
    
    public DeathStat getRequest(int id) {
		DeathStat retVal = null;
		
		Query<DeathStat> query = plugin.getDatabase().find(DeathStat.class).where().eq("id", id).query();
		
		if (query != null) {
			retVal = query.findUnique();
		}
		
		return retVal;
	}
	
	public int cleanup(Player victim, Player killer){
		int totalDecay = 0;
        while(true){ // Kill Decay: Only keeps the 10 most valuable kills in the past 2 weeks
            Map<?,DeathStat> killMap = plugin.getDatabase().find(DeathStat.class).where().ieq("killerName",killer.getName()).ieq("playerName",victim.getName()).eq("usedInRating",1).findMap();
			if(killMap != null){
				if(killMap.size() > 10){
					DeathStat lowest = new DeathStat();
                        lowest.setRatingChange(GearScore.getHardCap());
					for(Object entry : killMap.keySet()){
						if(killMap.get(entry).getRatingChange() < lowest.getRatingChange()){
							lowest = killMap.get(entry);
						}
					}
					this.plugin.pvpRatingTable.updatePlayerRating(lowest.getKillerName(),
							(this.plugin.pvpRatingTable.getPlayerRating(lowest.getKillerName())-lowest.getRatingChange()));
					this.plugin.pvpRatingTable.updatePlayerRating(lowest.getPlayerName(),
							(this.plugin.pvpRatingTable.getPlayerRating(lowest.getPlayerName())+lowest.getRatingChange()));
					lowest.setUsedInRating(0);
                    totalDecay += lowest.getRatingChange();
					this.save(lowest);
				}else{
					break;
				}
			}else{
				break;
			}
		}
        timeDecay: //Removes anything older than 2 weeks
		while(true){
			Map<?,DeathStat> timeMap = plugin.getDatabase().find(DeathStat.class).where().eq("usedInRating", 1).findMap();
			if(timeMap != null){
				DeathStat limit = new DeathStat();
                                limit.setTimestamp(System.currentTimeMillis() - (86400000L*decayDays));
				for(Object entry : timeMap.keySet()){
					if(timeMap.get(entry).getTimestamp() < limit.getTimestamp()){
						this.plugin.pvpRatingTable.updatePlayerRating(timeMap.get(entry).getKillerName(),
								(this.plugin.pvpRatingTable.getPlayerRating(timeMap.get(entry).getKillerName())-timeMap.get(entry).getRatingChange()));
						this.plugin.pvpRatingTable.updatePlayerRating(timeMap.get(entry).getPlayerName(),
								(this.plugin.pvpRatingTable.getPlayerRating(timeMap.get(entry).getPlayerName())+timeMap.get(entry).getRatingChange()));
						timeMap.get(entry).setUsedInRating(0);
                        continue;
					}else{
                         break timeDecay;
                     }
				}
			}else{
				break;
			}
		}
        return totalDecay;
	}
	
	public void save(DeathStat deathstat) {
		plugin.getDatabase().save(deathstat);
	}
	
}
