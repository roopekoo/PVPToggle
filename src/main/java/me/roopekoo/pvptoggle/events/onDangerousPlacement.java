package me.roopekoo.pvptoggle.events;

import me.roopekoo.pvptoggle.PVPToggle;
import me.roopekoo.pvptoggle.PvpPlayer;
import me.roopekoo.pvptoggle.onCombat;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityPlaceEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;

import java.util.Collection;
import java.util.Objects;

public class onDangerousPlacement implements Listener {
	PVPToggle pvpToggle = PVPToggle.getPlugin();
	PvpPlayer pvpPlayer = pvpToggle.getPvpPlayerInstance();
	onCombat onCombat = pvpToggle.getonCombatInstance();
	int RADIUS = 2;

	@EventHandler public void onLavaPlace(PlayerBucketEmptyEvent e) {
		boolean isCancelled;
		Material bucket = e.getBucket();
		if(bucket.toString().contains("LAVA")) {
			Player placer = e.getPlayer();
			//Get nearby players
			Location loc = e.getBlock().getLocation();
			isCancelled = checkPVP(loc, placer);
			e.setCancelled(isCancelled);
		}
	}

	@EventHandler public void onFirePlace(BlockIgniteEvent e) {
		boolean isCancelled;
		Player igniter = e.getPlayer();
		if(igniter != null) {
			Location loc = e.getBlock().getLocation();
			isCancelled = checkPVP(loc, igniter);
			e.setCancelled(isCancelled);
		}
	}

	@EventHandler public void onEntityPlace(EntityPlaceEvent e) {
		boolean isCancelled;
		Player placer = e.getPlayer();
		if(placer != null) {
			String entity = e.getEntityType().toString();
			if(entity.equals("ENDER_CRYSTAL") || entity.equals("MINECART_TNT")) {
				Location loc = e.getBlock().getLocation();
				isCancelled = checkPVP(loc, placer);
				e.setCancelled(isCancelled);
			}
		}
	}

	@EventHandler public void onTNTPlace(BlockPlaceEvent e) {
		if(e.getBlock().getType().toString().equals("TNT")) {
			boolean isCancelled;
			Player placer = e.getPlayer();
			Location loc = e.getBlock().getLocation();
			isCancelled = checkPVP(loc, placer);
			e.setCancelled(isCancelled);
		}
	}

	private boolean checkPVP(Location loc, Player placer) {
		boolean isCancelled = false;
		Collection<Entity> entities =
				Objects.requireNonNull(loc.getWorld()).getNearbyEntities(loc, RADIUS, RADIUS, RADIUS);
		for(Entity entity: entities) {
			if(entity instanceof Player victim) {
				if(victim.getUniqueId() != placer.getUniqueId()) {
					if(pvpPlayer.canPVP(placer.getUniqueId())) {
						if(!pvpPlayer.canPVP(victim.getUniqueId())) {
							isCancelled = true;
							break;
						} else {
							pvpPlayer.setLastAttacker(victim.getUniqueId(), placer.getUniqueId());
							pvpPlayer.setLastCombatTime(placer.getUniqueId());
							pvpPlayer.setLastCombatTime(victim.getUniqueId());
							onCombat.canSendOnCombatMessages(placer);
							onCombat.canSendOnCombatMessages(victim);
						}
					} else {
						isCancelled = true;
						break;
					}
				}
			}
		}
		return isCancelled;
	}
}
