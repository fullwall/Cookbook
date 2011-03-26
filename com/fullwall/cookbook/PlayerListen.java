package com.fullwall.cookbook;

import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerListener;

public class PlayerListen extends PlayerListener {
	public static Cookbook plugin;

	public PlayerListen(Cookbook plugin) {
		PlayerListen.plugin = plugin;
	}

	@Override
	public void onPlayerJoin(PlayerEvent e) {
		e.getPlayer().sendRawMessage("񘘦񗱊");
	}
}
