package com.fullwall.cookbook;

import java.util.ArrayList;

import org.bukkit.Material;

public class RecipeManager extends Cookbook {

	public static ArrayList<Recipe> getWorkbenchRecipesFromResultMaterial(
			Material check) {
		ArrayList<Recipe> recipes = new ArrayList<Recipe>();
		for (Recipe recipe : recipeObjects) {
			if (recipe.getResult().getType() == check)
				recipes.add(recipe);
		}
		return recipes;
	}

	public static ArrayList<FurnaceRecipe> getFurnaceRecipesFromResultMaterial(
			Material check) {
		ArrayList<FurnaceRecipe> recipes = new ArrayList<FurnaceRecipe>();
		for (FurnaceRecipe recipe : furnaceRecipeObjects) {
			if (recipe.getResult().getType() == check)
				recipes.add(recipe);
		}
		return recipes;
	}

	public static ArrayList<FurnaceRecipe> getFurnaceRecipesFromCooktime(
			int cooktime) {
		ArrayList<FurnaceRecipe> recipes = new ArrayList<FurnaceRecipe>();
		for (FurnaceRecipe recipe : furnaceRecipeObjects) {
			if (recipe.getCooktime() == cooktime)
				recipes.add(recipe);
		}
		return recipes;
	}

	public static ArrayList<FurnaceRecipe> getFurnaceRecipesFromIngredient(
			int ingredient) {
		ArrayList<FurnaceRecipe> recipes = new ArrayList<FurnaceRecipe>();
		for (FurnaceRecipe recipe : furnaceRecipeObjects) {
			if (recipe.getIngredient() == ingredient)
				recipes.add(recipe);
		}
		return recipes;
	}

	public static void addRecipe(Recipe recipe) {
		CraftResults.getInstance().addRecipe(recipe);
	}

	public static void addFurnaceRecipe(FurnaceRecipe recipe) {
		addFurnaceRecipe(recipe);
	}

	public static ArrayList<FurnaceRecipe> getAllFurnaceRecipes() {
		return furnaceRecipeObjects;
	}

	public static ArrayList<Recipe> getAllWorkbenchRecipes() {
		return recipeObjects;
	}

	public static void setWorkbenchRecipe(int index, Recipe recipe) {
		recipeObjects.set(index, recipe);
		instance = CraftResults.getInstance();
	}

	public static void setFurnaceRecipe(int index, FurnaceRecipe recipe) {
		furnaceRecipeObjects.set(index, recipe);
		addFurnaceRecipe(furnaceRecipeObjects);
	}

	public static boolean containsIngredient(int ingredientID) {
		for (FurnaceRecipe recipe : furnaceRecipeObjects) {
			if (recipe.getIngredient() == ingredientID)
				return true;
		}
		return false;
	}

	public static double getCooktimeFromIngredient(int ingredientID) {
		for (FurnaceRecipe recipe : furnaceRecipeObjects) {
			if (recipe.getIngredient() == ingredientID)
				return recipe.getCooktime();
		}
		return 1D;
	}
}
