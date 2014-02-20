package juicydev.infiniblocks.commands;

import java.util.Iterator;

import juicydev.infiniblocks.InfiniBlock;
import juicydev.infiniblocks.InfiniBlocks;
import juicydev.infiniblocks.Perms;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Furnace;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@SuppressWarnings("deprecation")
public class InfiniBlocksPluginCommand implements CommandExecutor {

	private final InfiniBlocks plugin;

	public InfiniBlocksPluginCommand(InfiniBlocks plugin) {
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

				if ((block.getType().equals(Material.DISPENSER))
						|| (block.getType().equals(Material.DROPPER))
						|| (block.getType().equals(Material.HOPPER))
						|| (block.getType().equals(Material.FURNACE)
								|| (block.getType()
										.equals(Material.BURNING_FURNACE)) || (block
									.getType().equals(Material.ANVIL)))) {
					Location loc = block.getLocation();

					Iterator<InfiniBlock> it = plugin.blockList.iterator();

					while (it.hasNext()) {
						InfiniBlock infblock = (InfiniBlock) it.next();
						Location pos = infblock.getLocation();

						if (loc.equals(pos)) {
							plugin.blockList.remove(infblock);
							plugin.saveDatabase();

							if ((block.getType().equals(Material.FURNACE))
									|| (block.getType()
											.equals(Material.BURNING_FURNACE))) {
								block.setType(Material.FURNACE);
								Furnace f = (Furnace) block.getState();
								f.setBurnTime((short) (20 * 0));
							}

							plugin.msg(player, "This "
									+ block.getType().toString().toLowerCase()
									+ " is no longer infinite.");
							plugin.log(player.getName()
									+ " removed an infinite "
									+ block.getType().toString().toLowerCase()
									+ " at x:" + pos.getBlockX() + " y:"
									+ pos.getBlockY() + " z:" + pos.getBlockZ()
									+ ".");

							return true;
						}
					}

					plugin.blockList.add(new InfiniBlock(loc));
					plugin.saveDatabase();

					if ((block.getType().equals(Material.FURNACE))
							|| (block.getType()
									.equals(Material.BURNING_FURNACE))) {
						block.setType(Material.BURNING_FURNACE);
						Furnace f = (Furnace) block.getState();
						f.setBurnTime((short) (20 * 60));
					}

					if (block.getType().equals(Material.ANVIL)) {
						while (block.getData() >= 4) {
							block.setData((byte) (block.getData() - 4), false);
						}
					}

					plugin.msg(player, "This "
							+ block.getType().toString().toLowerCase()
							+ " is now infinite.");
					plugin.log(player.getName() + " created an infinite "
							+ block.getType().toString().toLowerCase()
							+ " at x:" + loc.getBlockX() + " y:"
							+ loc.getBlockY() + " z:" + loc.getBlockZ() + ".");
				} else {
					plugin.err(player, "Please select a valid block.");
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

							Object[] array = plugin.blockList.toArray();
							if ((num >= 1) && (num <= array.length)) {
								if (array[num - 1].equals(null)) {
									plugin.err(
											player,
											"Please select a valid option. Use '/infiniblocks list' for a list of valid options.");
									return true;
								}
								InfiniBlock dispenser = (InfiniBlock) array[num - 1];
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
										"Please select a valid option. Use '/infiniblocks list' for a list of valid options.");
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
								plugin.blockList.clear();
								plugin.saveDatabase();
							} else {
								plugin.noPerm(player);
							}
						} else {
							try {
								int num = Integer.parseInt(args[1]);

								Object[] array = plugin.blockList.toArray();
								if ((num >= 1) && (num <= array.length)) {
									if (array[num - 1].equals(null)) {
										plugin.err(
												player,
												"Please select a valid option. Use '/infiniblocks list' for a list of valid options.");
										return true;
									}
									InfiniBlock infblock = (InfiniBlock) array[num - 1];
									Block block = infblock.getLocation()
											.getBlock();

									plugin.blockList.remove(infblock);
									plugin.saveDatabase();

									plugin.msg(player, "The "
											+ block.getType().toString()
													.toLowerCase()
											+ " is no longer infinite.");
								} else {
									plugin.err(
											player,
											"Please select a valid option. Use '/infiniblocks list' for a list of valid options.");
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
						plugin.blockList.clear();
						plugin.saveDatabase();
					} else {
						try {
							int num = Integer.parseInt(args[1]);

							Object[] array = plugin.blockList.toArray();
							if ((num >= 1) && (num <= array.length)) {
								if (array[num - 1].equals(null)) {
									plugin.err(
											sender,
											"Please select a valid option. Use '/infiniblocks list' for a list of valid options.");
									return true;
								}
								InfiniBlock infblock = (InfiniBlock) array[num - 1];
								Block block = infblock.getLocation().getBlock();

								plugin.blockList.remove(infblock);
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
										"Please select a valid option. Use '/infiniblocks list' for a list of valid options.");
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
