package juicy66173.infinitedispensers.listeners;

import java.util.Iterator;

import juicy66173.infinitedispensers.InfiniteDispenser;
import juicy66173.infinitedispensers.InfiniteDispensers;
import juicy66173.infinitedispensers.Perms;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Dispenser;
import org.bukkit.block.Dropper;
import org.bukkit.block.Hopper;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class BlockListener implements Listener {

	private final InfiniteDispensers plugin;

	public BlockListener(InfiniteDispensers plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onDispense(BlockDispenseEvent event) {
		if (event.isCancelled())
			return;

		Iterator<InfiniteDispenser> it = plugin.dispenserList.iterator();

		while (it.hasNext()) {
			InfiniteDispenser dispenser = (InfiniteDispenser) it.next();
			Location pos = dispenser.getLocation();

			if (pos.equals(event.getBlock().getLocation())) {
				if (event.getBlock().getType().equals(Material.DISPENSER)) {
					Dispenser dispenserBlock = (Dispenser) event.getBlock()
							.getState();

					ItemStack newItemStack = event.getItem().clone();
					dispenserBlock.getInventory().addItem(
							new ItemStack[] { newItemStack });
				} else if (event.getBlock().getType().equals(Material.DROPPER)) {
					Dropper dispenserBlock = (Dropper) event.getBlock()
							.getState();

					ItemStack newItemStack = event.getItem().clone();
					dispenserBlock.getInventory().addItem(
							new ItemStack[] { newItemStack });
				}

				return;
			}
		}
	}

	@EventHandler
	public void onHopperMoveItem(InventoryMoveItemEvent event) {
		if (event.isCancelled())
			return;

		Iterator<InfiniteDispenser> it = plugin.dispenserList.iterator();

		while (it.hasNext()) {
			InfiniteDispenser dispenser = (InfiniteDispenser) it.next();
			Location pos = dispenser.getLocation();

			Inventory src = event.getSource();
			Inventory dest = event.getDestination();
			Inventory init = event.getInitiator();
			ItemStack itemStack = event.getItem();

			Location loc = ((Hopper) init.getHolder()).getLocation();

			if (init.equals(src)) {
				if (pos.equals((loc))) {
					if (loc.getBlock().getType().equals(Material.HOPPER)) {
						ItemStack newItemStack = itemStack.clone();
						src.addItem(new ItemStack[] { newItemStack });
					}
				}
			}

			Bukkit.broadcastMessage(ChatColor.YELLOW + "Source Inventory: "
					+ ChatColor.GREEN + src.getName());
			Bukkit.broadcastMessage(ChatColor.YELLOW
					+ "Destination Inventory: " + ChatColor.GREEN
					+ dest.getName());
			Bukkit.broadcastMessage(ChatColor.YELLOW + "Initiator Inventory: "
					+ ChatColor.GREEN + init.getName());
			Bukkit.broadcastMessage(ChatColor.YELLOW + "Item: "
					+ ChatColor.GREEN + itemStack.getType().toString());
		}

		return;
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		if (event.isCancelled())
			return;

		Block block = event.getBlock();
		Location loc = block.getLocation();

		Player player = event.getPlayer();

		Iterator<InfiniteDispenser> it = plugin.dispenserList.iterator();

		while (it.hasNext()) {
			InfiniteDispenser dispenser = (InfiniteDispenser) it.next();
			Location pos = dispenser.getLocation();

			if (loc.equals(pos)) {
				if (player.hasPermission(Perms.OVERRIDE)) {
					plugin.dispenserList.remove(dispenser);
					plugin.saveDatabase();
					plugin.err(player, "You destroyed an infinite "
							+ block.getType().toString().toLowerCase()
							+ ". It has been unregistered.");
					plugin.log(player.getName() + " removed an infinite "
							+ block.getType().toString().toLowerCase()
							+ " at x:" + pos.getBlockX() + " y:"
							+ pos.getBlockY() + " z:" + pos.getBlockZ() + ".");
					return;
				} else {
					plugin.err(player,
							"You do not have permission to destroy infinite "
									+ block.getType().toString().toLowerCase()
									+ "s.");
					plugin.err(player, "Please unregister it and try again.");
					event.setCancelled(true);
					return;
				}
			}
		}

		return;
	}
}
