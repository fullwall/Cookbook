package com.fullwall.cookbook;

import java.util.ArrayList;

import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerListener;

public class PlayerListen extends PlayerListener {
	public static Cookbook plugin;
	public static ArrayList<Integer> tasks = new ArrayList<Integer>();

	public PlayerListen(Cookbook plugin) {
		PlayerListen.plugin = plugin;
	}

	@Override
	public void onPlayerJoin(PlayerJoinEvent e) {
		e.getPlayer().sendRawMessage("񘘦񗱊");
	}

	public void onPlayerInteract(PlayerInteractEvent e) {
		if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
			if (Cookbook.recipeObjects.size() > 0
					&& e.getClickedBlock().getTypeId() == 58) {
				// start task
				WorkThread task = new WorkThread(e.getPlayer(), plugin);
				int id = plugin
						.getServer()
						.getScheduler()
						.scheduleSyncRepeatingTask(plugin, task, 0,
								Cookbook.delay);
				tasks.add(id);
				task.addID(id);
			} else if (Cookbook.furnaceRecipeObjects.size() > 0
					&& (e.getClickedBlock().getTypeId() == 61 || e
							.getClickedBlock().getTypeId() == 62)) {
				// start task
				FurnaceWorkThread task = new FurnaceWorkThread(e.getPlayer(),
						plugin);
				int id = plugin
						.getServer()
						.getScheduler()
						.scheduleSyncRepeatingTask(plugin, task, 0,
								Cookbook.delay);
				tasks.add(id);
				task.addID(id);
			}
		}
	}
}
