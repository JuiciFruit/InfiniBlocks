package juicydev.infiniblocks;

import org.bukkit.Location;

public class InfiniBlock {
	protected final Location location;

	public InfiniBlock(Location location) {
		this.location = location;
	}

	public Location getLocation() {
		return this.location;
	}
}