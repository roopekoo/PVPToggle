package me.roopekoo.pvptoggle.commands;

import me.roopekoo.pvptoggle.Messages;
import me.roopekoo.pvptoggle.PVPToggle;
import me.roopekoo.pvptoggle.events.onPVP;
import me.roopekoo.pvptoggle.onCombat;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

public class pvpReload implements CommandExecutor {
	PVPToggle pvpToggle = PVPToggle.getPlugin();

	@Override public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(!sender.hasPermission("pvptoggle.pvpreload")) {
			sender.sendMessage(Messages.TITLE+Messages.NO_PERM.toString());
			return true;
		}
		if(args.length>0) {
			sender.sendMessage(Messages.TITLE+Messages.TOO_MANY_PARAMS.toString());
			return false;
		} else {
			pvpToggle.reloadConfig();
			onPVP onPvp = pvpToggle.getonPVPInstance();
			onCombat onCombat = pvpToggle.getonCombatInstance();
			FileConfiguration config = pvpToggle.getConfig();
			int timeout = config.getInt("MsgTimeout");
			long onCombatCooldown = config.getLong("CombatCooldownSeconds");
			onPvp.setTIMEOUT(timeout);
			onCombat.setCombatDelay(onCombatCooldown);
			pvpToggle.loadMessages();
			pvpToggle.reloadPlayerData();
			pvpToggle.initializePlayerData();
			sender.sendMessage(Messages.TITLE+Messages.RELOADED.toString());
		}
		return true;
	}
}
