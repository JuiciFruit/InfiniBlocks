package juicy66173.infinitedispensers;

import org.bukkit.Location;

public class InfiniteDispenser {
	protected final Location location;

	public InfiniteDispenser(Location location) {
		this.location = location;
	}

	public Location getLocation() {
		return this.location;
	}
}