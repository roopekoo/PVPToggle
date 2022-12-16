package me.roopekoo.pvptoggle.commands;

import me.roopekoo.pvptoggle.Messages;
import me.roopekoo.pvptoggle.PVPToggle;
import me.roopekoo.pvptoggle.PvpPlayer;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class pvp implements CommandExecutor {
	PVPToggle pvpToggle = PVPToggle.getPlugin();
	PvpPlayer pvpPlayer = pvpToggle.getPvpPlayerInstance();

	@Override public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(sender.hasPermission("pvptoggle.pvp")) {
			int len = args.length;
			if(len == 0) {
				sender.sendMessage(Messages.TITLE+Messages.NO_ENOUGH_PARAMS.toString());
				return false;
			}
			if(!(sender instanceof Player) && len == 1) {
				sender.sendMessage(Messages.TITLE+Messages.USER_MISSING.toString());
				return false;
			}
			UUID targetUUID = null;
			String targetPlName = null;
			String posSuf = "";
			if(len == 2) {
				if(args[1].equals("all")) {
					if(sender.hasPermission("pvptoggle.pvp.others.all")) {
						switch(args[0]) {
							case "on" -> {
								pvpPlayer.forcePVPAll(true);
								pvpToggle.writeFile();
								pvpToggle.notifyForcePVPChange(true);
								sender.sendMessage(Messages.TITLE+Messages.FORCE_PVP_ON.toString());
								return true;
							}
							case "off" -> {
								pvpPlayer.forcePVPAll(false);
								pvpToggle.writeFile();
								pvpToggle.notifyForcePVPChange(false);
								sender.sendMessage(Messages.TITLE+Messages.FORCE_PVP_OFF.toString());
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
				if(sender.hasPermission("pvptoggle.others")) {
					OfflinePlayer offlinePlayer = pvpToggle.nameToPlayer(args[1]);
					if(offlinePlayer != null) {
						targetUUID = offlinePlayer.getUniqueId();
						targetPlName = offlinePlayer.getName();
						posSuf = Messages.POS_SUFFIX.toString();
						if(sender instanceof Player senderPl) {
							if(!senderPl.getUniqueId().equals(targetUUID)) {
								notifyForcePvpChange(offlinePlayer, args[0]);
							}
						} else {
							notifyForcePvpChange(offlinePlayer, args[0]);
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
			if(len == 2 || len == 1) {
				if(targetUUID == null) {
					targetUUID = ((Player) sender).getUniqueId();
					targetPlName = Messages.YOUR.toString();
				}
				boolean canPVP = pvpPlayer.canPVP(targetUUID);
				if(!canTogglePvp(sender, canPVP, args[0])) {
					return true;
				}
				assert targetPlName != null;
				switch(args[0]) {
					case "on" -> {
						if(!canPVP) {
							pvpToggle.setPVP(targetUUID, true);
							pvpToggle.writeFile();
							sender.sendMessage(Messages.TITLE+Messages.PVP_ON.toString().replace("{pl}", targetPlName)
							                                                 .replace("{s}", posSuf));

						} else {
							sender.sendMessage(Messages.TITLE+
							                   Messages.PVP_ALREADY_ON.toString().replace("{pl}", targetPlName)
							                                          .replace("{s}", posSuf));
						}
						return true;
					}
					case "off" -> {
						if(canPVP) {
							pvpToggle.setPVP(targetUUID, false);
							pvpToggle.writeFile();
							sender.sendMessage(Messages.TITLE+Messages.PVP_OFF.toString().replace("{pl}", targetPlName)
							                                                  .replace("{s}", posSuf));
						} else {
							sender.sendMessage(Messages.TITLE+
							                   Messages.PVP_ALREADY_OFF.toString().replace("{pl}", targetPlName)
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

	private void notifyForcePvpChange(OfflinePlayer offlinePlayer, String arg) {
		if(offlinePlayer.isOnline()) {
			Player targetPl = (Player) offlinePlayer;
			if(arg.equals("on")) {
				targetPl.sendMessage(Messages.TITLE+Messages.NOTIFY_FORCE_PVP_ON.toString());
			} else if(arg.equals("off")) {
				targetPl.sendMessage(Messages.TITLE+Messages.NOTIFY_FORCE_PVP_OFF.toString());
			}
		}
	}

	private boolean canTogglePvp(CommandSender sender, boolean canPvp, String select) {
		if(sender.hasPermission("pvptoggle.pvp.bypass")) {
			return true;
		} else {
			UUID uuid = ((Player) sender).getUniqueId();
			if(select.equalsIgnoreCase("off") && canPvp && pvpPlayer.isOnCombat(uuid)) {
				sender.sendMessage(Messages.TITLE+Messages.DENY_COMBAT_PVP_OFF.toString());
				return false;
			}
		}
		return true;
	}

}
