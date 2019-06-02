package uk.co.harieo.StagePlay.entities;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.server.v1_12_R1.EntityInsentient;
import net.minecraft.server.v1_12_R1.PathfinderGoal;
import uk.co.harieo.FurCore.FurCore;

public class PathfinderGoalSpeak extends PathfinderGoal {

	private Location currentLocation;
	private String text;
	private String entityName;
	private List<Player> nearbyPlayers = new ArrayList<>(); // Prevents creating unnecessary amounts of lists
	private boolean isShouting;

	public PathfinderGoalSpeak(EntityInsentient entity, Location currentLocation, String text, boolean isShouting) {
		this.currentLocation = currentLocation;
		this.text = text;
		this.entityName = entity.getCustomName();
		this.isShouting = isShouting;
	}

	@Override
	public boolean a() {
		// No point talking if nobody is listening (within 15 blocks)
		updateNearbyPlayers();
		return isShouting || !nearbyPlayers.isEmpty();
	}

	@Override
	public void c() {
		if (isShouting) {
			Bukkit.getOnlinePlayers().forEach(this::sendMessage);
		} else {
			nearbyPlayers.forEach(this::sendMessage);
		}
	}

	/**
	 * Updates the {@link #nearbyPlayers} field with any players that are within 5 blocks of the current location
	 */
	private void updateNearbyPlayers() {
		nearbyPlayers.clear();
		for (Player player : Bukkit.getOnlinePlayers()) {
			if (currentLocation.distance(player.getLocation()) <= 15) {
				nearbyPlayers.add(player);
			}
		}
	}

	/**
	 * Send the message attached to this pathfinder to a specific player
	 *
	 * @param player to send the message to
	 */
	private void sendMessage(Player player) {
		player.sendMessage(
				ChatColor.YELLOW + entityName + ChatColor.DARK_GRAY + FurCore.ARROWS + " " + ChatColor.GRAY + text);
	}

}
