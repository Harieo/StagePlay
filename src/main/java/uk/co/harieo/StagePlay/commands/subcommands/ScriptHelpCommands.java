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
import uk.co.harieo.StagePlay.components.DefinedComponent;
import uk.co.harieo.StagePlay.scripts.StageAction;

public class ScriptHelpCommands {

	private static final String BULLET_POINT_ARROWS = ChatColor.DARK_GRAY + " " + FurCore.ARROWS + " ";

	@Group(@At("script"))
	@Command(aliases = {"", "help"},
			 desc = "For assistance on how to use StagePlay")
	public void help(@Sender Player sender, @Default("1") @Range(min = 1) int page) {
		sendTitle(sender, ChatColor.YELLOW + ChatColor.BOLD.toString() + "StagePlay " + ChatColor.GRAY + "by Harieo");

		// Description on how the plugin works
		sender.sendMessage(
				ChatColor.YELLOW + "StagePlay " + ChatColor.GRAY + "allows you to " + ChatColor.GREEN + "create "
						+ ChatColor.GRAY + "and " + ChatColor.AQUA + "script "
						+ ChatColor.GRAY + "entities in-game using commands.");
		sender.sendMessage(ChatColor.GRAY
				+ "Scripts are " + ChatColor.YELLOW + "saved to the StagePlay plugin folder " + ChatColor.GRAY + "and "
				+ ChatColor.LIGHT_PURPLE + "can be loaded on any server using a compatible version of the plugin!");
		sender.sendMessage("");

		sender.sendMessage(
				ChatColor.GOLD + "Stages " + ChatColor.GRAY + "can contain up to 1 of each " + ChatColor.WHITE
						+ "Action " + ChatColor.GRAY + "which may need an extra " + ChatColor.GREEN + "Component");
		sender.sendMessage(ChatColor.GRAY + "Use " + ChatColor.YELLOW + "/script actions " + ChatColor.GRAY + "or "
				+ ChatColor.YELLOW + "/script components " + ChatColor.GRAY + "for more info!");
		sender.sendMessage("");

		CommandDescription[] descriptions = CommandDescription.values();
		int valuesPerPage = 5;
		int pageMaximum = page * valuesPerPage;
		// Round up for last page to allow imperfect page lengths (e.g values.length % 5 != 0)
		int lastPage = descriptions.length / valuesPerPage + (descriptions.length % valuesPerPage == 0 ? 0 : 1);
		if (page > lastPage) {
			sender.sendMessage(ChatColor.RED + "There are no help pages past Page " + lastPage);
			return;
		}

		// Show a list of all the commands and what they do, 5 per page
		for (int i = pageMaximum - valuesPerPage; i < pageMaximum && i < descriptions.length; i++) {
			CommandDescription command = descriptions[i];
			sender.sendMessage(
					ChatColor.GREEN + "/script " + command.getSubCommand() + (command.hasArguments() ? " " + command
							.getArguments() : "")
							+ BULLET_POINT_ARROWS + ChatColor.GRAY + command
							.getDescription());
		}
		sender.sendMessage(ChatColor.GRAY + "Page " + page + "/" + lastPage + (page == lastPage ? ""
				: " - " + ChatColor.YELLOW + "/script help " + (page + 1)));
	}

	@Group(@At("script"))
	@Command(aliases = "actions",
			 desc = "Show a list of all applicable actions")
	public void showActions(@Sender Player sender) {
		sendTitle(sender, ChatColor.LIGHT_PURPLE + ChatColor.BOLD.toString() + "Actions");

		for (StageAction action : StageAction.values()) {
			sender.sendMessage(
					ChatColor.GREEN + action.name() + BULLET_POINT_ARROWS + ChatColor.GRAY + action.getUsage());
			sender.sendMessage(ChatColor.GRAY + "Requires the " + ChatColor.YELLOW + action.getComponent().getName()
					+ ChatColor.GRAY + " Component");
		}
		sender.sendMessage(
				ChatColor.GRAY + "Use " + ChatColor.LIGHT_PURPLE + "/script component <component> " + ChatColor.GRAY
						+ "to get more information on a component");
	}

	@Group(@At("script"))
	@Command(aliases = {"component", "components"},
			 desc = "Get more information on components")
	public void showComponent(@Sender Player sender, @Nullable DefinedComponent component) {
		if (component == null) {
			sendTitle(sender, ChatColor.GREEN + ChatColor.BOLD.toString() + "Action Components");
			sender.sendMessage(ChatColor.GRAY + "A " + ChatColor.YELLOW + "Component " + ChatColor.GRAY
					+ "is an " + ChatColor.GREEN + "extra piece of information " + ChatColor.GRAY
					+ "needed to execute an action");
			sender.sendMessage("");

			for (DefinedComponent definedComponent : DefinedComponent.values()) {
				sender.sendMessage(
						ChatColor.LIGHT_PURPLE + definedComponent.getName() + BULLET_POINT_ARROWS +
								ChatColor.GRAY + definedComponent.getDescription());
				if (definedComponent.doesUseLocation()) {
					sender.sendMessage(ChatColor.GRAY
							+ "This type of component uses your player position rather than being specified");
				} else {
					sender.sendMessage(formatComponentExample(definedComponent));
				}
			}
		} else {
			sender.sendMessage(ChatColor.YELLOW + component.getName() + BULLET_POINT_ARROWS + ChatColor.GRAY +
					component.getDescription());
			sender.sendMessage(formatComponentExample(component));
		}
	}

	private void sendTitle(Player player, String titleText) {
		player.sendMessage("");
		player.sendMessage(titleText);
		player.sendMessage("");
	}

	private String formatComponentExample(DefinedComponent component) {
		return ChatColor.GRAY + "An example of a " + ChatColor.LIGHT_PURPLE + component.getName() + " Component "
				+ ChatColor.GRAY + "would be " + ChatColor.YELLOW + component.getExample();
	}

	private enum CommandDescription {
		HELP("help", null, "Show a description of the plugin and its commands"),
		SHOW_ACTIONS("actions", null, "Show a list of possible actions for your entity"),
		SHOW_COMPONENTS("components", "[component]", "Shows a description of the components"),
		CREATE_SCRIPT("create", "<script-name> <entity-type> <entity-name>", "Create a new script"),
		ADD_ACTION("add", "<action> [component]", "Add an action to the current stage"),
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
