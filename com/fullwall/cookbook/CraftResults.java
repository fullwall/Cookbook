package com.fullwall.cookbook;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.minecraft.server.Block;
import net.minecraft.server.CraftingRecipe;
import net.minecraft.server.InventoryCrafting;
import net.minecraft.server.Item;
import net.minecraft.server.ItemStack;
import net.minecraft.server.ShapedRecipes;
import net.minecraft.server.ShapelessRecipes;

public class CraftResults {
	private static CraftResults instance = new CraftResults();
	@SuppressWarnings("rawtypes")
	private List b = new ArrayList();

	public static CraftResults getInstance() {
		for (Recipe r : Cookbook.recipeObjects) {
			instance = new CraftResults();
			instance.addRecipe(r);
		}
		return instance;
	}

	@SuppressWarnings("unchecked")
	public void addRecipe(Recipe myRecipe) {
		if (!myRecipe.isShapeless()) {
			ItemStack[] stackArray = new ItemStack[9];
			for (int i = 0; i < myRecipe.getIDs().size(); ++i) {
				int id = myRecipe.getIDs().get(i);
				int data = myRecipe.getDataValues().get(i);
				if (id == 0)
					continue;
				stackArray[i] = new ItemStack(id, 1, data);
			}
			ItemStack recipeResult = null;
			if (myRecipe.getResult() != null) {
				if (myRecipe.getResult().getData() != null)
					recipeResult = new ItemStack(myRecipe.getResult()
							.getTypeId(), myRecipe.getResult().getAmount(),
							myRecipe.getResult().getData().getData());
				else
					recipeResult = new ItemStack(myRecipe.getResult()
							.getTypeId(), myRecipe.getResult().getAmount(), 0);
			}
			ShapedRecipes recipe = new ShapedRecipes(3, 3, stackArray,
					recipeResult);
			this.b.add(recipe);
		} else {
			ArrayList<ItemStack> adding = new ArrayList<ItemStack>();
			for (int j = 0; j < myRecipe.getIDs().size(); ++j) {
				int id = myRecipe.getIDs().get(j);
				int data = myRecipe.getDataValues().get(j);
				if (id == 0)
					continue;
				net.minecraft.server.ItemStack item = new ItemStack(id, 1, data);
				adding.add(item.j());
			}
			ItemStack recipeResult = null;
			if (myRecipe.getResult() != null) {
				if (myRecipe.getResult().getData() != null)
					recipeResult = new ItemStack(myRecipe.getResult()
							.getTypeId(), myRecipe.getResult().getAmount(),
							myRecipe.getResult().getData().getData());
				else
					recipeResult = new ItemStack(myRecipe.getResult()
							.getTypeId(), myRecipe.getResult().getAmount(), 0);
			}
			this.b.add(new ShapelessRecipes(recipeResult, adding));
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	void addShaped(ItemStack itemstack, Object... aobject) {
		String s = "";
		int i = 0;
		int j = 0;
		int k = 0;

		if (aobject[i] instanceof String[]) {
			String[] astring = (String[]) ((String[]) aobject[i++]);

			for (int l = 0; l < astring.length; ++l) {
				String s1 = astring[l];

				++k;
				j = s1.length();
				s = s + s1;
			}
		} else {
			while (aobject[i] instanceof String) {
				String s2 = (String) aobject[i++];

				++k;
				j = s2.length();
				s = s + s2;
			}
		}

		HashMap hashmap;

		for (hashmap = new HashMap(); i < aobject.length; i += 2) {
			Character character = (Character) aobject[i];
			ItemStack itemstack1 = null;

			if (aobject[i + 1] instanceof Item) {
				itemstack1 = new ItemStack((Item) aobject[i + 1]);
			} else if (aobject[i + 1] instanceof Block) {
				itemstack1 = new ItemStack((Block) aobject[i + 1], 1, -1);
			} else if (aobject[i + 1] instanceof ItemStack) {
				itemstack1 = (ItemStack) aobject[i + 1];
			}

			hashmap.put(character, itemstack1);
		}

		ItemStack[] aitemstack = new ItemStack[j * k];

		for (int i1 = 0; i1 < j * k; ++i1) {
			char c0 = s.charAt(i1);

			if (hashmap.containsKey(Character.valueOf(c0))) {
				aitemstack[i1] = ((ItemStack) hashmap
						.get(Character.valueOf(c0))).j();
			} else {
				aitemstack[i1] = null;
			}
		}

		this.b.add(new ShapedRecipes(j, k, aitemstack, itemstack));
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	void addShapeless(ItemStack itemstack, Object... aobject) {
		ArrayList arraylist = new ArrayList();
		Object[] aobject1 = aobject;
		int i = aobject.length;

		for (int j = 0; j < i; ++j) {
			Object object = aobject1[j];

			if (object instanceof ItemStack) {
				arraylist.add(((ItemStack) object).j());
			} else if (object instanceof Item) {
				arraylist.add(new ItemStack((Item) object));
			} else {
				if (!(object instanceof Block)) {
					throw new RuntimeException("Invalid shapeless recipe!");
				}
				arraylist.add(new ItemStack((Block) object));
			}
		}

		this.b.add(new ShapelessRecipes(itemstack, arraylist));
	}

	public ItemStack getResult(InventoryCrafting inventorycrafting) {
		for (int i = 0; i < this.b.size(); ++i) {
			CraftingRecipe craftingrecipe = (CraftingRecipe) this.b.get(i);
			if (craftingrecipe.a(inventorycrafting)) {
				return craftingrecipe.b(inventorycrafting);
			}
		}
		return null;
	}
}