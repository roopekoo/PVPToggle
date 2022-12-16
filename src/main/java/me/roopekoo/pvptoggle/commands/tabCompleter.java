package me.roopekoo.pvptoggle.commands;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class tabCompleter implements TabCompleter {
	@Override public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		if(command.getName().equalsIgnoreCase("pvp")) {
			if(sender.hasPermission("pvptoggle.pvp")) {
				if(args.length == 1) {
					List<String> list = new ArrayList<>();
					if("on".contains(args[0])) {
						list.add("on");
					}
					if("off".contains(args[0])) {
						list.add("off");
					}
					return list;
				}
				if(args.length == 2) {
					List<String> list = new ArrayList<>();
					if(sender.hasPermission("pvptoggle.pvp.others.all")) {
						if("all".contains(args[1])) {
							list.add("all");
						}
					}
					if(sender.hasPermission("pvptoggle.pvp.others")) {
						for(OfflinePlayer p: Bukkit.getOfflinePlayers()) {
							if(Objects.requireNonNull(p.getName()).contains(args[1])) {
								list.add(p.getName());
							}
						}
					}
					return list;
				}
			} else {
				return null;
			}
		}
		if(command.getName().equalsIgnoreCase("pvpcheck")) {
			if(sender.hasPermission("pvptoggle.pvpcheck.others")) {
				if(args.length == 1) {
					List<String> list = new ArrayList<>();
					for(OfflinePlayer p: Bukkit.getOfflinePlayers()) {
						if(Objects.requireNonNull(p.getName()).contains(args[0])) {
							list.add(p.getName());
						}
					}
					return list;
				} else {
					return null;
				}
			}
		}
		if(command.getName().equalsIgnoreCase("pvpkillprotection")) {
			if(sender.hasPermission("pvptoggle.protect")) {
				if(args.length == 1) {
					List<String> list = new ArrayList<>();
					if("on".contains(args[0])) {
						list.add("on");
					}
					if("off".contains(args[0])) {
						list.add("off");
					}
					return list;
				}
				if(args.length == 2) {
					List<String> list = new ArrayList<>();
					if(sender.hasPermission("pvptoggle.protect.others.all")) {
						if("all".contains(args[1])) {
							list.add("all");
						}
					}
					if(sender.hasPermission("pvptoggle.protect.others")) {
						for(OfflinePlayer p: Bukkit.getOfflinePlayers()) {
							if(Objects.requireNonNull(p.getName()).contains(args[1])) {
								list.add(p.getName());
							}
						}
					}
					return list;
				}
			} else {
				return null;
			}
		}
		return null;
	}
}
