package com.c45y.KillDB.database;

import com.avaje.ebean.Query;
import com.c45y.KillDB.KillDB;

public class PvPRatingTable {

	KillDB plugin;
	
	public PvPRatingTable(KillDB plugin) {
		this.plugin = plugin;
	}

	public int getPlayerRating(String player){
		int rating = 500;
		Query<PvPRating> query = plugin.getDatabase().find(PvPRating.class).where().ieq("playerName", player).query();
		
		if (query != null){
			rating = query.findUnique().getRating();
		}
		
		return rating;
	}
	
	public void updatePlayerRating(String player, int newRating){
		PvPRating pvprating;
		Query<PvPRating> query = plugin.getDatabase().find(PvPRating.class).where().ieq("playerName", player).query();
		
		if (query != null){
			pvprating = query.findUnique();
			pvprating.setRating(newRating);
		}else{
			pvprating = new PvPRating();
			pvprating.setPlayerName(player);
			pvprating.setRating(newRating);
		}
		this.save(pvprating);
	}
	
	public PvPRating getRequest(int id) {
		PvPRating retVal = null;
		
		Query<PvPRating> query = plugin.getDatabase().find(PvPRating.class).where().eq("id", id).query();
		
		if (query != null) {
			retVal = query.findUnique();
		}
		
		return retVal;
	}
	
	public void save(PvPRating pvprating) {
		plugin.getDatabase().save(pvprating);
	}
	
}
