package com.fullwall.cookbook;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.minecraft.server.FurnaceRecipes;
import net.minecraft.server.ItemStack;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Cookbook for Bukkit
 * 
 * @author fullwall
 */
public class Cookbook extends JavaPlugin {

	public final PlayerListen pl = new PlayerListen(this);
	// 1 more than the max data value attainable - marks an itemstack out for
	// being able to be any data value in a recipe.
	public static final int MAGIC_DATA = -1;
	private static final String codename = "Classy";
	public static boolean verifyRecipes = false;
	public static boolean displayClientCount = false;
	public static Logger log = Logger.getLogger("Minecraft");
	public static ArrayList<Recipe> recipeObjects = new ArrayList<Recipe>();
	public static ArrayList<FurnaceRecipe> furnaceRecipeObjects = new ArrayList<FurnaceRecipe>();

	public static CraftResults instance;
	public static int delay = 1;

	public void onLoad() {
	}

	public void onEnable() {
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvent(Event.Type.PLAYER_INTERACT, pl, Priority.Normal, this);
		pm.registerEvent(Event.Type.PLAYER_JOIN, pl, Priority.Normal, this);
		PluginDescriptionFile pdfFile = this.getDescription();
		Permission.initialize(getServer());
		populateRecipes();
		populateFurnaceRecipes();
		instance = CraftResults.getInstance();
		addFurnaceRecipe(furnaceRecipeObjects);
		log.info("[" + pdfFile.getName() + "]: version ["
				+ pdfFile.getVersion() + "] (" + codename + ") loaded");

	}

	public void onDisable() {
		PluginDescriptionFile pdfFile = this.getDescription();
		log.info("[" + pdfFile.getName() + "]: version ["
				+ pdfFile.getVersion() + "] (" + codename + ") disabled");
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String commandLabel, String[] args) {
		String[] trimmed = args;
		if (command.getName().equals("cookbook")) {
			if (trimmed.length == 1 && trimmed[0].equals("reload")) {
				if (sender instanceof Player
						&& !(Permission.generic((Player) sender,
								"cookbook.reload"))) {
					return true;
				}
				recipeObjects = new ArrayList<Recipe>();
				furnaceRecipeObjects = new ArrayList<FurnaceRecipe>();
				populateRecipes();
				populateFurnaceRecipes();
				instance = CraftResults.getInstance();
				addFurnaceRecipe(furnaceRecipeObjects);
				sender.sendMessage(ChatColor.GRAY + "Recipes reloaded.");
				return true;
			}
			if (trimmed.length == 1 && trimmed[0].equals("recipes")) {
				if (sender instanceof Player
						&& !(Permission.generic((Player) sender,
								"cookbook.display.allrecipes"))) {
					sender.sendMessage(ChatColor.RED
							+ "You don't have permission to use this command.");
					return true;
				}
				displayRecipes(sender);
				return true;
			}
			if (trimmed.length == 3 && trimmed[0].equals("recipe")) {
				if (sender instanceof Player
						&& !(Permission.generic((Player) sender,
								"cookbook.display.recipe"))) {
					sender.sendMessage(ChatColor.RED
							+ "You don't have permission to use this command.");
					return true;
				}
				displayRecipe(sender, trimmed[1], trimmed[2]);
				return true;
			}
			if (trimmed.length == 2 && trimmed[0].equals("recipe")) {
				if (sender instanceof Player
						&& !(Permission.generic((Player) sender,
								"cookbook.display.recipe"))) {
					sender.sendMessage(ChatColor.RED
							+ "You don't have permission to use this command.");
					return true;
				}
				displayRecipe(sender, trimmed[1], "shaped");
				return true;
			}
			if (trimmed.length == 1 && trimmed[0].equals("frecipes")) {
				if (sender instanceof Player
						&& !(Permission.generic((Player) sender,
								"cookbook.display.allfurnacerecipes"))) {
					sender.sendMessage(ChatColor.RED
							+ "You don't have permission to use this command.");
					return true;
				}
				displayFurnaceRecipes(sender);
				return true;
			}
			if (trimmed.length == 2 && trimmed[0].equals("frecipe")) {
				if (sender instanceof Player
						&& !(Permission.generic((Player) sender,
								"cookbook.display.furnacerecipe"))) {
					sender.sendMessage(ChatColor.RED
							+ "You don't have permission to use this command.");
					return true;
				}
				displayFurnaceRecipe(sender, trimmed[1]);
				return true;
			}
		}
		return false;
	}

	private void displayRecipe(CommandSender sender, String string,
			String shapeless) {
		int index = Integer.parseInt(string) - 1;
		if (index == -1 || index >= recipeObjects.size()) {
			sender.sendMessage(ChatColor.RED + "That recipe does not exist.");
			return;
		}
		Recipe recipe = recipeObjects.get(index);
		sender.sendMessage(ChatColor.GOLD + "Recipe #" + index);
		int localCount = 0;
		if (recipe.getIDs().size() >= 1) {
			String row = "";
			int itemID, amount, data;
			itemID = recipe.getResult().getTypeId();
			amount = recipe.getResult().getAmount();
			data = recipe.getData();

			for (int i = 0; i < recipe.getIDs().size(); i++) {
				int id = recipe.getIDs().get(i);
				int cdata = recipe.getDataValues().get(localCount);
				if (localCount != 2)
					row += ChatColor.YELLOW + "" + id + ":" + cdata + "  ";
				if (localCount == 2) {
					row += ChatColor.YELLOW + "" + id + ":" + cdata;
					sender.sendMessage(row);
					row = "";
					localCount = -1;
				}
				localCount += 1;
			}
			if (amount > 1)
				sender.sendMessage("Result: " + ChatColor.YELLOW + "" + amount
						+ " " + ChatColor.GREEN
						+ Material.matchMaterial("" + itemID)
						+ "s, data value " + data + ".");
			else
				sender.sendMessage("Result: " + ChatColor.YELLOW + "1 "
						+ ChatColor.GREEN + Material.matchMaterial("" + itemID)
						+ ", data value " + data + ".");
		}
	}

	private void displayRecipes(CommandSender sender) {
		int count = 0;
		sender.sendMessage(ChatColor.GOLD + "Cookbook recipes");
		sender.sendMessage(ChatColor.AQUA + "-------------------");
		for (Recipe recipe : recipeObjects) {
			sender.sendMessage(ChatColor.LIGHT_PURPLE + recipe.getShapeless()
					+ " recipe");
			int localCount = 0;
			if (recipe.getIDs().size() >= 1) {
				String row = "";
				int itemID, amount, data;
				itemID = recipe.getResult().getTypeId();
				amount = recipe.getResult().getAmount();
				data = recipe.getData();

				for (int i = 0; i < recipe.getIDs().size(); i++) {
					int id = recipe.getIDs().get(i);
					int cdata = recipe.getDataValues().get(localCount);
					if (localCount != 2)
						row += ChatColor.YELLOW + "" + id + ":" + cdata + "  ";
					if (localCount == 2) {
						row += ChatColor.YELLOW + "" + id + ":" + cdata;
						sender.sendMessage(row);
						row = "";
						localCount = -1;
					}
					localCount += 1;
				}
				if (amount > 1)
					sender.sendMessage("Result: " + ChatColor.YELLOW + ""
							+ amount + " " + ChatColor.GREEN
							+ Material.matchMaterial("" + itemID)
							+ "s, data value " + data + ".");
				else
					sender.sendMessage("Result: " + ChatColor.YELLOW + "1 "
							+ ChatColor.GREEN
							+ Material.matchMaterial("" + itemID)
							+ ", data value " + data + ".");
				sender.sendMessage("");
			}
			count += 1;
		}
		sender.sendMessage(ChatColor.AQUA + "-------------------");
	}

	private void displayFurnaceRecipe(CommandSender sender, String string) {
		int index = Integer.parseInt(string) - 1;

		if (index == -1 || index >= furnaceRecipeObjects.size()) {
			sender.sendMessage(ChatColor.RED + "That recipe does not exist.");
			return;
		}
		FurnaceRecipe recipe = furnaceRecipeObjects.get(index);

		int id = recipe.getIngredient();
		int itemID = recipe.getResult().getTypeId();
		int data = recipe.getData();
		double cooktime = recipe.getCooktime();
		sender.sendMessage(ChatColor.GOLD + "Furnace recipe #" + index);
		sender.sendMessage(ChatColor.GREEN + "Cook one " + ChatColor.YELLOW
				+ Material.matchMaterial("" + id) + ChatColor.GREEN
				+ ". The cooktime will be changed by " + ChatColor.YELLOW
				+ cooktime + ChatColor.GREEN + ".");
		sender.sendMessage(ChatColor.GREEN + "The result will be "
				+ ChatColor.YELLOW + "1 " + Material.matchMaterial("" + itemID)
				+ ChatColor.GREEN + ", data value " + ChatColor.YELLOW + data
				+ ChatColor.GREEN + ".");
	}

	private void displayFurnaceRecipes(CommandSender sender) {
		int count = 0;
		sender.sendMessage(ChatColor.GOLD + "Cookbook furnace recipes");
		sender.sendMessage(ChatColor.AQUA + "-------------------");
		for (FurnaceRecipe recipe : furnaceRecipeObjects) {
			int id = recipe.getIngredient();
			int itemID = recipe.getResult().getTypeId();
			int data = recipe.getData();
			double cooktime = recipe.getCooktime();

			sender.sendMessage(ChatColor.GREEN + "Cook one " + ChatColor.YELLOW
					+ Material.matchMaterial("" + id) + ChatColor.GREEN
					+ ". The cooktime will be changed by " + ChatColor.YELLOW
					+ cooktime + ChatColor.GREEN + ".");
			sender.sendMessage(ChatColor.GREEN + "The result will be "
					+ ChatColor.YELLOW + "1 "
					+ Material.matchMaterial("" + itemID) + ChatColor.GREEN
					+ ", data value " + ChatColor.YELLOW + data
					+ ChatColor.GREEN + ".");
			sender.sendMessage("");
			count += 1;
		}
		sender.sendMessage(ChatColor.AQUA + "-------------------");
	}

	public void populateRecipes() {
		int recipecount = 0;
		try {
			File file = new File("plugins/Cookbook/Cookbook.recipes");
			Scanner scan = new Scanner(file);
			ArrayList<Integer> recipeToAdd = new ArrayList<Integer>();
			ArrayList<Integer> recipeInProgress = new ArrayList<Integer>();
			ArrayList<Integer> dataRecipeInProgress = new ArrayList<Integer>();
			ArrayList<Integer> dataRecipeToAdd = new ArrayList<Integer>();
			org.bukkit.inventory.ItemStack result = null;
			int data = 0;
			int i = 0;
			boolean defaultShapeless = true;
			boolean shapeless = false;
			while (scan.hasNextLine()) {
				String line = scan.nextLine();
				line = line.trim();
				if (!shouldNotContinue(line))
					continue;
				if (line.contains("delay=")) {
					String sub = line.substring(line.indexOf("=") + 1,
							line.length());
					delay = Integer.parseInt(sub);
					if (delay < 0)
						delay = 2;
					continue;
				}
				if (line.contains("display-client-recipe-count=")) {
					String sub = line.substring(line.indexOf("=") + 1,
							line.length());
					displayClientCount = Boolean.parseBoolean(sub);
					continue;
				}
				if (line.contains("verify-client-recipes=")) {
					String sub = line.substring(line.indexOf("=") + 1,
							line.length());
					verifyRecipes = Boolean.parseBoolean(sub);
					continue;
				}
				if (line.contains("default=")) {
					String sub = line.substring(line.indexOf("=") + 1,
							line.length());
					if (sub.equals("shapeless"))
						defaultShapeless = true;
					continue;
				}
				if (line.contains("@")) {
					if (line.contains("Shapeless"))
						shapeless = true;
					else
						shapeless = false;
					continue;
				}
				if (i != 3) {
					String key = line.substring(0, line.length() - 1).trim();
					if (key.equals(""))
						continue;
					String[] splitkey = key.split(";");
					String[] value = splitkey[0].split(",");
					for (String s : value) {
						s = s.trim();
						recipeInProgress.add(Integer.parseInt(s));
					}
					for (String str : splitkey[1].split(",")) {
						str = str.trim();
						if (str.equals("X"))
							dataRecipeInProgress.add(MAGIC_DATA);
						else
							dataRecipeInProgress.add(Integer.parseInt(str));
					}
					if (i == 2) {
						recipeToAdd = recipeInProgress;
						dataRecipeToAdd = dataRecipeInProgress;
					}
				} else {
					String key = line.substring(0, line.length() - 1).trim();
					if (key.equals(""))
						continue;
					String[] split = key.split(":");
					ItemStack stack = new ItemStack(Integer.parseInt(split[0]),
							Integer.parseInt(split[1]),
							Integer.parseInt(split[2]));
					data = stack.damage;
					if (stack != null && stack.id != 0)
						result = new CraftItemStack(stack);
				}
				if (recipeToAdd != null && recipeToAdd.size() >= 1 && i == 3) {
					recipeObjects.add(new Recipe(recipeToAdd, dataRecipeToAdd,
							result, shapeless, data));
					result = null;
					dataRecipeToAdd = new ArrayList<Integer>();
					dataRecipeInProgress = new ArrayList<Integer>();
					recipeToAdd = new ArrayList<Integer>();
					recipeInProgress = new ArrayList<Integer>();
					data = 0;
					recipecount += 1;
					if (!defaultShapeless)
						shapeless = false;
					i = -1;
				}
				i += 1;
			}

		} catch (FileNotFoundException e) {
			log.info("[Cookbook]: Cannot find recipes file.");
		} catch (Exception ex) {
			log.info("[Cookbook]: Invalid workbench recipe. Recipe number "
					+ recipecount + " is incorrect.");
		}
	}

	private void populateFurnaceRecipes() {
		int recipecount = 0;
		try {
			File file = new File("plugins/Cookbook/Cookbook.furnacerecipes");
			Scanner scan = new Scanner(file);
			int i = 0;
			int count = 0;

			int ingredientID = 1;
			int resultID = 1;
			int resultData = 0;
			double cooktime = 1;

			while (scan.hasNextLine()) {
				String line = scan.nextLine();
				line = line.trim();
				if (!shouldNotContinue(line))
					continue;
				if (i == 0) {
					String key = line.substring(0, line.length() - 1).trim();
					if (key.equals(""))
						continue;
					ingredientID = (Integer.valueOf(key));
				} else {
					String key = line.substring(0, line.length() - 1).trim();
					if (key.equals(""))
						continue;
					String[] split = key.split(":");
					resultID = Integer.parseInt(split[0]);
					resultData = Integer.parseInt(split[1]);
					cooktime = Double.parseDouble(split[2]);
					if (cooktime < -0.99999999D)
						cooktime = 0D;
				}
				if (i == 1) {
					// next recipe
					recipecount += 1;
					org.bukkit.inventory.ItemStack result = null;
					if (resultID != 0) {
						ItemStack temp = new ItemStack(resultID, 1, resultData);
						if (temp != null)
							result = new CraftItemStack(temp);
					}
					furnaceRecipeObjects.add(new FurnaceRecipe(ingredientID,
							result, cooktime, resultData));
					ingredientID = 1;
					resultID = 1;
					resultData = 0;
					cooktime = 1;
					count += 1;
					i = -1;
				}
				i += 1;
			}

		} catch (FileNotFoundException e) {
			log.log(Level.SEVERE,
					"[Cookbook]: Cannot find furnace recipes file.");
		} catch (Exception ex) {
			log.info("[Cookbook]: Invalid furnace recipe. Recipe number "
					+ recipecount + " is incorrect.");
		}
	}

	public static void addFurnaceRecipe(ArrayList<FurnaceRecipe> recipes) {
		int count = 0;
		for (FurnaceRecipe fr : recipes) {
			ItemStack result = null;
			if (fr.getResult().getTypeId() != 0) {
				result = new ItemStack(fr.getResult().getTypeId(), 1,
						fr.getData());
			}
			FurnaceRecipes.a().a(fr.getIngredient(), result);
			count += 1;
		}
	}

	public boolean shouldNotContinue(String line) {
		if (line.length() == 0)
			return false;
		if (line.trim().charAt(0) == '#')
			return false;
		return true;
	}
}