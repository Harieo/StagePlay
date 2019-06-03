package uk.co.harieo.StagePlay.commands.subcommands;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import app.ashcon.intake.Command;
import app.ashcon.intake.bukkit.parametric.annotation.Sender;
import app.ashcon.intake.group.At;
import app.ashcon.intake.group.Group;
import app.ashcon.intake.parametric.annotation.Default;
import app.ashcon.intake.parametric.annotation.Range;
import javax.annotation.Nullable;
import uk.co.harieo.FurCore.FurCore;

public class ScriptHelpCommands {

	@Group(@At("script"))
	@Command(aliases = {"", "help"},
			 desc = "For assistance on how to use StagePlay")
	public void help(@Sender Player sender, @Default("1") @Range(min = 1) int page) {
		sender.sendMessage("");
		sender.sendMessage(ChatColor.YELLOW + ChatColor.BOLD.toString() + "StagePlay " + ChatColor.GRAY + "by Harieo");
		sender.sendMessage("");

		// Description on how the plugin works
		sender.sendMessage(
				ChatColor.YELLOW + "StagePlay " + ChatColor.GRAY + "allows you to " + ChatColor.GREEN + "create "
						+ ChatColor.GRAY + "and " + ChatColor.AQUA + "script "
						+ ChatColor.GRAY + "entities in-game using commands.");
		sender.sendMessage(ChatColor.GRAY
				+ "Scripts are " + ChatColor.YELLOW + "saved to the StagePlay plugin folder " + ChatColor.GRAY + "and "
				+ ChatColor.LIGHT_PURPLE + "can be loaded on any server using a compatible version of the plugin!");
		sender.sendMessage("");

		CommandDescription[] descriptions = CommandDescription.values();
		int pageMaximum = page * 5;
		// Round up for last page to allow imperfect page lengths (e.g values.length % 5 != 0)
		int lastPage = descriptions.length / 5 + (descriptions.length % 5 == 0 ? 0 : 1);
		if (page > lastPage) {
			sender.sendMessage(ChatColor.RED + "There are no help pages past Page " + lastPage);
			return;
		}

		// Show a list of all the commands and what they do
		for (int i = pageMaximum - 5; i < pageMaximum && i < descriptions.length; i++) {
			CommandDescription command = descriptions[i];
			sender.sendMessage(ChatColor.GREEN + "/script " + command.getSubCommand() + (command.hasArguments() ? " " + command.getArguments() : "")
					+ ChatColor.DARK_GRAY + " " + FurCore.ARROWS + " " + ChatColor.GRAY + command.getDescription());
		}
		sender.sendMessage(ChatColor.GRAY + "Page " + page + "/" + lastPage + (page == lastPage ? ""
				: " - " + ChatColor.YELLOW + "/script help " + (page + 1)));
	}

	private enum CommandDescription {
		HELP("help", null, "Show a description of the plugin and its command"),
		ADD_ACTION("add", "<action>", "Add an action to the current stage"),
		REMOVE_ACTION("remove", "<action>", "Remove an action from the current stage"),
		LIST_ACTIONS("list", null, "Get a run-down of your stages and their actions"),
		SCRIPT_NAME("name", "<new-name>", "Change the name of your current script"),
		ENTITY_NAME("entityname", "<new-name", "Change the name of the entity to be spawned"),
		CHANGE_ENTITY("entity", "<entity-type>", "Change the type of entity you wish to spawn"),
		NEW_STAGE("newstage", null, "Add a new stage to the current script"),
		CHANGE_STAGE("stage", "<stage>", "Switch the stage you are adding/removing actions to/from"),
		REMOVE_STAGE("removestage", null, "Remove the NEWEST stage in the script"),
		COMMIT_SCRIPT("commit", null, "Save your script to the plugin folder as a file"),
		VALIDATE_SCRIPT("validate", null, "Get a report of any errors/warnings from your current script"),
		LOAD_SCRIPT("load", "<script-name>", "Load a script from a file in the plugin folder"),
		EXECUTE_SCRIPT("execute", "<script-name>", "Run a script that has been loaded via /script load");

		private String subCommand;
		private String arguments;
		private String description;

		CommandDescription(String subCommand, @Nullable String arguments, String description) {
			this.subCommand = subCommand;
			this.arguments = arguments;
			this.description = description;
		}

		public String getSubCommand() {
			return subCommand;
		}

		@Nullable
		public String getArguments() {
			return arguments;
		}

		public String getDescription() {
			return description;
		}

		public boolean hasArguments() {
			return arguments != null;
		}

	}

}
