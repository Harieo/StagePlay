package uk.co.harieo.StagePlay.commands;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import app.ashcon.intake.Command;
import app.ashcon.intake.bukkit.parametric.annotation.Sender;
import app.ashcon.intake.group.At;
import app.ashcon.intake.group.Group;
import app.ashcon.intake.parametric.annotation.Text;
import java.util.*;
import uk.co.harieo.FurBridge.rank.Rank;
import uk.co.harieo.FurCore.ranks.RankCache;
import uk.co.harieo.StagePlay.entities.StageableEntities;
import uk.co.harieo.StagePlay.scripts.StagedScript;

public class ScriptCommand {

	private static Map<UUID, StagedScript> scriptsInProgress = new HashMap<>();

	/**
	 * Retrieve the script that a player is currently editing, if any
	 *
	 * @param player who is editing the script to be retrieved
	 * @return the script or null if no script exists
	 */
	public static StagedScript getScript(Player player) {
		return scriptsInProgress.get(player.getUniqueId());
	}

	/**
	 * Checks whether the player currently has a script which they are editing
	 *
	 * @param player to check if they have a script in progress
	 * @return whether they are editing a script
	 */
	public static boolean isEditingScript(Player player) {
		return scriptsInProgress.containsKey(player.getUniqueId());
	}

	/**
	 * Removes a script from the in-progress cache on the assumption that it was committed as a file
	 *
	 * @param player who has finished their script
	 */
	public static void finishedScript(Player player) {
		scriptsInProgress.remove(player.getUniqueId());
	}

	@Group(@At("script"))
	@Command(aliases = {"create", "new"},
			 desc = "Creates a new script",
			 usage = "[create/new]")
	public void createScript(@Sender Player sender, String scriptName, StageableEntities entityType,
			@Text String entityName) {
		if (!RankCache.getCachedInfo(sender).hasPermission(Rank.MODERATOR)) {
			sender.sendMessage(ChatColor.RED + "You must be a Moderator or above to use scripts");
			return;
		} else if (isEditingScript(sender)) {
			sender.sendMessage(ChatColor.RED
					+ "You cannot edit more than 1 script at a time, use /script cancel or /script commit");
			return;
		}

		scriptsInProgress.put(sender.getUniqueId(), new StagedScript(scriptName, entityType, entityName));
		sender.sendMessage(ChatColor.GREEN + "Your script has been created!");
	}

	@Group(@At("script"))
	@Command(aliases = {"cancel", "abort"},
			 desc = "Cancel your current script, deleting it permanently")
	public void cancelScript(@Sender Player sender) {
		if (scriptsInProgress.containsKey(sender.getUniqueId())) {
			scriptsInProgress.remove(sender.getUniqueId());
			sender.sendMessage(ChatColor.RED + "Your current script has been deleted");
		}
	}

}
