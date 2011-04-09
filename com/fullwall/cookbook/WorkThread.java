package com.fullwall.cookbook;

import net.minecraft.server.ContainerWorkbench;
import net.minecraft.server.EntityPlayer;
import net.minecraft.server.InventoryCraftResult;
import net.minecraft.server.ItemStack;

import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class WorkThread implements Runnable {
	private CraftPlayer craftPlayer;
	private EntityPlayer entityPlayer;
	private Cookbook plugin;
	private int id;

	public WorkThread(Player p, Cookbook plugin) {
		this.craftPlayer = (CraftPlayer) p;
		this.entityPlayer = craftPlayer.getHandle();
		this.plugin = plugin;
	}

	public void addID(int id) {
		this.id = id;
	}

	@SuppressWarnings("unused")
	@Override
	public void run() {
		if (entityPlayer == null
				|| entityPlayer.activeContainer == entityPlayer.defaultContainer) {
			kill();
			return;
		}
		ContainerWorkbench containerBench = null;
		try {
			containerBench = (ContainerWorkbench) entityPlayer.activeContainer;
		} catch (Exception ex) {
			kill();
			return;
		}
		ItemStack result = ((InventoryCraftResult) containerBench.b)
				.getContents()[0];
		// ((ContainerWorkbench) ep.activeContainer).b.getContents()[0];
		if (containerBench.a != null
				&& Cookbook.instance.getResult(containerBench.a) != null) {
			ItemStack is = Cookbook.instance.getResult(containerBench.a);
			if (is.id == 0)
				is = null;
			result = is;
			containerBench.b.a(0, is);
			// ((InventoryCraftResult) containerBench.b);
		}
		if (!craftPlayer.isOnline())
			kill();
	}

	public void kill() {
		plugin.getServer().getScheduler().cancelTask(id);
		int index = PlayerListen.tasks.indexOf(id);
		if (index != -1)
			PlayerListen.tasks.remove(PlayerListen.tasks.indexOf(id));
	}
}
