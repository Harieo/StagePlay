package uk.co.harieo.StagePlay.commands.subcommands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.entity.Player;

import app.ashcon.intake.Command;
import app.ashcon.intake.bukkit.parametric.annotation.Sender;
import app.ashcon.intake.group.At;
import app.ashcon.intake.group.Group;
import java.io.FileNotFoundException;
import net.minecraft.server.v1_12_R1.EntityVillager;
import uk.co.harieo.FurBridge.rank.Rank;
import uk.co.harieo.FurCore.ranks.RankCache;
import uk.co.harieo.StagePlay.entities.ScriptedVillager;
import uk.co.harieo.StagePlay.scripts.ScriptExecutor;
import uk.co.harieo.StagePlay.scripts.ScriptLoader;

public class ScriptExecutionCommands {

	@Group(@At("script"))
	@Command(aliases = "load",
			 desc = "Loads a script from the plugin folder")
	public void loadScript(@Sender Player sender, String scriptName) {
		if (!RankCache.getCachedInfo(sender).hasPermission(Rank.MODERATOR)) {
			sender.sendMessage(ChatColor.RED + "You must be a Moderator or above to use scripts");
			return;
		}

		try {
			if (ScriptLoader.loadScript(scriptName)) {
				sender.sendMessage(
						ChatColor.GREEN + "Your script was successfully loaded, execute it with /script execute "
								+ scriptName);
			} else {
				sender.sendMessage(ChatColor.RED
						+ "The script you provided was invalid and could not be loaded, was it made via an up to date version of this plugin?");
			}
		} catch (FileNotFoundException ignored) {
			sender.sendMessage(ChatColor.RED
					+ "No script with that name was found in the plugin folder: <script-name>.json is the standard format");
		}
	}

	@Group(@At("script"))
	@Command(aliases = {"start", "execute"},
			 desc = "Execute a script in your world")
	public void executeScript(@Sender Player sender, String scriptName) {
		if (!RankCache.getCachedInfo(sender).hasPermission(Rank.MODERATOR)) {
			sender.sendMessage(ChatColor.RED + "You must be a Moderator or above to use scripts");
		} else if (ScriptLoader.isScriptLoaded(scriptName)) {
			World world = sender.getWorld();
			ScriptExecutor.executeScript(world, ScriptLoader.getScript(scriptName));
			sender.sendMessage(
					ChatColor.GRAY + "Executing script " + ChatColor.YELLOW + scriptName + ChatColor.GRAY + " in world "
							+ ChatColor.GREEN + world.getName());
		} else {
			sender.sendMessage(
					ChatColor.RED + "No script with the name " + scriptName + " are loaded, use /script load "
							+ scriptName);
		}
	}

}
