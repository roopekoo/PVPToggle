package me.roopekoo.pvptoggle;

import me.roopekoo.pvptoggle.commands.*;
import me.roopekoo.pvptoggle.events.*;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ForkJoinPool;
import java.util.logging.Level;

public final class PVPToggle extends JavaPlugin {

	private static final String BASEDIR = "plugins/PVPToggle";
	private static final String PATH = "/messages.yml";
	private static final File MSG_FILE = new File(BASEDIR+PATH);
	private static PVPToggle plugin = null;
	private final YamlConfiguration MSG;
	private File playerDataFile;
	private YamlConfiguration playerData;
	private onPVP onPvp;
	private onPlayerDamage onPlayerDmg;
	private PvpPlayer pvpPlayer;
	private onCombat onCombat;

	public PVPToggle() {
		File f = new File(BASEDIR);
		if(!f.exists()) {
			f.mkdir();
		}
		if(!f.exists()) {
			try {
				MSG_FILE.createNewFile();
			} catch(IOException e) {
				e.printStackTrace();
			}
		}
		MSG = YamlConfiguration.loadConfiguration(MSG_FILE);
	}

	public static PVPToggle getPlugin() {
		return plugin;
	}

	public static FileConfiguration loadConfig() {
		return plugin.getConfig();
	}

	@Override public void onEnable() {
		plugin = this;
		plugin.saveDefaultConfig();
		pvpPlayer = new PvpPlayer();
		onCombat = new onCombat();
		Objects.requireNonNull(plugin.getCommand("pvp")).setExecutor(new pvp());
		Objects.requireNonNull(plugin.getCommand("pvpcheck")).setExecutor(new pvpCheck());
		Objects.requireNonNull(plugin.getCommand("pvpkillprotection")).setExecutor(new pvpKillProtection());
		Objects.requireNonNull(plugin.getCommand("pvpreload")).setExecutor(new pvpReload());
		Objects.requireNonNull(plugin.getCommand("pvp")).setTabCompleter(new tabCompleter());
		Objects.requireNonNull(plugin.getCommand("pvpcheck")).setTabCompleter(new tabCompleter());
		Objects.requireNonNull(plugin.getCommand("pvpkillprotection")).setTabCompleter(new tabCompleter());
		PluginManager manager = getServer().getPluginManager();
		onPlayerDmg = new onPlayerDamage();
		manager.registerEvents(onPlayerDmg, plugin);
		manager.registerEvents(new onPlayerLogEvent(), plugin);
		manager.registerEvents(new onDeath(), plugin);
		onPvp = new onPVP();
		manager.registerEvents(onPvp, plugin);
		manager.registerEvents(new onDangerousPlacement(), plugin);
		loadMessages();
		loadPlayerDataFile();
		initializePlayerData();
	}

	public PvpPlayer getPvpPlayerInstance() {return pvpPlayer;}

	public onPVP getonPVPInstance() {
		return onPvp;
	}

	public onPlayerDamage getonPlayerDmgInstance() {return onPlayerDmg;}

	public onCombat getonCombatInstance() {
		return onCombat;
	}

	public void loadMessages() {
		for(Messages item: Messages.values()) {
			if(MSG.getString(item.getPath()) == null) {
				MSG.set(item.getPath(), item.getDefault());
			}
		}
		Messages.setFile(MSG);
		try {
			MSG.save(getMsgFile());
		} catch(IOException e) {
			Bukkit.getLogger().log(Level.WARNING, Messages.TITLE+Messages.SAVE_FAIL1.toString().replace("{filename}",
			                                                                                            "messages"+
			                                                                                            ".yml"));
			Bukkit.getLogger().log(Level.WARNING, Messages.TITLE+Messages.SAVE_FAIL2.toString());
			e.printStackTrace();
		}
	}

	private File getMsgFile() {
		return MSG_FILE;
	}

	public void loadPlayerDataFile() {
		playerDataFile = new File(getDataFolder(), "playerData.yml");
		if(!playerDataFile.exists()) {
			try {
				getDataFolder().mkdir();
				playerDataFile.createNewFile();
			} catch(IOException e) {
				// Send notice
				e.printStackTrace();
				// Without it loaded, we can't send them messages
				this.setEnabled(false);
			}
		}

		playerData = new YamlConfiguration();
		try {
			playerData.load(playerDataFile);
		} catch(IOException|InvalidConfigurationException e) {
			e.printStackTrace();
		}
	}

	public void writePlayerData(UUID uuid, boolean reload) {
		boolean canPVP;
		boolean killProtAfterDeath;
		if(noPlayerInYML(uuid)) {
			canPVP = loadConfig().getBoolean("DefaultPvpValue");
			killProtAfterDeath = loadConfig().getBoolean("DefaultKillProtAfterDeath");
			playerData.set("players."+uuid+".canPVP", canPVP);
			playerData.set("players."+uuid+".killProtAfterDeath", killProtAfterDeath);
		} else {
			canPVP = playerData.getBoolean("players."+uuid+".canPVP");
			killProtAfterDeath = playerData.getBoolean("players."+uuid+".killProtAfterDeath");
		}
		if(!pvpPlayer.hasPlayer(uuid)) {
			pvpPlayer.addNewPlayer(uuid, canPVP, killProtAfterDeath);
		}
		if(reload) {
			pvpPlayer.setCanPVP(uuid, canPVP);
			pvpPlayer.setKillProtAfterDeath(uuid, killProtAfterDeath);
		}
	}

	public void writeFile() {
		ForkJoinPool.commonPool().submit(()->{
			try {
				playerData.save(playerDataFile);
			} catch(IOException e) {
				e.printStackTrace();
			}
		});
	}

	public void reloadPlayerData() {
		playerData = YamlConfiguration.loadConfiguration(playerDataFile);
	}

	private boolean noPlayerInYML(UUID uuid) {
		ConfigurationSection sec = playerData.getConfigurationSection("players");
		return sec == null || !sec.contains(uuid.toString());
	}

	public void initializePlayerData() {
		UUID uuid;
		OfflinePlayer[] offlinePlayers = Bukkit.getOfflinePlayers();
		for(OfflinePlayer offlinePlayer: offlinePlayers) {
			uuid = offlinePlayer.getUniqueId();
			writePlayerData(uuid, true);
		}
	}

	public void setPVP(UUID uuid, boolean canPVP) {
		pvpPlayer.setCanPVP(uuid, canPVP);
		playerData.set("players."+uuid+".canPVP", canPVP);
	}

	public void setKillProtection(UUID uuid, boolean killProtAfterDeath) {
		pvpPlayer.setDeathKillProt(uuid, killProtAfterDeath);
		playerData.set("players."+uuid+".killProtAfterDeath", killProtAfterDeath);
	}

	public void remindMessage(Player player) {
		long msgDelay = plugin.getConfig().getLong("LoginReminderDelaySeconds");
		new BukkitRunnable() {
			public void run() {
				UUID uuid = player.getUniqueId();
				if(pvpPlayer.canPVP(uuid)) {
					player.sendMessage(Messages.TITLE+Messages.PVP_REMIND_ON.toString());
					String pl1 = Messages.YOUR.toString();
					String pl2 = Messages.YOU.toString();
					String s = "";
					if(pvpPlayer.isKillProtected(uuid)) {
						player.sendMessage(Messages.TITLE+
						                   Messages.KILL_PROTECT_ON.toString().replace("{pl1}", pl1).replace("{s1}", s)
						                                           .replace("{pl2}", pl2).replace("{s2}", s));
					} else {
						player.sendMessage(Messages.TITLE+
						                   Messages.KILL_PROTECT_OFF.toString().replace("{pl1}", pl1).replace("{s1}"
								                           , s)
						                                            .replace("{pl2}", pl2).replace("{s2}", s));
					}
				} else {
					player.sendMessage(Messages.TITLE+Messages.PVP_REMIND_OFF.toString());
				}
			}
		}.runTaskLater(plugin, 20L*msgDelay);
	}

	public void notifyForcePVPChange(boolean isPVPOn) {
		for(Player pl: getServer().getOnlinePlayers()) {
			if(isPVPOn) {
				pl.sendMessage(Messages.TITLE+Messages.NOTIFY_FORCE_PVP_ON.toString());
			} else {
				pl.sendMessage(Messages.TITLE+Messages.NOTIFY_FORCE_PVP_OFF.toString());
			}
		}
	}

	public void notifyForceProtChange(boolean isProtected) {
		for(Player pl: getServer().getOnlinePlayers()) {
			if(isProtected) {
				pl.sendMessage(Messages.TITLE+Messages.NOTIFY_FORCE_PVP_KILL_PROT_ON.toString());
			} else {
				pl.sendMessage(Messages.TITLE+Messages.NOTIFY_FORCE_PVP_KILL_PROT_OFF.toString());
			}
		}
	}

	public OfflinePlayer nameToPlayer(String name) {
		OfflinePlayer[] offlinePlayers = Bukkit.getOfflinePlayers();
		List<OfflinePlayer> result =
				Arrays.stream(offlinePlayers).filter(pl->name.equalsIgnoreCase(pl.getName())).toList();
		if(result.isEmpty()) {
			return null;
		}
		return result.get(0);
	}

	public void writePVPStatus(UUID uuid, boolean canPVP) {
		String uuid1 = uuid.toString();
		playerData.set("players."+uuid1+".canPVP", canPVP);
	}

	public void writeKillProtStatus(UUID uuid, boolean isProtected) {
		playerData.set("players."+uuid.toString()+".killProtAfterDeath", isProtected);
	}
}
