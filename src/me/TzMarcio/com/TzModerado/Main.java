package me.TzMarcio.com.TzModerado;

import java.io.File;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin
  implements Listener
{
  private FileConfiguration mods = null;
  private File modsFile = null;

  public void onEnable(){
    saveDefaultConfig();
    Bukkit.getPluginManager().registerEvents(this, this);
    this.modsFile = new File(getDataFolder(), "mods.yml");
    this.mods = YamlConfiguration.loadConfiguration(this.modsFile);
  }

  public void onDisable(){}

  @SuppressWarnings("deprecation")
  public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
    if (cmd.getName().equalsIgnoreCase("moderate")) {
    	if (!(sender instanceof Player)) {
    		sender.sendMessage(getConfig().getString("MSGCommand_ExecutedInGame").replace("&", "§"));
    		return true;
    	}
    	Player p = (Player)sender;
    	String ss = p.getPlayer().getName();
    	if (p.hasPermission(getConfig().getString("Permission"))) {
    		if (this.mods.getBoolean("TzModerate.List." + ss)) {
    			p.sendMessage(getConfig().getString("MSGLeave_Mode").replace("&", "§"));
    			this.mods.set("TzModerate.List." + ss, Boolean.valueOf(false));
    			p.setGameMode(GameMode.getByValue(getConfig().getInt("Gamemode")));
    			p.getInventory().clear();
    			p.getInventory().setHelmet(null);
    			p.getInventory().setChestplate(null);
    			p.getInventory().setLeggings(null);
    			p.getInventory().setBoots(null);
    			try {
    				this.mods.save(this.modsFile);
    			} catch (IOException e) {
    				e.printStackTrace();
    			}
    		} else {
    			boolean isEmpty = false;
    			for (int is = 0; is < 36; is++) {
    				if (p.getInventory().getItem(is) != null){
    					isEmpty = true;
    					break;
    				}
    			}
    			if (isEmpty) {
    				p.sendMessage(getConfig().getString("MSGEmpty_inv").replace("&", "§"));
    			}else if ((p.getInventory().getHelmet() == null) && (p.getInventory().getChestplate() == null) && (p.getInventory().getLeggings() == null) && (p.getInventory().getBoots() == null)) {
    				p.sendMessage(getConfig().getString("MSGJoin_Mode").replace("&", "§"));
    				this.mods.set("TzModerate.List." + ss, Boolean.valueOf(true));
    				p.setGameMode(GameMode.getByValue(getConfig().getInt("Gamemode2")));
    				try {
    					this.mods.save(this.modsFile);
    				} catch (IOException e) {
    					e.printStackTrace();
    				}
    			} else {
    				p.sendMessage(getConfig().getString("MSGEmpty_inv").replace("&", "§"));
    			}
    		}
    	}else {
    		p.sendMessage(getConfig().getString("Message_WithoutPermission").replace("&", "§"));
    	}
    }
    return false;
  	}

  @EventHandler(priority=EventPriority.HIGH)
  private void PlayerPickupItemEvent(PlayerPickupItemEvent event){
	  Entity ps = event.getPlayer();
	  if (!(ps instanceof Player)) {
		  return;
	  }
	  Player p = event.getPlayer();
	  String ss = p.getPlayer().getName();
	  if(getConfig().getBoolean("PickUPItens")){
		  if (this.mods.getBoolean("TzModerate.List." + ss)){
			  event.setCancelled(true);
		  }else{
			  return;
		  }
	  }
  }

  @EventHandler(priority=EventPriority.HIGH)
  private void onDrop(PlayerDropItemEvent event){
    Entity ps = event.getPlayer();
    if (!(ps instanceof Player)) {
      return;
    }
    Player p = event.getPlayer();
    String ss = p.getPlayer().getName();
    if(getConfig().getBoolean("Drops")){
	    if (this.mods.getBoolean("TzModerate.List." + ss))
	      event.setCancelled(true);
	    else
	      return;
    }
  }

  @EventHandler(priority=EventPriority.HIGH)
  private void onQuit(PlayerQuitEvent event)
  {
    Entity ps = event.getPlayer();
    if (!(ps instanceof Player)) {
      return;
    }
    Player p = event.getPlayer();
    String ss = p.getPlayer().getName();
    if (this.mods.getBoolean("TzModerate.List." + ss)) {
      this.mods.set("TzModerate.List." + ss, Boolean.valueOf(false));
      p.getInventory().clear();
      p.getInventory().setHelmet(null);
      p.getInventory().setChestplate(null);
      p.getInventory().setLeggings(null);
      p.getInventory().setBoots(null);
      p.setGameMode(GameMode.SURVIVAL);
      try {
        this.mods.save(this.modsFile);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  @EventHandler(priority=EventPriority.HIGH)
  private void onPlace(BlockPlaceEvent event) {
    Entity ps = event.getPlayer();
    if (!(ps instanceof Player)) {
      return;
    }
    Player p = event.getPlayer();
    String ss = p.getPlayer().getName();
    if(getConfig().getBoolean("Blocks")){
	    if (this.mods.getBoolean("TzModerate.List." + ss))
	      event.setCancelled(true);
	    else
	      event.setCancelled(false);
    }
  }

  @SuppressWarnings("deprecation")
@EventHandler(priority=EventPriority.HIGH)
  private void click(PlayerInteractEvent e)
  {
    Entity ps = e.getPlayer();
    if (!(ps instanceof Player)) {
      return;
    }
    Player p = e.getPlayer();
    String ss = p.getPlayer().getName();
    int item = p.getItemInHand().getTypeId();
    int is = p.getItemInHand().getTypeId();
    Action a = e.getAction();
    if ((a == Action.RIGHT_CLICK_BLOCK) && (item == is))
    	if(getConfig().getBoolean("Blocks")){
	      if (this.mods.getBoolean("TzModerate.List." + ss))
	        e.setCancelled(true);
	      else
	        return;
    	}
  }

  @SuppressWarnings("deprecation")
@EventHandler(priority=EventPriority.HIGH)
  private void onClick(PlayerInteractEvent e)
  {
    Entity ps = e.getPlayer();
    if (!(ps instanceof Player)) {
      return;
    }
    Player p = e.getPlayer();
    String ss = p.getPlayer().getName();
    int item = p.getItemInHand().getTypeId();
    int is = p.getItemInHand().getTypeId();
    Action a = e.getAction();
    if ((a == Action.RIGHT_CLICK_BLOCK) && (item == is) && (e.getClickedBlock() == e.getClickedBlock())) {
    	if(getConfig().getBoolean("Blocks")){
	      if (this.mods.getBoolean("TzModerate.List." + ss))
	        e.setCancelled(true);
	      else {
	    	  return;
	      }
    	}
    }
    else if (e.getClickedBlock() == e.getClickedBlock())
    	if(getConfig().getBoolean("Blocks")){
	      if (this.mods.getBoolean("TzModerate.List." + ss))
	        e.setCancelled(true);
	      else
	        return;
    	}
  }

@EventHandler(priority=EventPriority.HIGH)
  private void onEntityDamage(EntityDamageByEntityEvent e)
  {
    Entity entity = e.getDamager();
    if (!(entity instanceof Player)) {
      return;
    }
    if ((e.getDamager() instanceof Projectile)) {
      Projectile damager = (Projectile)e.getDamager();
      LivingEntity ss = (LivingEntity) damager.getShooter();
      String msp = ((OfflinePlayer)ss).getPlayer().getName();
      if(getConfig().getBoolean("Damage")){
	      if (this.mods.getBoolean("TzModerate.List." + msp))
	        e.setCancelled(true);
	      else
	        return;
      }
    }
    else
    {
      String msp = ((OfflinePlayer)entity).getPlayer().getName();
      if(getConfig().getBoolean("Damage")){
	      if (this.mods.getBoolean("TzModerate.List." + msp))
	        e.setCancelled(true);
	      else
	    	  return;
      }
    }
  }

  @EventHandler
  public void onCmd(PlayerCommandPreprocessEvent e)
  {
    for (String s : getConfig().getStringList("BlockCommands"))
      if (e.getMessage().startsWith(s)) {
        e.setCancelled(true);
        e.getPlayer().sendMessage(getConfig().getString("MSGCmd_Block").replace("&", "§").replace("<cmd>", e.getMessage()));
        return;
      }
  }
}