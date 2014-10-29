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
import java.util.List;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import net.sacredlabyrinth.phaed.simpleclans.*;
import org.bukkit.ChatColor;

public class KillDB extends JavaPlugin
{
	public final HandleDeath handleDeath = new HandleDeath(this);
	static final Logger log = Logger.getLogger("Minecraft");
	DeathStatTable deathStatTable;
	public PvPRatingTable pvpRatingTable;
	public boolean logDeathItems = false;

	public void onEnable() {
		setupDatabase();
		deathStatTable = new DeathStatTable(this);
		pvpRatingTable = new PvPRatingTable(this);
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(handleDeath, this);
                GearScore.setUp();
                pvpRatingTable.loadTop5();
	}
	
	public void setupDatabase() {
            try {
                this.getDatabase().find(PvPRating.class).findRowCount();
                this.getDatabase().find(DeathStat.class).findRowCount();
            } catch (PersistenceException ex) {
                getLogger().log(Level.INFO, "First run, initializing database.");
                installDDL();
            }
        }
        
        public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args){
            if(commandLabel.equalsIgnoreCase("rating")){
                if(args.length == 0){
                    try{
                        int rating = this.pvpRatingTable.getPlayerRating(sender.getName().toLowerCase());
                        ChatColor color;
                        if(rating < 350){
                            color = ChatColor.RED;
                        }else if(rating > 650){
                            color = ChatColor.GREEN;
                        }else{
                            color = ChatColor.YELLOW;
                        }
                        sender.sendMessage(color + "You have a rating of " + rating);
                    }catch(Exception e){
                        sender.sendMessage(ChatColor.YELLOW + "You have a rating of 500");
                    }
                }else if(SimpleClans.getInstance().getClanManager().isClan(args[0])){
                    List<ClanPlayer> members = SimpleClans.getInstance().getClanManager().getClan(args[0]).getAllMembers();
                    List<String> names = new ArrayList();
                    for(ClanPlayer member : members){
                        names.add(member.getName());
                    }
                    int totalRating = 0;
                    int totalNames = 0;
                    for(String name : names){
                        try{
                           totalRating += this.pvpRatingTable.getPlayerRating(name.toLowerCase());
                           totalNames++;
                        }catch(Exception e){
                           totalRating += 500;
                           totalNames++;
                        }
                    }
                    ChatColor color;
                    if(totalRating/totalNames < 350){
                        color = ChatColor.RED;
                    }else if(totalRating/totalNames > 650){
                        color = ChatColor.GREEN;
                    }else{
                        color = ChatColor.YELLOW;
                    }
                    sender.sendMessage(color + SimpleClans.getInstance().getClanManager().getClan(args[0]).getName()
                            + "has an average rating of " + (totalRating/totalNames));
                }else{
                    try{
                        int rating = this.pvpRatingTable.getPlayerRating(args[0].toLowerCase());
                        ChatColor color;
                        if(rating < 350){
                            color = ChatColor.RED;
                        }else if(rating > 650){
                            color = ChatColor.GREEN;
                        }else{
                            color = ChatColor.YELLOW;
                        }
                        sender.sendMessage(color + args[0] + " has a rating of " + rating);
                    }catch(Exception e){
                        sender.sendMessage(ChatColor.YELLOW + args[0] + " has a rating of 500");
                    }
                }
            }else if(commandLabel.equalsIgnoreCase("top5")){
                PvPRating[] top5 = this.pvpRatingTable.top5();
                sender.sendMessage(ChatColor.GREEN + "       The Current Top 5!       ");
                sender.sendMessage(ChatColor.GREEN + "--------------------------------");
                sender.sendMessage(ChatColor.GREEN + "1) " + top5[0].getPlayerName() + " has a rating of " + top5[0].getRating());
                sender.sendMessage(ChatColor.GREEN + "2) " + top5[1].getPlayerName() + " has a rating of " + top5[1].getRating());
                sender.sendMessage(ChatColor.GREEN + "3) " + top5[2].getPlayerName() + " has a rating of " + top5[2].getRating());
                sender.sendMessage(ChatColor.GREEN + "4) " + top5[3].getPlayerName() + " has a rating of " + top5[3].getRating());
                sender.sendMessage(ChatColor.GREEN + "5) " + top5[4].getPlayerName() + " has a rating of " + top5[4].getRating());
            }
            return true;
        }
	
	@Override
    public ArrayList<Class<?>> getDatabaseClasses() {
        ArrayList<Class<?>> list = new ArrayList<Class<?>>();
        list.add(DeathStat.class);
        list.add(PvPRating.class);
        return list;
    }
}