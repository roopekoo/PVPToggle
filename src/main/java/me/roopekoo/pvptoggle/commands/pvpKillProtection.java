package me.roopekoo.pvptoggle.commands;

import me.roopekoo.pvptoggle.Messages;
import me.roopekoo.pvptoggle.PVPToggle;
import me.roopekoo.pvptoggle.PvpPlayer;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class pvpKillProtection implements CommandExecutor {
	PVPToggle pvpToggle = PVPToggle.getPlugin();
	PvpPlayer pvpPlayer = pvpToggle.getPvpPlayerInstance();

	@Override public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(sender.hasPermission("pvptoggle.protect")) {
			int len = args.length;
			if(len == 0) {
				sender.sendMessage(Messages.TITLE+Messages.NO_ENOUGH_PARAMS.toString());
				return false;
			}
			OfflinePlayer targetPl = null;
			String targetPl1 = "";
			String targetPl2 = "";
			String posSuf = Messages.POS_SUFFIX.toString();
			String plural = Messages.PLURAL.toString();
			if(len == 2) {
				if(args[1].equals("all")) {
					if(sender.hasPermission("pvptoggle.protect.others.all")) {
						switch(args[0]) {
							case "on" -> {
								pvpPlayer.forceKillProtAll(true);
								pvpToggle.writeFile();
								pvpToggle.notifyForceProtChange(true);
								sender.sendMessage(Messages.TITLE+Messages.FORCE_PVP_KILL_PROT_ON.toString());
								return true;
							}
							case "off" -> {
								pvpPlayer.forceKillProtAll(true);
								pvpToggle.writeFile();
								pvpToggle.notifyForceProtChange(false);
								sender.sendMessage(Messages.TITLE+Messages.FORCE_PVP_KILL_PROT_OFF.toString());
								return true;
							}
							default -> {
								sender.sendMessage(Messages.TITLE+Messages.INCORRECT_PARAM.toString());
								return false;
							}
						}
					} else {
						sender.sendMessage(Messages.TITLE+Messages.NO_PERM.toString());
					}
					return true;
				}
				if(sender.hasPermission("pvptoggle.protect.others")) {
					OfflinePlayer offlinepl = pvpToggle.nameToPlayer(args[1]);
					if(offlinepl != null) {
						targetPl = offlinepl;
						targetPl1 = offlinepl.getName();
						targetPl2 = offlinepl.getName();
						if(sender instanceof Player senderPl) {
							if(!senderPl.getUniqueId().equals(offlinepl.getUniqueId())) {
								notifyForceProtChange(offlinepl, args[0]);
							}
						} else {
							notifyForceProtChange(offlinepl, args[0]);
						}
					} else {
						sender.sendMessage(Messages.TITLE+Messages.INCORRECT_USER.toString());
						return true;
					}
				} else {
					sender.sendMessage(Messages.TITLE+Messages.NO_PERM.toString());
					return true;
				}
			}
			if(len == 1 || len == 2) {
				if(!(sender instanceof Player) && len == 1) {
					sender.sendMessage(Messages.TITLE+Messages.USER_MISSING.toString());
					return false;
				}
				if(targetPl == null) {
					targetPl = (Player) sender;
					targetPl1 = Messages.YOUR.toString();
					targetPl2 = Messages.YOU.toString();
					posSuf = "";
					plural = "";
				}
				boolean isProtected = pvpPlayer.isKillProtected(targetPl.getUniqueId());
				assert targetPl1 != null;
				assert targetPl2 != null;
				switch(args[0]) {
					case "on" -> {
						if(!isProtected) {
							pvpToggle.setKillProtection(targetPl.getUniqueId(), true);
							pvpToggle.writeFile();
							sender.sendMessage(Messages.TITLE+
							                   Messages.KILL_PROTECT_ON.toString().replace("{pl1}", targetPl1)
							                                           .replace("{s1}", posSuf)
							                                           .replace("{pl2}", targetPl2)
							                                           .replace("{s2}", plural));
						} else {
							sender.sendMessage(Messages.TITLE+
							                   Messages.PROT_ALREADY_ON.toString().replace("{pl}", targetPl1)
							                                           .replace("{s}", posSuf));
						}
						return true;
					}
					case "off" -> {
						if(isProtected) {
							pvpToggle.setKillProtection(targetPl.getUniqueId(), false);
							pvpToggle.writeFile();
							sender.sendMessage(Messages.TITLE+
							                   Messages.KILL_PROTECT_OFF.toString().replace("{pl1}", targetPl1)
							                                            .replace("{s1}", posSuf)
							                                            .replace("{pl2}", targetPl2)
							                                            .replace("{s2}", plural));
						} else {
							sender.sendMessage(Messages.TITLE+
							                   Messages.PROT_ALREADY_OFF.toString().replace("{pl}", targetPl1)
							                                            .replace("{s}", posSuf));
						}
						return true;
					}
					default -> {
						sender.sendMessage(Messages.TITLE+Messages.INCORRECT_PARAM.toString());
						return false;
					}
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

	private void notifyForceProtChange(OfflinePlayer offlinepl, String arg) {
		if(offlinepl.isOnline()) {
			Player targetPl = (Player) offlinepl;
			if(arg.equals("on")) {
				targetPl.sendMessage(Messages.TITLE+Messages.NOTIFY_FORCE_PVP_KILL_PROT_ON.toString());
			} else if(arg.equals("off")) {
				targetPl.sendMessage(Messages.TITLE+Messages.NOTIFY_FORCE_PVP_KILL_PROT_OFF.toString());
			}
		}
	}
}
