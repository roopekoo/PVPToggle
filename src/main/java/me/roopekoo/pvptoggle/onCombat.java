package me.roopekoo.pvptoggle;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public class onCombat {
	PVPToggle plugin = PVPToggle.getPlugin();
	PvpPlayer pvpPlayer = plugin.getPvpPlayerInstance();
	long combatDelay = plugin.getConfig().getLong("CombatCooldownSeconds")*1000;

	private void remindCombatMessage(Player player) {
		UUID uuid = player.getUniqueId();
		long diff = pvpPlayer.getTimeDifference(pvpPlayer.getLastCombatTime(player.getUniqueId()));
		if(diff<combatDelay && combatDelay-diff>1000 && pvpPlayer.isOnCombat(uuid)) {
			long RemainingTicks = (combatDelay-diff)/50;
			String waitTime = pvpPlayer.msToStringTime(combatDelay-diff);
			player.sendMessage(Messages.TITLE+Messages.COMBAT_ON_TITLE.toString());
			player.sendMessage(Messages.TITLE+Messages.COMBAT_ON_TIME.toString().replace("{t}", waitTime));
			new BukkitRunnable() {
				public void run() {
					remindCombatMessage(player);
				}
			}.runTaskLater(plugin, RemainingTicks);
		} else {
			player.sendMessage("You are no longer in combat! You can log out safely.");
			pvpPlayer.setOnCombat(player.getUniqueId(), false);
		}

	}

	public void canSendOnCombatMessages(Player player) {
		UUID uuid = player.getUniqueId();
		if(!pvpPlayer.isOnCombat(uuid)) {
			pvpPlayer.setOnCombat(uuid, true);
			remindCombatMessage(player);
		}
	}

	public void setCombatDelay(long combatTimeout) {
		combatDelay = combatTimeout*1000;
	}
}
