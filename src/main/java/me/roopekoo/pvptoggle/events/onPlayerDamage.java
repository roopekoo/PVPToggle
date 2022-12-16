package me.roopekoo.pvptoggle.events;

import me.roopekoo.pvptoggle.PVPToggle;
import me.roopekoo.pvptoggle.PvpPlayer;
import me.roopekoo.pvptoggle.onCombat;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.data.type.RespawnAnchor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.Set;
import java.util.UUID;

public class onPlayerDamage implements Listener {
	PVPToggle pvpToggle = PVPToggle.getPlugin();
	PvpPlayer pvpPlayer = pvpToggle.getPvpPlayerInstance();
	onCombat onCombat = pvpToggle.getonCombatInstance();
	private Player exploder;


	@EventHandler public void onPlayerHurt(EntityDamageEvent e) {
		Entity test = e.getEntity();
		if(exploder == null) {
			return;
		}
		if(e.getEntity() instanceof Player victim) {
			if(e.getCause().toString().equals("BLOCK_EXPLOSION")) {
				UUID attacker_uuid = exploder.getUniqueId();
				UUID victim_uuid = victim.getUniqueId();
				if(attacker_uuid != victim_uuid) {
					if(pvpPlayer.canPVP(attacker_uuid)) {
						if(!pvpPlayer.canPVP(victim_uuid)) {
							e.setCancelled(true);
						} else {
							pvpPlayer.setLastAttacker(victim_uuid, attacker_uuid);
							pvpPlayer.setLastCombatTime(attacker_uuid);
							pvpPlayer.setLastCombatTime(victim_uuid);
							onCombat.canSendOnCombatMessages(exploder);
							onCombat.canSendOnCombatMessages(victim);
						}
					} else {
						e.setCancelled(true);
					}
				}
			}
			if(e.getCause().toString().equals("ENTITY_EXPLOSION")) {
				UUID attacker_uuid = exploder.getUniqueId();
				UUID victim_uuid = victim.getUniqueId();
				if(attacker_uuid != victim_uuid) {
					if(pvpPlayer.canPVP(attacker_uuid)) {
						if(!pvpPlayer.canPVP(victim_uuid)) {
							e.setCancelled(true);
						} else {
							pvpPlayer.setLastAttacker(victim_uuid, attacker_uuid);
							pvpPlayer.setLastCombatTime(attacker_uuid);
							pvpPlayer.setLastCombatTime(victim_uuid);
							onCombat.canSendOnCombatMessages(exploder);
							onCombat.canSendOnCombatMessages(victim);
						}
					} else {
						e.setCancelled(true);
					}
				}
			}
			exploder = null;
		}
	}

	@EventHandler public void onInteractExplosive(PlayerInteractEvent e) {
		if(e.getClickedBlock() != null) {
			if(e.getAction().toString().equals("RIGHT_CLICK_BLOCK")) {
				Set<Material> beds = Tag.BEDS.getValues();
				Material material = e.getClickedBlock().getType();
				String dim = e.getPlayer().getWorld().getEnvironment().toString();
				Player pl = e.getPlayer();
				if(beds.contains(material)) {
					if(dim.equals("NETHER") || dim.equals("THE_END")) {
						exploder = pl;
					}
				}
				if(material.toString().equals("RESPAWN_ANCHOR")) {
					if(dim.equals("NORMAL") || dim.equals("THE_END")) {
						RespawnAnchor ra = (RespawnAnchor) e.getClickedBlock().getBlockData();
						if(e.getMaterial().toString().equals("GLOWSTONE")) {
							if(ra.getCharges() == 4) {
								exploder = pl;
							}
						} else {
							if(ra.getCharges()>=1) {
								exploder = pl;
							}
						}
					}
				}
			}
		}
	}

	@EventHandler public void playerEntityExplosion(PlayerInteractEntityEvent e) {
		if(e.getRightClicked().getName().equals("Creeper")) {
			if(e.getPlayer().getInventory().getItem(e.getHand()).getType().toString().equals("FLINT_AND_STEEL")) {
				exploder = e.getPlayer();
			}
		}
	}

	public void setExploder(Player exploder) {
		this.exploder = exploder;
	}
}
