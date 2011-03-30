package com.fullwall.cookbook;

import java.lang.reflect.Field;

import net.minecraft.server.ContainerFurnace;
import net.minecraft.server.EntityPlayer;
import net.minecraft.server.FurnaceRecipes;
import net.minecraft.server.ItemStack;
import net.minecraft.server.TileEntityFurnace;

import org.bukkit.Material;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class FurnaceWorkThread implements Runnable {
	private CraftPlayer craftPlayer;
	private EntityPlayer entityPlayer;
	private Cookbook plugin;
	private int id;

	public FurnaceWorkThread(Player p, Cookbook plugin) {
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
		ContainerFurnace furnace = null;
		try {
			furnace = (ContainerFurnace) entityPlayer.activeContainer;
		} catch (Exception ex) {
			kill();
			return;
		}
		Field privateTileEntity;
		try {
			privateTileEntity = ContainerFurnace.class.getDeclaredField("a");
		} catch (Exception ex) {
			Cookbook.log
					.info("[Cookbook]: Error type FURNACE_GET_FIELD: report!");
			return;
		}
		privateTileEntity.setAccessible(true);
		TileEntityFurnace tileEntity;

		try {
			tileEntity = (TileEntityFurnace) privateTileEntity.get(furnace);
		} catch (Exception ex) {
			Cookbook.log.info("[Cookbook]: Error type FURNACE_GET: report!");
			return;
		}

		ItemStack ingredient = tileEntity.c_(0);
		ItemStack fuel = tileEntity.c_(1);
		ItemStack result = tileEntity.c_(2);
		if ((ingredient != null) && FurnaceRecipes.a().a(ingredient.id) != null) {
			if (RecipeManager.containsIngredient(ingredient.id)
					&& tileEntity.a > 0
					&& (result == null || result.count != org.bukkit.Material
							.getMaterial(result.id).getMaxStackSize())) {
				if (result != null) {
					int maxStackSize = Material.getMaterial(result.id)
							.getMaxStackSize();
					if (maxStackSize == -1 || maxStackSize > 64)
						maxStackSize = Material.getMaterial(result.id)
								.getMaxDurability();
					if (result.count == maxStackSize)
						return;
				}
				tileEntity.c += RecipeManager
						.getCooktimeFromIngredient(ingredient.id);
				if (tileEntity.c > 200)
					tileEntity.c = 199;
			}
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
