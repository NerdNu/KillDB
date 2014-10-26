package com.c45y.KillDB;

import java.util.logging.Logger;

import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.logging.Level;

import javax.persistence.PersistenceException;

import com.c45y.KillDB.database.DeathStat;
import com.c45y.KillDB.database.DeathStatTable;
import com.c45y.KillDB.database.PvPRating;
import com.c45y.KillDB.database.PvPRatingTable;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class KillDB extends JavaPlugin
{
	public final HandleDeath handleDeath = new HandleDeath(this);
	Logger log = Logger.getLogger("Minecraft");
	DeathStatTable deathStatTable;
	public PvPRatingTable pvpRatingTable;
	public boolean logDeathItems = false;

	public void onEnable() {
		setupDatabase();
		deathStatTable = new DeathStatTable(this);
		pvpRatingTable = new PvPRatingTable(this);
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(handleDeath, this);
	}
	
	public boolean setupDatabase() {
        try {
            getDatabase().find(DeathStat.class).findRowCount();
            getDatabase().find(PvPRating.class).findRowCount();
        } catch (PersistenceException ex) {
            getLogger().log(Level.INFO, "First run, initializing database.");
            installDDL();
            return true;
        }
        
        return false;
    }
        
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args){
        Player player = (Player)sender;
	if(commandLabel.equalsIgnoreCase("rating")){
            sender.sendMessage("" + args[0] + " has a rating of " + this.pvpRatingTable.getPlayerRating(args[0]));
	}		
	return true;
    }
	
	@Override
    public ArrayList<Class<?>> getDatabaseClasses() {
        ArrayList<Class<?>> list = new ArrayList<Class<?>>();
        list.add(DeathStat.class);
        return list;
    }
}