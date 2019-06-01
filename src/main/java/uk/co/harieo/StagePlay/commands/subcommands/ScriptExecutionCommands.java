package uk.co.harieo.StagePlay.commands.subcommands;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import app.ashcon.intake.Command;
import app.ashcon.intake.bukkit.parametric.annotation.Sender;
import app.ashcon.intake.group.At;
import app.ashcon.intake.group.Group;
import java.io.FileNotFoundException;
import uk.co.harieo.StagePlay.scripts.ScriptLoader;

public class ScriptExecutionCommands {

	@Group(@At("script"))
	@Command(aliases = "load",
			 desc = "Loads a script from the plugin folder")
	public void loadScript(@Sender Player sender, String scriptName) {
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
	public void executeScript(@Sender Player sender, String scriptName) {
		// TODO this
	}

}
