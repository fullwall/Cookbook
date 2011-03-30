package com.fullwall.cookbook;

import org.bukkit.inventory.ItemStack;

public class FurnaceRecipe {

	private int ingredient;
	private ItemStack result;
	private double cooktime;

	public FurnaceRecipe(int ingredient, ItemStack result, double cooktime) {
		this.ingredient = ingredient;
		this.result = result;
		this.cooktime = cooktime;
	}

	public int getIngredient() {
		return this.ingredient;
	}

	public void setIngredient(int ingredient) {
		this.ingredient = ingredient;
	}

	public ItemStack getResult() {
		return this.result;
	}

	public void setResult(ItemStack result) {
		this.result = result;
	}

	public double getCooktime() {
		return this.cooktime;
	}

	public void setCooktime(double cooktime) {
		this.cooktime = cooktime;
	}

}
