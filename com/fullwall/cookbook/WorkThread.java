package com.fullwall.cookbook;

import net.minecraft.server.ContainerWorkbench;
import net.minecraft.server.EntityPlayer;
import net.minecraft.server.ItemStack;
import net.minecraft.server.Packet103SetSlot;

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

	@Override
	public void run() {
		try {
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
			if (containerBench.a != null
					&& Cookbook.instance.getResult(containerBench.a) != null) {
				ItemStack is = Cookbook.instance.getResult(containerBench.a);
				if (is.id == 0)
					is = null;
				containerBench.b.a(0, is);
				Packet103SetSlot packet = new Packet103SetSlot(
						entityPlayer.activeContainer.f, 0, is);
				entityPlayer.a.b(packet);
			}
			if (!craftPlayer.isOnline())
				kill();
		} catch (Exception ex) {
			Cookbook.log.info("[Cookbook]: Error in workbench task. Error is: "
					+ ex.getMessage() + ". Stack trace: "
					+ ex.getStackTrace()[0]);
			return;
		}
	}

	public void kill() {
		plugin.getServer().getScheduler().cancelTask(id);
		int index = PlayerListen.tasks.indexOf(id);
		if (index != -1)
			PlayerListen.tasks.remove(PlayerListen.tasks.indexOf(id));
	}
}
