package juicydev.infiniblocks;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Vector;
import java.util.logging.Logger;

import juicydev.infiniblocks.commands.InfiniBlocksPluginCommand;
import juicydev.infiniblocks.listeners.BlockListener;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Furnace;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

@SuppressWarnings("deprecation")
public class InfiniBlocks extends JavaPlugin {

	public Vector<InfiniBlock> blockList = new Vector<InfiniBlock>();

	public static InfiniBlocks mainPlugin;
	public static String loggerPrefix = "[InfiniBlocks] ";
	public static Logger logger = Logger
			.getLogger("Minecraft.InfiniBlocks.JuicyDev");

	public void onDisable() {
		saveDatabase();

		log("Thank you for using InfiniBlocks by JuicyDev!");
	}

	public void onEnable() {
		PluginManager pm = getServer().getPluginManager();
		mainPlugin = this;

		try {
			Metrics metrics = new Metrics(this);
			metrics.start();
		} catch (IOException e) {
			log(e);
		}

		getServer().getScheduler().scheduleSyncRepeatingTask(mainPlugin,
				new Runnable() {
					@Override
					public void run() {
						Iterator<InfiniBlock> it = blockList.iterator();

						while (it.hasNext()) {
							InfiniBlock infblock = (InfiniBlock) it.next();
							Block bl = infblock.getLocation().getBlock();

							if (bl.getType().equals(Material.ANVIL)) {
								while (bl.getData() >= 4) {
									bl.setData((byte) (bl.getData() - 4), false);
								}
							}

							if ((bl.getState().getType()
									.equals(Material.BURNING_FURNACE))) {
								Furnace f = (Furnace) bl.getState();
								f.setBurnTime((short) (20 * 60));
							}
						}
					}
				}, 0L, (long) (20 * 5)); /* Fires every 5 seconds */

		pm.registerEvents(new BlockListener(this), this);

		getCommand("infiniblocks").setExecutor(
				new InfiniBlocksPluginCommand(this));

		getDataFolder().mkdirs();

		loadDatabase();

		log("Thank you for using InfiniBlocks by JuicyDev!");
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
		player.sendMessage(ChatColor.GREEN + "==========[" + ChatColor.BLUE
				+ ChatColor.BOLD + " InfiniBlocks List " + ChatColor.GREEN
				+ "]==========");

		Object[] array = blockList.toArray();
		for (int i = 0; i < array.length; i++) {
			InfiniBlock dispenser = (InfiniBlock) array[i];
			Block block = dispenser.getLocation().getBlock();

			int x = block.getX();
			int y = block.getY();
			int z = block.getZ();
			World world = block.getWorld();
			String blockType = block.getType().toString().toLowerCase();

			player.sendMessage(ChatColor.BLUE + String.valueOf(i + 1) + ".  "
					+ ChatColor.YELLOW + "Type:" + ChatColor.GREEN + blockType
					+ ChatColor.YELLOW + " World:" + ChatColor.GREEN
					+ world.getName() + ChatColor.YELLOW + " x:"
					+ ChatColor.GREEN + x + ChatColor.YELLOW + " y:"
					+ ChatColor.GREEN + y + ChatColor.YELLOW + " z:"
					+ ChatColor.GREEN + z + ChatColor.YELLOW + ".");
		}
	}

	public void list(CommandSender sender) {
		sender.sendMessage("");
		sender.sendMessage(ChatColor.GREEN + "==========[" + ChatColor.BLUE
				+ ChatColor.BOLD + " InfiniBlocks List " + ChatColor.GREEN
				+ "]==========");

		Object[] array = blockList.toArray();
		for (int i = 0; i < array.length; i++) {
			InfiniBlock dispenser = (InfiniBlock) array[i];
			Block block = dispenser.getLocation().getBlock();

			int x = block.getX();
			int y = block.getY();
			int z = block.getZ();
			World world = block.getWorld();
			String blockType = block.getType().toString().toLowerCase();

			sender.sendMessage(ChatColor.BLUE + String.valueOf(i + 1) + ".  "
					+ ChatColor.YELLOW + "Type:" + ChatColor.GREEN + blockType
					+ ChatColor.YELLOW + " World:" + ChatColor.GREEN
					+ world.getName() + ChatColor.YELLOW + " x:"
					+ ChatColor.GREEN + x + ChatColor.YELLOW + " y:"
					+ ChatColor.GREEN + y + ChatColor.YELLOW + " z:"
					+ ChatColor.GREEN + z + ChatColor.YELLOW + ".");
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
		if (new File(getDataFolder(), "blocks.dat").exists()) {
			Scanner scanner = null;
			try {
				scanner = new Scanner(new FileInputStream(new File(
						getDataFolder(), "blocks.dat")), "UTF-8");

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

						this.blockList.add(new InfiniBlock(new Location(world,
								x, y, z)));
					}
				}
				log("Loaded " + this.blockList.size()
						+ " infiniblocks from database.");
			} catch (Exception ex) {
				log("Cannot load blocks.dat!");
				ex.printStackTrace();
			} finally {
				if (scanner != null)
					scanner.close();
			}
		} else {
			log("There's no blocks.dat! Creating one on next save.");
		}

	}

	public void saveDatabase() {
		try {
			Writer out = new OutputStreamWriter(new FileOutputStream(new File(
					getDataFolder(), "blocks.dat")), "UTF-8");

			Iterator<InfiniBlock> it = this.blockList.iterator();

			while (it.hasNext()) {
				InfiniBlock dispenser = (InfiniBlock) it.next();
				Location pos = dispenser.getLocation();

				out.write(pos.getBlockX() + ";" + pos.getBlockY() + ";"
						+ pos.getBlockZ() + ";" + pos.getWorld().getName()
						+ ";" + "\n");
			}

			out.flush();
			out.close();

			getLogger().info(
					"Saved " + this.blockList.size()
							+ " infiniblocks to database.");
		} catch (Exception ex) {
			getLogger().severe("Cannot save blocks.dat!");
			ex.printStackTrace();
		}
	}
}
