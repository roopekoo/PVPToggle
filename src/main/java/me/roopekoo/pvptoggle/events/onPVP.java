package me.roopekoo.pvptoggle.events;

import me.roopekoo.pvptoggle.Messages;
import me.roopekoo.pvptoggle.PVPToggle;
import me.roopekoo.pvptoggle.PvpPlayer;
import me.roopekoo.pvptoggle.onCombat;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.AreaEffectCloudApplyEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.projectiles.ProjectileSource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class onPVP implements Listener {
	FileConfiguration config = PVPToggle.loadConfig();
	long TIMEOUT = config.getInt("MsgTimeout");
	PVPToggle pvpToggle = PVPToggle.getPlugin();
	PvpPlayer pvpPlayer = pvpToggle.getPvpPlayerInstance();
	onPlayerDamage playerDamage = pvpToggle.getonPlayerDmgInstance();
	onCombat onCombat = pvpToggle.getonCombatInstance();

	@EventHandler public void on(EntityDamageByEntityEvent e) {
		// target is not a player
		if(!(e.getEntity() instanceof Player)) {
			if(e.getEntity().getType().toString().equals("ENDER_CRYSTAL")) {
				if(e.getDamager() instanceof Player player) {
					playerDamage.setExploder(player);
				}
				if(e.getDamager() instanceof Projectile projectile) {
					ProjectileSource source = projectile.getShooter();
					if(source instanceof Player player) {
						playerDamage.setExploder(player);
					}
				}
				if(e.getDamager() instanceof TNTPrimed tnt) {
					Entity source = tnt.getSource();
					if(source instanceof Player player) {
						playerDamage.setExploder(player);
					}
				}
			}
			return;
		}

		Player victim = ((Player) e.getEntity()).getPlayer();
		assert victim != null;
		Entity damager = e.getDamager();
		// Player damages with melee weapon
		if(damager instanceof Player) {
			checkCancel(e, (Player) damager, victim);
		}
		// Player damages by throwable entity
		if(damager instanceof Projectile projectile) {
			ProjectileSource source = projectile.getShooter();
			if(source instanceof Player) {
				checkCancel(e, (Player) source, victim);
			}
		}
		// Player damages by lingering potion
		if(damager instanceof AreaEffectCloud lingering) {
			ProjectileSource source = lingering.getSource();
			if(source instanceof Player) {
				checkCancel(e, (Player) source, victim);
			}
		}
		// Player damages by TNT
		if(damager instanceof TNTPrimed tnt) {
			Entity source = tnt.getSource();
			if(source instanceof Player attacker) {
				if(!checkCancel(e, attacker, victim)) {
					playerDamage.setExploder(attacker);
				}
			}
		}
	}

	@EventHandler public void onPotion(PotionSplashEvent e) {
		if(e.getEntity().getShooter() instanceof Player attacker) {
			Collection<PotionEffect> effects = e.getEntity().getEffects();
			Collection<LivingEntity> entities = e.getAffectedEntities();
			for(PotionEffect effect: effects) {
				if(effect.getType().getName().equals("POISON")) {
					for(LivingEntity entity: entities) {
						if(entity instanceof Player victim) {
							if(attacker.getUniqueId() != entity.getUniqueId()) {
								if(pvpPlayer.canPVP(attacker.getUniqueId())) {
									if(!pvpPlayer.canPVP(entity.getUniqueId())) {
										e.setIntensity(entity, 0);
									} else {
										pvpPlayer.setLastAttacker(victim.getUniqueId(), attacker.getUniqueId());
										pvpPlayer.setLastCombatTime(attacker.getUniqueId());
										pvpPlayer.setLastCombatTime(victim.getUniqueId());
										onCombat.canSendOnCombatMessages(attacker);
										onCombat.canSendOnCombatMessages(victim);
									}
								} else {
									e.setIntensity(entity, 0);
								}
							}
						}
					}
				}
			}
		}
	}

	@EventHandler public void onLingering(AreaEffectCloudApplyEvent e) {
		if(e.getEntity().getSource() instanceof Player attacker) {
			if(e.getEntity().getBasePotionData().getType().name().equals("POISON")) {
				List<LivingEntity> entities = e.getAffectedEntities();
				ArrayList<LivingEntity> protectedEntities = new ArrayList<>();

				for(Entity entity: entities) {
					if(entity instanceof Player victim) {
						if(victim.getUniqueId() != attacker.getUniqueId()) {
							if(pvpPlayer.canPVP(attacker.getUniqueId())) {
								if(!pvpPlayer.canPVP(victim.getUniqueId())) {
									protectedEntities.add(victim);
								} else {
									pvpPlayer.setLastAttacker(victim.getUniqueId(), attacker.getUniqueId());
									pvpPlayer.setLastCombatTime(attacker.getUniqueId());
									pvpPlayer.setLastCombatTime(victim.getUniqueId());
									onCombat.canSendOnCombatMessages(attacker);
									onCombat.canSendOnCombatMessages(victim);
								}
							} else {
								protectedEntities.add(victim);
							}
						}
					}
				}
				for(Entity entity: protectedEntities) {
					e.getAffectedEntities().remove(entity);
				}
			}
		}
	}


	private boolean PVPCheck(Player attacker, Player victim) {
		boolean isCancelled = false;
		boolean sendMsg = false;
		UUID attack_uuid = attacker.getUniqueId();
		UUID victim_uuid = victim.getUniqueId();
		String posSuf = "";

		long lastMsgTime = pvpPlayer.getLatestMsgTime(attack_uuid);

		if(pvpPlayer.getTimeDifference(lastMsgTime)>=TIMEOUT) {
			sendMsg = true;
			pvpPlayer.setLatestMsgTime(attack_uuid);
		}
		if(attack_uuid != victim_uuid) {
			if(pvpPlayer.canPVP(attack_uuid)) {
				if(!pvpPlayer.canPVP(victim_uuid)) {
					posSuf = Messages.POS_SUFFIX.toString();
					if(sendMsg) {
						attacker.sendMessage(Messages.TITLE+
						                     Messages.DENY_ATTACK.toString().replace("{pl}", victim.getName())
						                                         .replace("{s}", posSuf));
					}
					isCancelled = true;
				} else {
					//Attacker and victim can pvp
					pvpPlayer.setLastAttacker(victim_uuid, attack_uuid);
					pvpPlayer.setLastCombatTime(attack_uuid);
					pvpPlayer.setLastCombatTime(victim_uuid);
					onCombat.canSendOnCombatMessages(attacker);
					onCombat.canSendOnCombatMessages(victim);
				}
			} else {
				if(sendMsg) {
					attacker.sendMessage(Messages.TITLE+
					                     Messages.DENY_ATTACK.toString().replace("{pl}", Messages.YOUR.toString())
					                                         .replace("{s}", posSuf));
				}
				isCancelled = true;
			}
		}
		return isCancelled;
	}

	private boolean checkCancel(EntityDamageByEntityEvent e, Player source, Player victim) {
		boolean isCancelled = PVPCheck(source, victim);
		e.setCancelled(isCancelled);
		return isCancelled;
	}

	public void setTIMEOUT(long TIMEOUT) {
		this.TIMEOUT = TIMEOUT;
	}
}
