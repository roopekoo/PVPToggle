package me.roopekoo.pvptoggle;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PvpPlayer {
	private final HashMap<UUID, User> playerMap = new HashMap<>();
	PVPToggle pvpToggle = PVPToggle.getPlugin();

	public boolean hasPlayer(UUID uuid) {
		return playerMap.containsKey(uuid);
	}

	public void addNewPlayer(UUID uuid, boolean canPVP, boolean killProtAfterDeath) {
		User user = new User(uuid, canPVP, killProtAfterDeath);
		playerMap.put(uuid, user);
	}

	public void setCanPVP(UUID uuid, boolean canPVP) {
		playerMap.get(uuid).canPVP = canPVP;
	}

	public void setDeathKillProt(UUID uuid, boolean canPVP) {
		playerMap.get(uuid).deathKillProt = canPVP;
	}

	public boolean canPVP(UUID uuid) {
		return playerMap.get(uuid).canPVP;
	}

	public void setKillProtAfterDeath(UUID uuid, boolean killProtAfterDeath) {
		playerMap.get(uuid).deathKillProt = killProtAfterDeath;
	}

	public void forcePVPAll(boolean canPVP) {
		for(Map.Entry<UUID, User> entry: playerMap.entrySet()) {
			entry.getValue().canPVP = canPVP;
			UUID uuid = entry.getValue().uuid;
			pvpToggle.writePVPStatus(uuid, canPVP);
		}
	}

	public void forceKillProtAll(boolean isProtected) {
		for(Map.Entry<UUID, User> entry: playerMap.entrySet()) {
			entry.getValue().deathKillProt = isProtected;
			pvpToggle.writeKillProtStatus(entry.getKey(), isProtected);
		}
	}

	public void setLastAttacker(UUID victim_uuid, UUID attack_uuid) {
		playerMap.get(victim_uuid).lastAttacker = attack_uuid;
	}

	public UUID getLastAttacker(UUID uuid) {
		return playerMap.get(uuid).lastAttacker;
	}

	public void setLastCombatTime(UUID uuid) {
		playerMap.get(uuid).lastCombatTime = new Date().getTime();
	}

	public long getLastCombatTime(UUID uuid) {
		return playerMap.get(uuid).lastCombatTime;
	}

	public long getLatestMsgTime(UUID uuid) {
		return playerMap.get(uuid).lastOnPVPMsg;
	}

	public void setLatestMsgTime(UUID attack_uuid) {
		playerMap.get(attack_uuid).lastOnPVPMsg = new Date().getTime();
	}

	public boolean isKillProtected(UUID uuid) {
		return playerMap.get(uuid).deathKillProt;
	}

	public boolean isOnCombat(UUID uuid) {
		return playerMap.get(uuid).isOnCombat;
	}

	public long getTimeDifference(long prevTime) {
		long now = new Date().getTime();
		return now-prevTime;
	}

	public String msToStringTime(long diff) {
		long waitTime;
		String unit;
		if(diff/msToUnit.MINUTE.value>1) {
			waitTime = Math.round(diff/msToUnit.MINUTE.value);
			if(waitTime == 1) {
				unit = Messages.MINUTE.toString();
			} else {
				unit = Messages.MINUTES.toString();
			}
		} else {
			waitTime = Math.round(diff/msToUnit.SECOND.value);
			if(waitTime == 1) {
				unit = Messages.SECOND.toString();
			} else {
				unit = Messages.SECONDS.toString();
			}
		}
		return waitTime+" "+unit;
	}

	public void setOnCombat(UUID uuid, boolean isOnCombat) {
		playerMap.get(uuid).isOnCombat = isOnCombat;
	}

	private enum msToUnit {
		SECOND(1000), MINUTE(1000*60);

		public final double value;

		msToUnit(double value) {
			this.value = value;
		}
	}

	static final class User {
		UUID uuid;
		UUID lastAttacker;
		boolean canPVP;
		boolean deathKillProt;
		boolean isOnCombat = false;
		long lastCombatTime;
		long lastOnPVPMsg;

		public User(UUID uuid, boolean canPVP, boolean deathKillProt) {
			this.uuid = uuid;
			this.canPVP = canPVP;
			this.deathKillProt = deathKillProt;
		}
	}
}
