package me.roopekoo.pvptoggle;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;

public enum Messages {
	TITLE("title", "&8[&ePVP&6Toggle&8] "),
	FILE_CREATE_FAIL1("file-create-fail1", "&cCouldn't create {filename} file."),
	FILE_CREATE_FAIL2("file-create-fail2", "&cThis is a fatal error. Now disabling!"),
	SAVE_FAIL1("save-fail1", "&cFailed to save {filename}."),
	SAVE_FAIL2("save-fail2", "&cReport this stack trace to Roopekoo."),
	NO_ENOUGH_PARAMS("Not-enough-parameters", "&cNot enough parameters!"),
	TOO_MANY_PARAMS("too-many-parameters", "&cToo many parameters!"),
	INCORRECT_PARAM("Incorrect-parameter", "&cIncorrect parameter!"),
	NO_PERM("no-permission", "&cYou do not have permission to do that!"),
	INCORRECT_USER("incorrect-username", "&cIncorrect username!"),
	USER_MISSING("username-missing", "&cPlease provide a username!"),
	FORCE_PVP_ON("Force-pvp-on", "&6Force-&2enabled &eeverybody's &2PVP!"),
	FORCE_PVP_OFF("Force-pvp-off", "&6Force-&4disabled &eeverybody's &2PVP!"),
	FORCE_PVP_KILL_PROT_ON("force-pvp-kill-protection-on", "&2Force-enabled everybody's pvp-kill protection!"),
	FORCE_PVP_KILL_PROT_OFF("force-pvp-kill-protection-off", "&2Force-disabled everybody's pvp-kill protection!"),
	AUTO_PVP_OFF("auto-pvp-off", "&eYour &aPVP has been switched &coff&a automatically!"),
	PVP_ON("pvp-on", "&e{pl}{s} &6PVP is &2enabled"),
	PVP_OFF("pvp-off", "&e{pl}{s} &6PVP is &4disabled"),
	PVP_ALREADY_ON("pvp-already-on", "&e{pl}{s} &6PVP is already &2enabled&6!"),
	PVP_ALREADY_OFF("pvp-already-off", "&e{pl}{s} &6PVP is already &4disabled&6!"),
	PROT_ALREADY_ON("protection-already-on", "&e{pl}{s} &6PVP-kill protection is already &2enabled!"),
	PROT_ALREADY_OFF("protection-already-off", "&e{pl}{s} &6PVP-kill protection is already &4disabled!"),
	PVP_REMIND_ON("pvp-remind-on", "&2Your &6PVP is &2enabled&6! &cWatch out for other blood thirsty players!"),
	PVP_REMIND_OFF("pvp-remind-off", "&2Your &6PVP is &4disabled&6! &aOther players cannot hurt you."),
	NOTIFY_FORCE_PVP_ON("notify-force-pvp-on", "&2Your &6PVP has been force-&2enabled&6!"),
	NOTIFY_FORCE_PVP_OFF("notify-force-pvp-off", "&2Your &6PVP has been force-&4disabled&6!"),
	NOTIFY_FORCE_PVP_KILL_PROT_ON("notify-force-pvp-kill-protection-on",
	                              "&2Your &6PVP-kill protection has been force-&2enabled&6!"),
	NOTIFY_FORCE_PVP_KILL_PROT_OFF("notify-force-pvp-kill-protection-off",
	                               "&2Your &6PVP-kill protection has been force-&4disabled&6!"),
	KILL_PROTECT_ON("kill-protect-on",
	                "&e{pl1}{s1} &6PVP will be set to &4OFF &6if &e{pl2} &6get{s2} killed by another player."),
	KILL_PROTECT_OFF("kill-protect-off",
	                 "&e{pl1}{s1} &6PVP will stay &2ON &6if &e{pl2} &6get{s2} killed by another player."),
	DENY_ATTACK("deny-attack", "&4You cannot hurt others! &e{pl}{s} &6PVP is not enabled!"),
	DENY_COMBAT_PVP_OFF("deny-combat-pvp-off", "&cYou cannot turn your pvp &4off &cwhile you are on &4combat&c!"),
	COMBAT_ON_TITLE("combat-on-title", "&cYou are in combat! You will be &4killed &cif you log off!"),
	COMBAT_ON_TIME("combat-on-time", "&cWait &6{t} &cto be able to log out safely."),
	COMBAT_OFF("combat-off", "&aYou are no longer in combat! You can log out &2safely."),
	COMBAT_SERVER_KILL("combat-server-kill", "&4&lYou were killed because you logged off during combat!"),
	RELOADED("reloaded", "&2Plugin reloaded!"),
	YOUR("your", "Your"),
	YOU("you", "you"),
	POS_SUFFIX("possessive-suffix", "'s"),
	PLURAL("plural", "s"),
	MINUTE("minute", "minute"),
	MINUTES("minutes", "minutes"),
	SECOND("second", "second"),
	SECONDS("seconds", "seconds");

	private static YamlConfiguration MSG;
	private final String def;
	private final String path;

	Messages(String path, String message) {
		this.path = path;
		this.def = message;
	}

	public static void setFile(YamlConfiguration messages) {
		MSG = messages;
	}

	@Override public String toString() {
		String s = MSG.getString(this.path, def);
		return ChatColor.translateAlternateColorCodes('&', s);
	}

	public String getPath() {
		return this.path;
	}

	public Object getDefault() {
		return this.def;
	}
}
