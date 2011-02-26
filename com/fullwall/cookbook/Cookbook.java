package com.fullwall.cookbook;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

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
	private static final String codename = "Oven-Fresh";
	public static Logger log = Logger.getLogger("Minecraft");
	public static ArrayList<ArrayList<Integer>> recipes = new ArrayList<ArrayList<Integer>>();
	public static ArrayList<Integer[]> results = new ArrayList<Integer[]>();

	public void onEnable() {

		PluginManager pm = getServer().getPluginManager();
		pm.registerEvent(Event.Type.BLOCK_INTERACT, l, Priority.Normal, this);
		PluginDescriptionFile pdfFile = this.getDescription();
		Permission.initialize(getServer());
		populateRecipes();
		CraftResults.getInstance();
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

			int i = 0;

			while (scan.hasNextLine()) {
				String line = scan.nextLine();
				line = line.trim();

				if (!shouldNotContinue(line))
					continue;

				if (i != 3) {
					String key = line.substring(0, line.length() - 1).trim();
					if (key.equals(""))
						continue;
					String[] value = key.split(",");
					for (String s : value) {
						recipeInProgress.add(Integer.parseInt(s));
					}
					if (i == 2) {
						recipeToAdd = recipeInProgress;
					}
				} else {
					String key = line.substring(0, line.length() - 1).trim();
					if (key.equals(""))
						continue;
					String[] split = key.split(":");
					Integer[] temp = { Integer.parseInt(split[0]),
							Integer.parseInt(split[1]) };
					results.add(temp);
				}
				if (recipeToAdd != null && recipeToAdd.size() >= 1 && i == 3) {
					recipes.add(recipeToAdd);
					recipeToAdd = new ArrayList<Integer>();
					recipeInProgress = new ArrayList<Integer>();
					i = -1;
				}
				i += 1;
			}

		} catch (FileNotFoundException e) {
			log.log(Level.SEVERE, "[Cookbook]: Cannot read recipes file.");
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