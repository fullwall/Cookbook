package com.fullwall.cookbook;

import com.nijikokun.bukkit.Permissions.Permissions;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class Permission {
	@SuppressWarnings("unused")
	private static Permissions permissionsPlugin;
	private static boolean permissionsEnabled = false;

	public static void initialize(Server server) {
		Plugin test = server.getPluginManager().getPlugin("Permissions");
		if (test != null) {
			permissionsPlugin = (Permissions) test;
			permissionsEnabled = true;
		} else {
			//Logger log = Logger.getLogger("Minecraft");
			//log.log(Level.SEVERE,
					//"[Cookbook]: Nijikokuns' Permissions plugin isn't loaded, disabling plugin.");
			//Listen.plugin.getServer().getPluginManager()
					//.disablePlugin(Listen.plugin);
		}
	}

	public static boolean isAdmin(Player player) {
		if (permissionsEnabled) {
			return permission(player, "");
		}
		return player.isOp();
	}

	private static boolean permission(Player player, String string) {
		return Permissions.Security.permission(player, string);
	}

	public static boolean check(Player player) {
		if (permissionsEnabled) {
			return permission(player, "");
		}
		return true;
	}

	public static boolean generic(Player player, String string) {
		if (permissionsEnabled) {
			return permission(player, string);
		}
		return true;
	}

}