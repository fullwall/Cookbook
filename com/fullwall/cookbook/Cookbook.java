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

	public final Listen l = new Listen(this);
	private static final String codename = "Smelter";
	public static Logger log = Logger.getLogger("Minecraft");
	public static ArrayList<ArrayList<Integer>> recipes = new ArrayList<ArrayList<Integer>>();
	public static ArrayList<ArrayList<Integer>> recipesData = new ArrayList<ArrayList<Integer>>();
	public static ArrayList<ArrayList<Integer>> shapelessRecipes = new ArrayList<ArrayList<Integer>>();
	public static ArrayList<ArrayList<Integer>> shapelessRecipesData = new ArrayList<ArrayList<Integer>>();
	public static ArrayList<Integer> furnaceRecipes = new ArrayList<Integer>();
	public static ArrayList<Integer[]> results = new ArrayList<Integer[]>();
	public static ArrayList<Integer[]> shapedResults = new ArrayList<Integer[]>();
	public static ArrayList<Integer[]> furnaceResults = new ArrayList<Integer[]>();
	public static ArrayList<Double> furnaceCooktimes = new ArrayList<Double>();
	public static CraftResults instance;
	public static int delay = 2;

	public void onEnable() {

		PluginManager pm = getServer().getPluginManager();
		pm.registerEvent(Event.Type.BLOCK_INTERACT, l, Priority.Normal, this);
		PluginDescriptionFile pdfFile = this.getDescription();
		Permission.initialize(getServer());
		populateRecipes();
		populateFurnaceRecipes();
		instance = CraftResults.getInstance();
		addFurnaceRecipe(furnaceRecipes);
		log.info("[" + pdfFile.getName() + "]: version ["
				+ pdfFile.getVersion() + "] (" + codename + ") loaded");

	}

	public void onDisable() {
		PluginDescriptionFile pdfFile = this.getDescription();
		log.info("[" + pdfFile.getName() + "]: version ["
				+ pdfFile.getVersion() + "] (" + codename + ") disabled");
	}

	public void populateRecipes() {
		try {
			File file = new File("plugins/Cookbook/Cookbook.recipes");
			Scanner scan = new Scanner(file);
			ArrayList<Integer> recipeToAdd = new ArrayList<Integer>();
			ArrayList<Integer> recipeInProgress = new ArrayList<Integer>();
			ArrayList<Integer> dataRecipeInProgress = new ArrayList<Integer>();
			ArrayList<Integer> dataRecipeToAdd = new ArrayList<Integer>();
			int i = 0;
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
					Integer[] temp = { Integer.parseInt(split[0]),
							Integer.parseInt(split[1]),
							Integer.parseInt(split[2]) };
					if (shapeless)
						shapedResults.add(temp);
					else
						results.add(temp);
				}
				if (recipeToAdd != null && recipeToAdd.size() >= 1 && i == 3) {
					if (shapeless)
						shapelessRecipes.add(recipeToAdd);
					else
						recipes.add(recipeToAdd);
					if (shapeless)
						shapelessRecipesData.add(dataRecipeToAdd);
					else
						recipesData.add(dataRecipeToAdd);
					dataRecipeToAdd = new ArrayList<Integer>();
					dataRecipeInProgress = new ArrayList<Integer>();
					recipeToAdd = new ArrayList<Integer>();
					recipeInProgress = new ArrayList<Integer>();
					shapeless = false;
					i = -1;
				}
				i += 1;
			}

		} catch (FileNotFoundException e) {
			log.log(Level.SEVERE, "[Cookbook]: Cannot find recipes file.");
		}
	}

	private void populateFurnaceRecipes() {
		try {
			File file = new File("plugins/Cookbook/Cookbook.furnacerecipes");
			Scanner scan = new Scanner(file);
			int i = 0;
			while (scan.hasNextLine()) {
				String line = scan.nextLine();
				line = line.trim();
				if (!shouldNotContinue(line))
					continue;
				if (i == 0) {
					String key = line.substring(0, line.length() - 1).trim();
					if (key.equals(""))
						continue;
					furnaceRecipes.add(Integer.valueOf(key));
				} else {
					String key = line.substring(0, line.length() - 1).trim();
					if (key.equals(""))
						continue;
					String[] split = key.split(":");
					Integer[] temp = { Integer.parseInt(split[0]),
							Integer.parseInt(split[1]) };
					furnaceResults.add(temp);
					Double cooktime = Double.parseDouble(split[2]);
					if (cooktime < -0.99999999D)
						cooktime = 0D;
					furnaceCooktimes.add(cooktime);
				}
				if (i == 1) {
					// next recipe
					i = -1;
				}
				i += 1;
			}

		} catch (FileNotFoundException e) {
			log.log(Level.SEVERE,
					"[Cookbook]: Cannot find furnace recipes file.");
		}
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
				populateRecipes();
				populateFurnaceRecipes();
				instance = CraftResults.getInstance();
				addFurnaceRecipe(furnaceRecipes);
				sender.sendMessage(ChatColor.GRAY + "Recipes reloaded.");
				return true;
			}
			if (trimmed.length == 1 && trimmed[0].equals("recipes")) {
				displayRecipes(sender);
				return true;
			}
			if (trimmed.length == 2 && trimmed[0].equals("recipe")) {
				displayRecipe(sender, trimmed[1]);
				return true;
			}
		}

		return false;
	}

	private void displayRecipe(CommandSender sender, String string) {
		ArrayList<Integer> localRecipes = recipes.get(0);
		if (localRecipes == null) {
			sender.sendMessage("That recipe does not exist.");
		}

		int localCount = 0;
		if (localRecipes.size() >= 1) {
			String row = "";
			int itemID = Cookbook.results.get(0)[0];
			int amount = Cookbook.results.get(0)[1];
			for (int i = 0; i < localRecipes.size(); i++) {
				int id = localRecipes.get(i);
				if (localCount != 2)
					row += ChatColor.YELLOW + "" + id + ChatColor.BLACK + ", ";
				if (localCount == 2) {
					row += ChatColor.YELLOW + "" + id + ".";
					sender.sendMessage(row);
					row = "";
					localCount = -1;
				}
				localCount += 1;
			}
			if (amount > 1)
				sender.sendMessage("Result: " + ChatColor.YELLOW + "" + amount
						+ " " + ChatColor.GREEN
						+ Material.matchMaterial("" + itemID) + "s.");
			else
				sender.sendMessage("Result: " + ChatColor.YELLOW + "1 "
						+ ChatColor.GREEN + Material.matchMaterial("" + itemID)
						+ ".");
		}
	}

	private void displayRecipes(CommandSender sender) {
		int count = 0;
		sender.sendMessage(ChatColor.GOLD + "Cookbook recipes");
		sender.sendMessage(ChatColor.AQUA + "-------------------");
		for (ArrayList<Integer> localRecipes : recipes) {
			int localCount = 0;
			if (localRecipes.size() >= 1) {
				String row = "";
				int itemID = Cookbook.results.get(count)[0];
				int amount = Cookbook.results.get(count)[1];
				for (int i = 0; i < localRecipes.size(); i++) {
					int id = localRecipes.get(i);
					if (localCount != 2)
						row += ChatColor.YELLOW + "" + id + ChatColor.BLACK
								+ ", ";
					if (localCount == 2) {
						row += ChatColor.YELLOW + "" + id + ".";
						sender.sendMessage(row);
						row = "";
						localCount = -1;
					}
					localCount += 1;
				}
				if (amount > 1)
					sender.sendMessage("Result: " + ChatColor.YELLOW + ""
							+ amount + " " + ChatColor.GREEN
							+ Material.matchMaterial("" + itemID) + "s.");
				else
					sender.sendMessage("Result: " + ChatColor.YELLOW + "1 "
							+ ChatColor.GREEN
							+ Material.matchMaterial("" + itemID) + ".");
			}
			count += 1;
		}
		sender.sendMessage(ChatColor.AQUA + "-------------------");
	}

	public void addFurnaceRecipe(ArrayList<Integer> myRecipes) {
		int count = 0;
		for (Integer id : myRecipes) {
			int itemID = Cookbook.furnaceResults.get(count)[0];
			int damage = Cookbook.furnaceResults.get(count)[1];

			ItemStack result = new ItemStack(itemID, 1, damage);
			FurnaceRecipes.a().a(id, result);

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