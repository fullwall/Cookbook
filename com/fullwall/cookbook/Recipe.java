package com.fullwall.cookbook;

import java.util.ArrayList;

import org.bukkit.inventory.ItemStack;

public class Recipe {

	private ArrayList<Integer> recipe;
	private ArrayList<Integer> dataValues;
	private ItemStack result;
	private boolean shapeless;
	private int data;

	public Recipe(ArrayList<Integer> recipe, ArrayList<Integer> dataValues,
			ItemStack result, boolean shapeless, int data) {
		this.recipe = recipe;
		this.dataValues = dataValues;
		this.result = result;
		this.shapeless = shapeless;
		this.data = data;
	}

	public ItemStack getResult() {
		return this.result;
	}

	public int getData() {
		return this.data;
	}

	public void setResult(ItemStack result) {
		this.result = result;
	}

	public ArrayList<Integer> getIDs() {
		return this.recipe;
	}

	public void setIDs(ArrayList<Integer> recipe) {
		this.recipe = recipe;
	}

	public ArrayList<Integer> getDataValues() {
		return this.dataValues;
	}

	public void setDataValues(ArrayList<Integer> values) {
		this.dataValues = values;
	}

	public boolean isShapeless() {
		return this.shapeless;
	}

	public void setShapeless(boolean shapeless) {
		this.shapeless = shapeless;
	}

	public String getShapeless() {
		if (this.shapeless)
			return "Shapeless";
		else
			return "Shaped";
	}
}
