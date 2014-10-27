package com.c45y.KillDB.database;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import com.avaje.ebean.validation.NotNull;

@Entity()
@Table(name = "pvp_ratings")
public class PvPRating {
	
	@Id
	private int id;
	
	@NotNull
	private String playerName;
	private int rating;
	
	public void setId(int id) {
		this.id = id;
	}
	
	public int getId() {
		return this.id;
	}
	
	public void setPlayerName(String playerName) {
		this.playerName = playerName.toLowerCase();
	}
	
	public String getPlayerName() {
		return this.playerName;
	}
	
	public void setRating(int rating){
		this.rating = rating;
	}
	
	public int getRating(){
		return rating;
	}
}
