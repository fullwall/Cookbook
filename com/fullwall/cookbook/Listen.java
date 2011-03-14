package com.fullwall.cookbook;

import java.util.ArrayList;

import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockInteractEvent;
import org.bukkit.event.block.BlockListener;

/**
 * Listener
 * 
 * @author fullwall
 */
public class Listen extends BlockListener {

	public static Cookbook plugin;
	public static ArrayList<Integer> tasks = new ArrayList<Integer>();

	public Listen(Cookbook plugin) {
		Listen.plugin = plugin;
	}

	public void onBlockInteract(BlockInteractEvent e) {
		if ((Cookbook.shapelessRecipes.size() > 0 || Cookbook.recipes.size() > 0)
				&& e.getBlock().getTypeId() == 58
				&& e.getEntity() instanceof Player) {
			// start task
			WorkThread task = new WorkThread((Player) e.getEntity(), plugin);
			int id = plugin.getServer().getScheduler()
					.scheduleSyncRepeatingTask(plugin, task, 0, Cookbook.delay);
			tasks.add(id);
			task.addID(id);
		} else if (Cookbook.furnaceRecipes.size() > 0
				&& (e.getBlock().getTypeId() == 61 || e.getBlock().getTypeId() == 62)
				&& e.getEntity() instanceof Player) {
			// start task
			FurnaceWorkThread task = new FurnaceWorkThread(
					(Player) e.getEntity(), plugin);
			int id = plugin.getServer().getScheduler()
					.scheduleSyncRepeatingTask(plugin, task, 0, Cookbook.delay);
			tasks.add(id);
			task.addID(id);
		}
	}
}