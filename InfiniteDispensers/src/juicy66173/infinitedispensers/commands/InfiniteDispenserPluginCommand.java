package juicy66173.infinitedispensers.commands;

import java.util.Iterator;

import juicy66173.infinitedispensers.InfiniteDispenser;
import juicy66173.infinitedispensers.InfiniteDispensers;
import juicy66173.infinitedispensers.Perms;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@SuppressWarnings("deprecation")
public class InfiniteDispenserPluginCommand implements CommandExecutor {

	private final InfiniteDispensers plugin;

	public InfiniteDispenserPluginCommand(InfiniteDispensers plugin) {
		this.plugin = plugin;
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label,
			String[] args) {

		if (sender instanceof Player) {
			Player player = (Player) sender;

			if (!player.hasPermission(Perms.USE)) {
				plugin.noPerm(player);
				return true;
			}

			if (args.length == 0) {
				Block block = player.getTargetBlock(null, 5);

				if ((block.getType() == Material.DISPENSER)
						|| (block.getType() == Material.DROPPER)
						|| (block.getType() == Material.HOPPER)) {
					Location loc = block.getLocation();

					Iterator<InfiniteDispenser> it = plugin.dispenserList
							.iterator();

					while (it.hasNext()) {
						InfiniteDispenser dispenser = (InfiniteDispenser) it
								.next();
						Location pos = dispenser.getLocation();

						if (loc.equals(pos)) {
							plugin.dispenserList.remove(dispenser);
							plugin.saveDatabase();

							plugin.msg(
									player,
									"This "
											+ block.getType().toString()
													.toLowerCase()
											+ " is no longer infinite. It can now run out of contents.");
							plugin.log(player.getName()
									+ " removed an infinite "
									+ block.getType().toString().toLowerCase()
									+ " at x:" + pos.getBlockX() + " y:"
									+ pos.getBlockY() + " z:" + pos.getBlockZ()
									+ ".");

							return true;
						}
					}

					plugin.dispenserList.add(new InfiniteDispenser(loc));
					plugin.saveDatabase();

					plugin.msg(player, "This "
							+ block.getType().toString().toLowerCase()
							+ " is now infinite.");
					plugin.log(player.getName() + " created an infinite "
							+ block.getType().toString().toLowerCase()
							+ " at x:" + loc.getBlockX() + " y:"
							+ loc.getBlockY() + " z:" + loc.getBlockZ() + ".");
				} else {
					plugin.err(player,
							"Please select a dispenser, dropper or hopper");
				}
			} else if (args.length == 1) {
				if (args[0].equalsIgnoreCase("list")) {
					if (player.hasPermission(Perms.LIST)) {
						plugin.list(player);
					} else {
						plugin.noPerm(player);
					}
				} else {
					plugin.usage(player, label);
				}
			} else if (args.length == 2) {
				if (args[0].equalsIgnoreCase("tp")) {
					if (player.hasPermission(Perms.TP)) {
						try {
							int num = Integer.parseInt(args[1]);

							Object[] array = plugin.dispenserList.toArray();
							if ((num >= 1) && (num <= array.length)) {
								if (array[num - 1].equals(null)) {
									plugin.err(
											player,
											"Please select a valid option. Use '/infinitedispensers list' for a list of valid options.");
									return true;
								}
								InfiniteDispenser dispenser = (InfiniteDispenser) array[num - 1];
								Block block = dispenser.getLocation()
										.getBlock();

								int x = block.getX();
								int y = block.getY();
								int z = block.getZ();
								World world = block.getWorld();
								String blockType = block.getType().toString()
										.toLowerCase();

								plugin.msg(
										player,
										"Teleporting to '" + blockType
												+ "' in world '"
												+ world.getName() + "' at '"
												+ x + "' '" + y + "' '" + z
												+ "'.");

								double x1 = x + 0.5;
								double y1 = y + 1;
								double z1 = z + 0.5;
								Location loc = new Location(world, x1, y1, z1);

								player.teleport(loc);
							} else {
								plugin.err(
										player,
										"Please select a valid option. Use '/infinitedispensers list' for a list of valid options.");
							}
						} catch (NumberFormatException e) {
							plugin.err(player, "That is not a valid integer.");
						}
					} else {
						plugin.noPerm(player);
					}
				} else if (args[0].equalsIgnoreCase("remove")) {
					if (player.hasPermission(Perms.REMOVE)) {
						if (args[1].equalsIgnoreCase("all")) {
							if (player.hasPermission(Perms.REMOVE_ALL)) {
								plugin.dispenserList.clear();
								plugin.saveDatabase();
							} else {
								plugin.noPerm(player);
							}
						} else {
							try {
								int num = Integer.parseInt(args[1]);

								Object[] array = plugin.dispenserList.toArray();
								if ((num >= 1) && (num <= array.length)) {
									if (array[num - 1].equals(null)) {
										plugin.err(
												player,
												"Please select a valid option. Use '/infinitedispensers list' for a list of valid options.");
										return true;
									}
									InfiniteDispenser dispenser = (InfiniteDispenser) array[num - 1];
									Block block = dispenser.getLocation()
											.getBlock();

									plugin.dispenserList.remove(dispenser);
									plugin.saveDatabase();

									plugin.msg(
											player,
											"The "
													+ block.getType()
															.toString()
															.toLowerCase()
													+ " is no longer infinite.  It can now run out of contents.");
								} else {
									plugin.err(
											player,
											"Please select a valid option. Use '/infinitedispensers list' for a list of valid options.");
								}
							} catch (NumberFormatException e) {
								plugin.err(player,
										"That is not a valid integer.");
							}
						}
					} else {
						plugin.noPerm(player);
					}
				} else {
					plugin.usage(player, label);
				}
			} else {
				plugin.usage(player, label);
			}
		} else {
			if (args.length == 1) {
				if (args[0].equalsIgnoreCase("list")) {
					plugin.list(sender);
				} else {
					plugin.usage(sender, label);
				}
			} else if (args.length == 2) {
				if (args[0].equalsIgnoreCase("remove")) {
					if (args[1].equalsIgnoreCase("all")) {
						plugin.dispenserList.clear();
						plugin.saveDatabase();
					} else {
						try {
							int num = Integer.parseInt(args[1]);

							Object[] array = plugin.dispenserList.toArray();
							if ((num >= 1) && (num <= array.length)) {
								if (array[num - 1].equals(null)) {
									plugin.err(
											sender,
											"Please select a valid option. Use '/infinitedispensers list' for a list of valid options.");
									return true;
								}
								InfiniteDispenser dispenser = (InfiniteDispenser) array[num - 1];
								Block block = dispenser.getLocation()
										.getBlock();

								plugin.dispenserList.remove(dispenser);
								plugin.saveDatabase();

								plugin.msg(
										sender,
										"The "
												+ block.getType().toString()
														.toLowerCase()
												+ " is no longer infinite.  It can now run out of contents.");
							} else {
								plugin.err(
										sender,
										"Please select a valid option. Use '/infinitedispensers list' for a list of valid options.");
							}
						} catch (NumberFormatException e) {
							plugin.err(sender, "That is not a valid integer.");
						}
					}
				} else {
					plugin.usage(sender, label);
				}
			} else {
				plugin.usage(sender, label);
			}
		}

		return true;
	}
}
