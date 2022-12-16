package me.roopekoo.pvptoggle.events;


import me.roopekoo.pvptoggle.Messages;
import me.roopekoo.pvptoggle.PVPToggle;
import me.roopekoo.pvptoggle.PvpPlayer;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.UUID;

public class onPlayerLogEvent implements Listener {
	PVPToggle pvpToggle = PVPToggle.getPlugin();
	PvpPlayer pvpPlayer = pvpToggle.getPvpPlayerInstance();
	long combatLogKillTime = pvpToggle.getConfig().getLong("CombatLogOffWaitSeconds");
	ArrayList<UUID> killList = new ArrayList<>();

	@EventHandler public void onJoin(PlayerJoinEvent e) {
		Player player = e.getPlayer();
		UUID uuid = player.getUniqueId();
		if(killList.contains(uuid)) {
			if(!player.hasPermission("pvptoggle.combatlog.bypass")) {
				player.setHealth(0);
				player.sendMessage(Messages.TITLE+Messages.COMBAT_SERVER_KILL.toString());
				pvpPlayer.setOnCombat(uuid, false);
			}
			killList.remove(uuid);
		}
		pvpToggle.writePlayerData(uuid, false);
		pvpToggle.writeFile();
		pvpToggle.remindMessage(e.getPlayer());
	}

	@EventHandler public void onLeave(PlayerQuitEvent e) {
		OfflinePlayer offlinePlayer = e.getPlayer();
		UUID uuid = offlinePlayer.getUniqueId();
		if(pvpPlayer.isOnCombat(uuid)) {
			new BukkitRunnable() {
				public void run() {
					if(!offlinePlayer.isOnline()) {
						killList.add(uuid);
					}
				}
			}.runTaskLater(pvpToggle, 20L*combatLogKillTime);
		}
	}
}
