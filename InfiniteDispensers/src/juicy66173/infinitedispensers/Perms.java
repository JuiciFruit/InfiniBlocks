package juicy66173.infinitedispensers;

import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

public class Perms {
	public static final Permission USE = new Permission("infinitedispensers.use", "Allows the player to create and remove infinite dispensers, droppers and hoppers. Given to ops by default.", PermissionDefault.OP);
	public static final Permission OVERRIDE = new Permission("infinitedispensers.override", "Allows the player to remove registered infinite dispensers, droppers and hoppers by breaking them rather than unregistering by command. Not given to ops by default.", PermissionDefault.FALSE);
	public static final Permission LIST = new Permission("infinitedispensers.list", "Allows the player list to locations of infinite dispensers, droppers and hoppers.", PermissionDefault.OP);
	public static final Permission TP = new Permission("infinitedispensers.tp", "Allows the player to teleport to items on the list of infinite dispensers, droppers and hoppers.", PermissionDefault.OP);
	public static final Permission REMOVE = new Permission("infinitedispensers.remove", "Allows the player to remove items from the list  of infinite dispensers, droppers and hoppers.", PermissionDefault.OP);
	public static final Permission REMOVE_ALL = new Permission("infinitedispensers.remove.all", "Allows the player to remove all items on the list of infinite dispensers, droppers and hoppers.", PermissionDefault.FALSE);
}
