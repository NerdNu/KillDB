package com.c45y.KillDB.database;

import com.avaje.ebean.Query;
import com.c45y.KillDB.KillDB;
import java.util.Map;

public class PvPRatingTable {

	KillDB plugin;
        private String first = "luke_gardner";
        private int firstRating = 0;
        private String second = "Ludeman84";
        private int secondRating = 0;
        private String third = "alansmithee331";
        private int thirdRating = 0;
        private String fourth = "Barlimore";
        private int fourthRating = 0;
        private String fifth = "Skuld";
        private int fifthRating = 0;
	
	public PvPRatingTable(KillDB plugin) {
		this.plugin = plugin;
	}

	public int getPlayerRating(String player){
		int rating = 500;
		Query<PvPRating> query = plugin.getDatabase().find(PvPRating.class).where().ieq("playerName", player.toLowerCase()).query();
		
		if (query != null){
			try{
                            PvPRating result = query.findUnique();
                            if (result.getPlayerName().equalsIgnoreCase(player.toLowerCase())){
                                return result.getRating();
                            }else{
                                return 500;
                            }
                        }catch(Exception ex){
                            return 500;
                        }
		}
		
		return rating;
	}
	
	public void updatePlayerRating(String player, int newRating){
		PvPRating pvprating;
		try{
                    Query<PvPRating> query = plugin.getDatabase().find(PvPRating.class).where().ieq("playerName", player.toLowerCase()).query();
		
                    if (query != null){
                            pvprating = query.findUnique();
                            pvprating.setRating(newRating);
                            plugin.getDatabase().save(pvprating);
                    }
		}catch(Exception ex){
			pvprating = new PvPRating();
			pvprating.setPlayerName(player.toLowerCase());
			pvprating.setRating(newRating);
                        plugin.getDatabase().save(pvprating);
		}
                updateTop5(player,newRating);
	}
        
        public void loadTop5(){
            Map<?,PvPRating> map = plugin.getDatabase().find(PvPRating.class).findMap();
            for(Object entry : map.keySet()){
                updateTop5(map.get(entry).getPlayerName(),map.get(entry).getRating());
            }
        }
        
        public void updateTop5(String player, int rating){
            if(player.equalsIgnoreCase(first)||player.equalsIgnoreCase(second)||player.equalsIgnoreCase(third)||player.equalsIgnoreCase(fourth)||player.equalsIgnoreCase(fifth)){
                if(player.equalsIgnoreCase(first)){
                    firstRating = rating;
                }else if(player.equalsIgnoreCase(second)){
                    secondRating = rating;
                }else if(player.equalsIgnoreCase(third)){
                    thirdRating = rating;
                }else if(player.equalsIgnoreCase(fourth)){
                    fourthRating = rating;
                }else if(player.equalsIgnoreCase(fifth)){
                    fifthRating = rating;
                }
            }else if(rating > firstRating){
                fifth = fourth;
                fifthRating = fourthRating;
                fourth = third;
                fourthRating = thirdRating;
                third = second;
                thirdRating = secondRating;
                second = first;
                secondRating = firstRating;
                first = player;
                firstRating = rating;
            }else if(rating > secondRating && !player.equals(first)){
                fifth = fourth;
                fifthRating = fourthRating;
                fourth = third;
                fourthRating = thirdRating;
                third = second;
                thirdRating = secondRating;
                second = player;
                secondRating = rating;
            }else if(rating > thirdRating && !player.equals(first) && !player.equals(second)){
                fifth = fourth;
                fifthRating = fourthRating;
                fourth = third;
                fourthRating = thirdRating;
                third = player;
                thirdRating = rating;
            }else if(rating > fourthRating && !player.equals(first) && !player.equals(second) && !player.equals(third)){
                fifth = fourth;
                fifthRating = fourthRating;
                fourth = player;
                fourthRating = rating;
            }else if(rating > fifthRating && !player.equals(first) && !player.equals(second) && !player.equals(third) && !player.equals(fourth)){
                fifth = player;
                fifthRating = rating;
            }
        }
        
        public void cleanupTop5(){
            String tempName = "";
            int tempRating = 0;
            if(second.equalsIgnoreCase(first)){
                second = "";
                secondRating = 0;
            }else if(third.equalsIgnoreCase(first)||third.equalsIgnoreCase(second)){
                third = "";
                thirdRating = 0;
            }else if(fourth.equalsIgnoreCase(first)||fourth.equalsIgnoreCase(second)||fourth.equalsIgnoreCase(third)){
                fourth = "";
                fourthRating = 0;
            }else if(fifth.equalsIgnoreCase(first)||fifth.equalsIgnoreCase(second)||fifth.equalsIgnoreCase(third)||fifth.equalsIgnoreCase(fourth)){
                fifth = "";
                fifthRating = 0;
            }
            if(fifthRating > fourthRating){
                tempName = fourth;
                tempRating = fourthRating;
                fourth = fifth;
                fourthRating = fifthRating;
                fifth = tempName;
                fifthRating = tempRating;
            }
            if(fourthRating > thirdRating){
                tempName = third;
                tempRating = thirdRating;
                third = fourth;
                thirdRating = fourthRating;
                fourth = tempName;
                fourthRating = tempRating;
            }
            if(thirdRating > secondRating){
                tempName = second;
                tempRating = secondRating;
                second = third;
                secondRating = thirdRating;
                third = tempName;
                thirdRating = tempRating;
            }
            if(secondRating > firstRating){
                tempName = first;
                tempRating = firstRating;
                first = second;
                firstRating = secondRating;
                second = tempName;
                secondRating = tempRating;
            }
        }
	
	public PvPRating getRequest(int id) {
		PvPRating retVal = null;
		
		Query<PvPRating> query = plugin.getDatabase().find(PvPRating.class).where().eq("id", id).query();
		
		if (query != null) {
			retVal = query.findUnique();
		}
		
		return retVal;
	}
        
        public PvPRating[] top5(){
            PvPRating firstPlace = new PvPRating();
            firstPlace.setPlayerName(first);
            firstPlace.setRating(firstRating);
            PvPRating secondPlace = new PvPRating();
            secondPlace.setPlayerName(second);
            secondPlace.setRating(secondRating);
            PvPRating thirdPlace = new PvPRating();
            thirdPlace.setPlayerName(third);
            thirdPlace.setRating(thirdRating);
            PvPRating fourthPlace = new PvPRating();
            fourthPlace.setPlayerName(fourth);
            fourthPlace.setRating(fourthRating);
            PvPRating fifthPlace = new PvPRating();
            fifthPlace.setPlayerName(fifth);
            fifthPlace.setRating(fifthRating);
            PvPRating[] results = {firstPlace,secondPlace,thirdPlace,fourthPlace,fifthPlace};
            return results;
        }
	
	public void save(PvPRating pvprating) {
		plugin.getDatabase().save(pvprating);
	}
	
}
