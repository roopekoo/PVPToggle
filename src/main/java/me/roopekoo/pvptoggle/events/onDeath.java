package me.roopekoo.pvptoggle.events;


import me.roopekoo.pvptoggle.Messages;
import me.roopekoo.pvptoggle.PVPToggle;
import me.roopekoo.pvptoggle.PvpPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.UUID;

public class onDeath implements Listener {
	PVPToggle pvpToggle = PVPToggle.getPlugin();
	PvpPlayer pvpPlayer = pvpToggle.getPvpPlayerInstance();

	@EventHandler void onPlayerDeath(PlayerDeathEvent e) {
		Player pl = e.getEntity();
		UUID uuid = pl.getUniqueId();
		UUID attacker_uuid = pvpPlayer.getLastAttacker(pl.getUniqueId());
		if(attacker_uuid != null) {
			long lastAttackTime = pvpPlayer.getLastCombatTime(attacker_uuid);
			// The server should manage with 2ms, with lag 8ms
			if(pvpPlayer.getTimeDifference(lastAttackTime)<10) {
				if(pvpPlayer.canPVP(uuid)) {
					if(pvpPlayer.isKillProtected(uuid)) {
						pvpPlayer.setCanPVP(uuid, false);
						pvpToggle.setPVP(uuid, false);
						pvpToggle.writeFile();
						pvpPlayer.setOnCombat(uuid, false);
						pl.sendMessage(Messages.TITLE+Messages.AUTO_PVP_OFF.toString());
					}
				}
			}
		}
	}
}
