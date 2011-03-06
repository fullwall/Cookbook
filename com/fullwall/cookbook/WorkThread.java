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

	public WorkThread(Player p) {
		this.craftPlayer = (CraftPlayer) p;
		this.entityPlayer = craftPlayer.getHandle();

		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			kill();
		}

	}

	@SuppressWarnings("unused")
	@Override
	public void run() {
		while (!Thread.interrupted()) {
			if (entityPlayer == null
					|| entityPlayer.activeContainer == entityPlayer.defaultContainer) {
				kill();
				break;
			}
			ContainerWorkbench containerBench = null;
			try {
				containerBench = (ContainerWorkbench) entityPlayer.activeContainer;
			} catch (Exception ex) {
				kill();
				break;
			}

			ItemStack result = ((InventoryCraftResult) containerBench.b)
					.getContents()[0];
			// ((ContainerWorkbench) ep.activeContainer).b.getContents()[0];
			boolean a = false;
			if (CraftResults.getInstance().getResult(containerBench.a) != null) {
				ItemStack checkResults = CraftResults.getInstance().getResult(
						containerBench.a);
				if (checkResults != null) {
					a = true;
				}
				if (a) {
					ItemStack is = checkResults;
					result = is;
					containerBench.b.a(0, is);
					// ((InventoryCraftResult) containerBench.b);
				}
			}
			try {
				Thread.sleep(100);
				if (!craftPlayer.isOnline())
					kill();
			} catch (InterruptedException e) {
				Cookbook.log.info("[Cookbook]: Interrupted thread.");
			}
		}
	}

	public void kill() {
		Listen.runningThreads.remove(this);
		Thread kill = Thread.currentThread();
		kill.interrupt();
	}
}
