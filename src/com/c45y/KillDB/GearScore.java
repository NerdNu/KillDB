package com.c45y.KillDB;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import java.util.Properties;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class GearScore {
    private static double baseChange;
    private static double gearMax;
    private static double gearMin;
    private static int hardCap;
    
    private final Player killer;
    private final int killerRating;
    private final Player victim;
    private final int victimRating;
	
    private double killerOffense = 0.0;
    private double killerFireOffense = 0.0;
    private double killerRangedOffense = 0.0;
    private double killerDefense = 0.0;
    private double killerFireDefense = 0.0;
    private double killerRangedDefense = 0.0;
    
    private double victimOffense = 0.0;
    private double victimFireOffense = 0.0;
    private double victimRangedOffense = 0.0;
    private double victimDefense = 0.0;
    private double victimFireDefense = 0.0;
    private double victimRangedDefense = 0.0;
    
    
    
    public GearScore(Player victim,int victimRating, Player killer, int killerRating){
    	this.victim = victim;
    	this.victimRating = victimRating;
    	this.killer = killer;
    	this.killerRating = killerRating;
    }
    
    public int[] getChange(){
    	double[] killerGS = this.getGS(this.killer);
    	this.killerOffense = killerGS[0];
    	if(killerOffense == 0){ // Accounts for the 1 damage you can do empty handed
    		killerOffense = 1;
    	}
    	this.killerFireOffense = killerGS[1];
    	this.killerRangedOffense = killerGS[2];
    	this.killerDefense = killerGS[3];
    	this.killerFireDefense = killerGS[4];
    	this.killerRangedDefense = killerGS[5];
    	double[] victimGS = this.getGS(this.victim);
    	this.victimOffense = victimGS[0];
    	if(this.victimOffense == 0){  // Accounts for the 1 damage you can do empty handed
    		this.victimOffense = 1;
    	}
    	this.victimFireOffense = victimGS[1];
    	this.victimRangedOffense = victimGS[2];
    	this.victimDefense = victimGS[3];
    	this.victimFireDefense = victimGS[4];
    	this.victimRangedDefense = victimGS[5];
    	
    	this.killerOffense += this.killerRangedOffense;
    	this.killerOffense *= (1 - this.victimDefense);
    	this.killerRangedOffense *= (1 - this.victimDefense);
    	this.killerOffense -= this.killerRangedOffense;
    	this.killerRangedOffense *= (1 - this.victimRangedDefense);
    	this.killerOffense += this.killerRangedOffense;
    	this.killerFireOffense *= (1 - this.victimFireDefense);
    	this.killerOffense += this.killerFireOffense;
    	    	
    	this.victimOffense += this.victimRangedOffense;
    	this.victimOffense *= (1 - this.killerDefense);
    	this.victimRangedOffense *= (1 - this.killerDefense);
    	this.victimOffense -= this.victimRangedOffense;
    	this.victimRangedOffense *= (1 - this.killerRangedDefense);
    	this.victimOffense += this.victimRangedOffense;
    	this.victimFireOffense *= (1 - this.killerFireDefense);
    	this.victimOffense += this.victimFireOffense;
    	
    	double change = baseChange;
    	
    	if (this.victimOffense > this.killerOffense){
            change += ((this.victimOffense - this.killerOffense)/25.25) * (gearMax - baseChange);
    	}
        if (this.killerOffense > this.victimOffense){
            change -= ((this.killerOffense - this.victimOffense)/25.25) * (baseChange - gearMin);
        }
        change *= ((double)this.victimRating/(double)this.killerRating);
        if (change > hardCap){ 
        	change = hardCap;
        }
        int changeOfRating = (int)change;
        int newKillerRating = this.killerRating + changeOfRating;
        int newVictimRating = this.victimRating - changeOfRating;
    	
    	int[] resultArray = {newVictimRating, newKillerRating, changeOfRating};
    	return resultArray;
    }
    
    public double[] getGS(Player player){
    	double[] results = {0.0,0.0,0.0,0.0,0.0,0.0};

    	double[][] totalGear = new double[5][];
    	totalGear[0] = itemScore(player.getInventory().getHelmet());
    	totalGear[1] = itemScore(player.getInventory().getChestplate());
    	totalGear[2] = itemScore(player.getInventory().getLeggings());
    	totalGear[3] = itemScore(player.getInventory().getBoots());
    	totalGear[4] = itemScore(player.getItemInHand());
    	
    	for(int i=0;i<6;i++){
    		for(int j=0;j<5;j++){
    			results[i] += totalGear[j][i];
    		}
    	}
    	return results;
    }
    
    public double[] itemScore(ItemStack item){
    	if(item == null){
            double[] nullArray = {0.0,0.0,0.0,0.0,0.0,0.0};
            return nullArray;
        }else{
            String name = item.getType().toString().toLowerCase();
            double offense = 0.0;
            double fireOffense = 0.0;
            double rangedOffense = 0.0;
            double defense = 0.0;
            double fireDefense = 0.0;
            double rangedDefense = 0.0;
            if(name.equals("air")){
            }
            else if(name.contains("helmet")){
                    if(name.contains("leather")){
                            defense += .04;
                    }
                    else if(name.contains("gold")){
                            defense += .08;
                    }
    		else if(name.contains("chain")){
    			defense += .08;
    		}
    		else if(name.contains("iron")){
    			defense += .08;
    		}
    		else if(name.contains("diamond")){
    			defense += .12;
    		}
    	}
    	else if(name.contains("chestplate")){
    		if(name.contains("leather")){
    			defense += .12;
    		}
    		else if(name.contains("gold")){
    			defense += .20;
    		}
    		else if(name.contains("chain")){
    			defense += .20;
    		}
    		else if(name.contains("iron")){
    			defense += .24;
    		}
    		else if(name.contains("diamond")){
    			defense += .32;
    		}
    	}
    	else if(name.contains("leggings")){
    		if(name.contains("leather")){
    			defense += .08;
    		}
    		else if(name.contains("gold")){
    			defense += .12;
    		}
    		else if(name.contains("chain")){
    			defense += .16;
    		}
    		else if(name.contains("iron")){
    			defense += .20;
    		}
    		else if(name.contains("diamond")){
    			defense += .24;
    		}
    	}
    	else if(name.contains("boots")){
    		if(name.contains("leather")){
    			defense += .04;
    		}
    		else if(name.contains("gold")){
    			defense += .04;
    		}
    		else if(name.contains("chain")){
    			defense += .04;
    		}
    		else if(name.contains("iron")){
    			defense += .08;
    		}
    		else if(name.contains("diamond")){
    			defense += .12;
    		}
    	}
    	else if(name.contains("sword")){
    		if(name.contains("wood")){
    			offense += 5.0;
    		}
    		else if(name.contains("gold")){
    			offense += 5.0;
    		}
    		else if(name.contains("stone")){
    			offense += 6.0;
    		}
    		else if(name.contains("iron")){
    			offense += 7.0;
    		}
    		else if(name.contains("diamond")){
    			offense += 8.0;
    		}
    	}
    	else if(name.contains("pickaxe")){
    		if(name.contains("wood")){
    			offense += 3.0;
    		}
    		else if(name.contains("gold")){
    			offense += 3.0;
    		}
    		else if(name.contains("stone")){
    			offense += 4.0;
    		}
    		else if(name.contains("iron")){
    			offense += 5.0;
    		}
    		else if(name.contains("diamond")){
    			offense += 6.0;
    		}
    	}
    	else if(name.contains("axe")){
    		if(name.contains("wood")){
    			offense += 4.0;
    		}
    		else if(name.contains("gold")){
    			offense += 4.0;
    		}
    		else if(name.contains("stone")){
    			offense += 5.0;
    		}
    		else if(name.contains("iron")){
    			offense += 6.0;
    		}
    		else if(name.contains("diamond")){
    			offense += 7.0;
    		}
    	}
    	else if(name.contains("spade")){
    		if(name.contains("wood")){
    			offense += 2.0;
    		}
    		else if(name.contains("gold")){
    			offense += 2.0;
    		}
    		else if(name.contains("stone")){
    			offense += 3.0;
    		}
    		else if(name.contains("iron")){
    			offense += 4.0;
    		}
    		else if(name.contains("diamond")){
    			offense += 5.0;
    		}
    	}
    	else if(name.equals("bow")){
    		rangedOffense += 8;
    	}
    	else if(name.contains("hoe")){
    		offense += 1;
    	}
    	double[] enchants = enchantScore(name,item.getEnchantments());
    	offense += enchants[0];
    	fireOffense += enchants[1];
    	rangedOffense += enchants[2];
    	defense += (1-defense) * enchants[3];
    	fireDefense += enchants[4];
    	rangedDefense += enchants[5];
        double[] results = {offense, fireOffense, rangedOffense, defense, fireDefense, rangedDefense};
    	return results;
        }
    }
    
    public double[] enchantScore(String item,Map<Enchantment,Integer> enchants){
    	double offense = 0.0;
    	double fireOffense = 0.0;
    	double rangedOffense = 0.0;
    	double defense = 0.0;
    	double fireDefense = 0.0;
    	double rangedDefense = 0.0;
    	for(Enchantment e : enchants.keySet()){
    		String name = e.getName();
    		if(item.contains("sword")||item.contains("axe")||item.contains("spade")){
    			if(name.equals("DAMAGE_ALL")){ // Sharpness
    				offense += (enchants.get(e) * 1.25);
    			}
    			else if(name.equals("FIRE_ASPECT")){
    				if(enchants.get(e) == 1){
    					fireOffense += 3.0;
    				}
    				else{
    					fireOffense += 7.0;
    				}
    			}
    		}
    		else if(item.contains("bow")){
    			if(name.equals("ARROW_DAMAGE")){ // Power
    				if(enchants.get(e) == 1){
    					rangedOffense += 4.0;
    				}
    				else if(enchants.get(e) == 2){
    					rangedOffense += 7.0;
    				}
    				else if(enchants.get(e) == 3){
    					rangedOffense += 8.0;
    				}
    				else if(enchants.get(e) == 4){
    					rangedOffense += 11.0;
    				}
    				else if(enchants.get(e) == 5){
    					rangedOffense += 12.0;
    				}
    			}
    			else if(name.equals("ARROW_FIRE")){ // Flame
    				fireOffense += 3.0;
    			}
    		}
    		else if(item.contains("helmet")||item.contains("chestplate")||item.contains("leggings")||item.contains("boots")){
    			if(name.equals("PROTECTION_ENVIRONMENTAL")){
    				if(enchants.get(e) < 4){
    					defense += (enchants.get(e) * .03);
    				}
    				else{
    					defense += .15;
    				}
    			}
    			else if(name.equals("PROTECTION_FIRE")){
    				if(enchants.get(e) < 4){
    					fireDefense += (enchants.get(e) * .06);
    				}
    				else{
    					fireDefense += .27;
    				}
    			}
    			else if(name.equals("PROTECTION_PROJECTILE")){
    				if(enchants.get(e) < 4){
    					rangedDefense += (.03 * ((2*enchants.get(e))+1));
    				}
    				else{
    					rangedDefense += .33;
    				}
    			}
    			else if(name.equals("THORNS")){
    				if(enchants.get(e) == 1){
    					offense += 1.0;
    				}
    				else if(enchants.get(e) == 2){
    					offense += 2.5;
    				}
    				else if(enchants.get(e) == 3){
    					offense += 4.0;
    				}
    			}
    		}
    	}
        double[] results = {offense,fireOffense,rangedOffense,defense,fireDefense,rangedDefense};
    	return results;
    }
    
    public static void setUp(){
        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream("plugins/StrikeDeath/config.properties"));
            baseChange = Double.parseDouble(properties.getProperty("baseChange"));
            gearMax = Double.parseDouble(properties.getProperty("gearMax"));
            gearMin = Double.parseDouble(properties.getProperty("gearMin"));
            hardCap = Integer.parseInt(properties.getProperty("hardCap"));
        } catch (IOException e) {
            try {
                Properties props = new Properties();
                props.setProperty("baseChange", "20.0");
                baseChange = 20.0;
                props.setProperty("gearMax", "50.0");
                gearMax = 50.0;
                props.setProperty("gearMin", "5.0");
                gearMin = 5.0;
                props.setProperty("hardCap", "200");
                hardCap = 200;
                File f = new File("plugins/StrikeDeath/config.properties");
                OutputStream out = new FileOutputStream( f );
                props.store(out, "Initial file creation");
            }
            catch (IOException ex ) {
                ex.printStackTrace();
            }
        }
    }
}
