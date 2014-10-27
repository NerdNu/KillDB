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
                        Map<?,DeathStat> killMap = plugin.getDatabase().find(DeathStat.class).where().ieq("killerName",killer.getName().toLowerCase()).ieq("playerName",victim.getName().toLowerCase()).eq("usedInRating",1).findMap();
			if(killMap != null){
				if(killMap.size() > 10){
					DeathStat earliest = new DeathStat();
                                        earliest.setTimestamp(System.currentTimeMillis());
					for(Object entry : killMap.keySet()){
						if(killMap.get(entry).getTimestamp() < earliest.getTimestamp()){
							earliest = killMap.get(entry);
						}
					}
					this.plugin.pvpRatingTable.updatePlayerRating(earliest.getKillerName().toLowerCase(),
							(this.plugin.pvpRatingTable.getPlayerRating(earliest.getKillerName().toLowerCase())-earliest.getRatingChange()));
					this.plugin.pvpRatingTable.updatePlayerRating(earliest.getPlayerName().toLowerCase(),
							(this.plugin.pvpRatingTable.getPlayerRating(earliest.getPlayerName().toLowerCase())+earliest.getRatingChange()));
					earliest.setUsedInRating(0);
					this.save(earliest);
				}else{
					break;
				}
			}else{
				break;
			}
		}
                timeDecay:
		while(true){
                    System.out.println("while 2");
			Map<?,DeathStat> timeMap = plugin.getDatabase().find(DeathStat.class).where().eq("usedInRating", 1).findMap();
			if(timeMap != null){
                            System.out.println("for 2-1");
				DeathStat limit = new DeathStat();
                                limit.setTimestamp(System.currentTimeMillis() - 1209600000L);
				for(Object entry : timeMap.keySet()){
                                    System.out.println("for 2");
					if(timeMap.get(entry).getTimestamp() < limit.getTimestamp()){
                                            System.out.println("if 2-2");
						this.plugin.pvpRatingTable.updatePlayerRating(timeMap.get(entry).getKillerName().toLowerCase(),
								(this.plugin.pvpRatingTable.getPlayerRating(timeMap.get(entry).getKillerName().toLowerCase())-timeMap.get(entry).getRatingChange()));
						this.plugin.pvpRatingTable.updatePlayerRating(timeMap.get(entry).getPlayerName().toLowerCase(),
								(this.plugin.pvpRatingTable.getPlayerRating(timeMap.get(entry).getPlayerName().toLowerCase())+timeMap.get(entry).getRatingChange()));
						timeMap.get(entry).setUsedInRating(0);
                                                continue;
					}else{
                                            break timeDecay;
                                        }
				}
			}else{
                            System.out.println("while 2 else");
				break;
			}
		}
	}
	
	public void save(DeathStat deathstat) {
		plugin.getDatabase().save(deathstat);
	}
	
}
