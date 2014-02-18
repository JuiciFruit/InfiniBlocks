package juicy66173.infinitedispensers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Vector;
import java.util.logging.Logger;

import juicy66173.infinitedispensers.commands.InfiniteDispenserPluginCommand;
import juicy66173.infinitedispensers.listeners.BlockListener;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class InfiniteDispensers extends JavaPlugin {

	public Vector<InfiniteDispenser> dispenserList = new Vector<InfiniteDispenser>();

	public static InfiniteDispensers mainPlugin;
	public static String loggerPrefix = "[InfiniteDispensers] ";
	public static Logger logger = Logger
			.getLogger("Minecraft.InfiniteDispensers.Juicy66173");

	public void onDisable() {
		saveDatabase();
		
		log("Thank you for using InfiniteDispensers by Juicy66173!");
	}

	public void onEnable() {
		PluginManager pm = getServer().getPluginManager();
		mainPlugin = this;

		pm.registerEvents(new BlockListener(this), this);

		getCommand("infinitedispenser").setExecutor(
				new InfiniteDispenserPluginCommand(this));

		getDataFolder().mkdirs();
		
		loadDatabase();
		
		log("Thank you for using InfiniteDispensers by Juicy66173!");
	}

	/* ===== Methods ===== */

	public void noPerm(Player player) {
		err(player, "You do not have permission to do this.");
	}
	
	public void usage(Player player, String label) {
		err(player, "Usage: /" + label);
		err(player, "Usage: /" + label + " " + "list");
		err(player, "Usage: /" + label + " " + "tp <number>");
		err(player, "Usage: /" + label + " " + "remove <number/all>");
	}
	
	public void usage(CommandSender sender, String label) {
		err(sender, "Usage: /" + label);
		err(sender, "Usage: /" + label + " " + "list");
		err(sender, "Usage: /" + label + " " + "tp <number>");
		err(sender, "Usage: /" + label + " " + "remove <number/all>");
	}
	
	public void list(Player player) {
		player.sendMessage("");
		player.sendMessage(ChatColor.GREEN + "==========["
				+ ChatColor.BLUE + ChatColor.BOLD
				+ " Infinite Dispensers List " + ChatColor.GREEN
				+ "]==========");

		Object[] array = dispenserList.toArray();
		for (int i = 0; i < array.length; i++) {
			InfiniteDispenser dispenser = (InfiniteDispenser) array[i];
			Block block = dispenser.getLocation().getBlock();

			int x = block.getX();
			int y = block.getY();
			int z = block.getZ();
			World world = block.getWorld();
			String blockType = block.getType().toString()
					.toLowerCase();

			player.sendMessage(ChatColor.BLUE
					+ String.valueOf(i + 1) + ".  "
					+ ChatColor.YELLOW + "Type:" + ChatColor.GREEN
					+ blockType + ChatColor.YELLOW + " World:"
					+ ChatColor.GREEN + world.getName()
					+ ChatColor.YELLOW + " x:" + ChatColor.GREEN
					+ x + ChatColor.YELLOW + " y:"
					+ ChatColor.GREEN + y + ChatColor.YELLOW
					+ " z:" + ChatColor.GREEN + z
					+ ChatColor.YELLOW + ".");
		}
	}
	
	public void list(CommandSender sender) {
		sender.sendMessage("");
		sender.sendMessage(ChatColor.GREEN + "==========["
				+ ChatColor.BLUE + ChatColor.BOLD
				+ " Infinite Dispensers List " + ChatColor.GREEN
				+ "]==========");

		Object[] array = dispenserList.toArray();
		for (int i = 0; i < array.length; i++) {
			InfiniteDispenser dispenser = (InfiniteDispenser) array[i];
			Block block = dispenser.getLocation().getBlock();

			int x = block.getX();
			int y = block.getY();
			int z = block.getZ();
			World world = block.getWorld();
			String blockType = block.getType().toString()
					.toLowerCase();

			sender.sendMessage(ChatColor.BLUE
					+ String.valueOf(i + 1) + ".  "
					+ ChatColor.YELLOW + "Type:" + ChatColor.GREEN
					+ blockType + ChatColor.YELLOW + " World:"
					+ ChatColor.GREEN + world.getName()
					+ ChatColor.YELLOW + " x:" + ChatColor.GREEN
					+ x + ChatColor.YELLOW + " y:"
					+ ChatColor.GREEN + y + ChatColor.YELLOW
					+ " z:" + ChatColor.GREEN + z
					+ ChatColor.YELLOW + ".");
		}
	}

	public void msg(Player player, String str) {
		player.sendMessage(ChatColor.YELLOW + ChatColor.BOLD.toString()
				+ loggerPrefix + ChatColor.YELLOW + str);
	}

	public void msg(CommandSender sender, String str) {
		sender.sendMessage(ChatColor.YELLOW + ChatColor.BOLD.toString()
				+ loggerPrefix + ChatColor.YELLOW + str);
	}

	public void err(Player player, String str) {
		player.sendMessage(ChatColor.RED + ChatColor.BOLD.toString()
				+ loggerPrefix + ChatColor.RED + str);
	}

	public void err(CommandSender sender, String str) {
		sender.sendMessage(ChatColor.RED + ChatColor.BOLD.toString()
				+ loggerPrefix + ChatColor.RED + str);
	}

	public void log(String text) {
		logger.info(loggerPrefix + text);
	}

	public void log(Throwable e) {
		logger.severe(loggerPrefix + e.toString());
		e.printStackTrace();
	}

	public void loadDatabase() {
		if (new File(getDataFolder(), "dispensers.dat").exists()) {
			Scanner scanner = null;
			try {
				scanner = new Scanner(new FileInputStream(new File(
						getDataFolder(), "dispensers.dat")), "UTF-8");

				while (scanner.hasNextLine()) {
					String line = scanner.nextLine().replace("\n", "")
							.replace("\r", "");

					if ((!line.equalsIgnoreCase(""))
							&& (!line.equalsIgnoreCase(" "))) {
						String[] lineEx = line.split(";");

						double x = Double.parseDouble(lineEx[0]);
						double y = Double.parseDouble(lineEx[1]);
						double z = Double.parseDouble(lineEx[2]);
						World world = getServer().getWorld(lineEx[3]);

						this.dispenserList.add(new InfiniteDispenser(
								new Location(world, x, y, z)));
					}
				}
				log("Loaded " + this.dispenserList.size()
						+ " infinitedispensers from database.");
			} catch (Exception ex) {
				log("Cannot load dispensers.dat!");
				ex.printStackTrace();
			} finally {
				if (scanner != null)
					scanner.close();
			}
		} else {
			log("There's no dispensers.dat! Creating one on next save.");
		}

	}

	public void saveDatabase() {
		try {
			Writer out = new OutputStreamWriter(new FileOutputStream(new File(
					getDataFolder(), "dispensers.dat")), "UTF-8");

			Iterator<InfiniteDispenser> it = this.dispenserList.iterator();

			while (it.hasNext()) {
				InfiniteDispenser dispenser = (InfiniteDispenser) it.next();
				Location pos = dispenser.getLocation();

				out.write(pos.getBlockX() + ";" + pos.getBlockY() + ";"
						+ pos.getBlockZ() + ";" + pos.getWorld().getName()
						+ ";" + "\n");
			}

			out.flush();
			out.close();

			getLogger().info(
					"Saved " + this.dispenserList.size()
							+ " dispensers to database.");
		} catch (Exception ex) {
			getLogger().severe("Cannot save dispensers.dat!");
			ex.printStackTrace();
		}
	}
}
