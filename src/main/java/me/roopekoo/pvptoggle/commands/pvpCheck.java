package me.roopekoo.pvptoggle.commands;

import me.roopekoo.pvptoggle.Messages;
import me.roopekoo.pvptoggle.PVPToggle;
import me.roopekoo.pvptoggle.PvpPlayer;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class pvpCheck implements CommandExecutor {
	PVPToggle pvpToggle = PVPToggle.getPlugin();
	PvpPlayer pvpPlayer = pvpToggle.getPvpPlayerInstance();

	@Override public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		String posSuf = "";
		String plural = "";
		if(sender.hasPermission("pvptoggle.pvpcheck")) {
			if(args.length == 0) {
				if(sender instanceof Player pl) {
					String pl1 = Messages.YOUR.toString();
					String pl2 = Messages.YOU.toString();
					boolean canPVP = pvpPlayer.canPVP(pl.getUniqueId());
					boolean isProtected = pvpPlayer.isKillProtected(pl.getUniqueId());
					PVPmessage(sender, canPVP, pl1, posSuf);
					PVPKillProtectMessage(sender, canPVP, isProtected, pl1, pl2, posSuf, plural);
					return true;
				} else {
					sender.sendMessage(Messages.TITLE+Messages.USER_MISSING.toString());
					return false;
				}
			}
			if(args.length == 1) {
				if(sender.hasPermission("pvptoggle.pvpcheck.others")) {
					OfflinePlayer pl = pvpToggle.nameToPlayer(args[0]);
					if(pl != null) {
						String user = pl.getName();
						posSuf = Messages.POS_SUFFIX.toString();
						plural = Messages.PLURAL.toString();
						boolean canPVP = pvpPlayer.canPVP(pl.getUniqueId());
						boolean isProtected = pvpPlayer.isKillProtected(pl.getUniqueId());
						PVPmessage(sender, canPVP, user, posSuf);
						PVPKillProtectMessage(sender, canPVP, isProtected, user, user, posSuf, plural);
						return true;
					} else {
						sender.sendMessage(Messages.TITLE+Messages.INCORRECT_USER.toString());
					}
				} else {
					sender.sendMessage(Messages.TITLE+Messages.NO_PERM.toString());
					return true;
				}
			} else {
				sender.sendMessage(Messages.TITLE+Messages.TOO_MANY_PARAMS.toString());
			}
		} else {
			sender.sendMessage(Messages.TITLE+Messages.NO_PERM.toString());
			return true;
		}
		return false;
	}

	private void PVPmessage(CommandSender sender, boolean canPVP, String player, String posSuf) {
		if(canPVP) {
			sender.sendMessage(
					Messages.TITLE+Messages.PVP_ON.toString().replace("{pl}", player).replace("{s}", posSuf));
		} else {
			sender.sendMessage(
					Messages.TITLE+Messages.PVP_OFF.toString().replace("{pl}", player).replace("{s}", posSuf));
		}
	}

	private void PVPKillProtectMessage(CommandSender sender, boolean canPvp, boolean isProtected, String pl1,
	                                   String pl2, String posSuf, String plural) {
		if(canPvp) {
			if(isProtected) {
				sender.sendMessage(Messages.TITLE+
				                   Messages.KILL_PROTECT_ON.toString().replace("{pl1}", pl1).replace("{s1}", posSuf)
				                                           .replace("{pl2}", pl2).replace("{s2}", plural));
			} else {
				sender.sendMessage(Messages.TITLE+
				                   Messages.KILL_PROTECT_OFF.toString().replace("{pl1}", pl1).replace("{s1}", posSuf)
				                                            .replace("{pl2}", pl2).replace("{s2}", plural));
			}
		}
	}
}
