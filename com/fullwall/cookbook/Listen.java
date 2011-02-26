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
	public static ArrayList<Thread> runningThreads = new ArrayList<Thread>();

	public Listen(Cookbook plugin) {
		Listen.plugin = plugin;
	}

	public void onBlockInteract(BlockInteractEvent e) {
		if (e.getBlock().getTypeId() == 58 && e.getEntity() instanceof Player) {
			// start thread
			Thread newThread = new Thread(
					new WorkThread((Player) e.getEntity()));
			newThread.start();
			runningThreads.add(newThread);
		}
	}

}