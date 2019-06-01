package uk.co.harieo.StagePlay.commands.subcommands;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import app.ashcon.intake.Command;
import app.ashcon.intake.bukkit.parametric.annotation.Sender;
import app.ashcon.intake.group.At;
import app.ashcon.intake.group.Group;
import app.ashcon.intake.parametric.annotation.Text;
import java.io.FileNotFoundException;
import java.nio.file.FileAlreadyExistsException;
import uk.co.harieo.StagePlay.commands.ScriptCommand;
import uk.co.harieo.StagePlay.entities.StageableEntities;
import uk.co.harieo.StagePlay.scripts.StageActions;
import uk.co.harieo.StagePlay.scripts.StagedScript;

public class ScriptEditingCommands {

	@Group(@At("script"))
	@Command(aliases = {"name", "changename"},
			 desc = "Change the name of this script")
	public void changeScriptName(@Sender Player sender, String newName) {
		if (!ScriptCommand.isEditingScript(sender)) {
			sender.sendMessage(ChatColor.RED + "You are not editing a script, use /script create");
			return;
		}

		ScriptCommand.getScript(sender).setScriptName(newName);
		sender.sendMessage(ChatColor.GREEN + "Your script name has been successfully changed!");
	}

	@Group(@At("script"))
	@Command(aliases = {"entity", "entitytype"},
			 desc = "Changes the type of entity that this script will load")
	public void changeEntityType(@Sender Player sender, StageableEntities newEntityType) {
		if (!ScriptCommand.isEditingScript(sender)) {
			sender.sendMessage(ChatColor.RED + "You are not editing a script, use /script create");
			return;
		}

		ScriptCommand.getScript(sender).setEntityType(newEntityType);
		sender.sendMessage(ChatColor.GREEN + "The entity type has been successfully changed!");
	}

	@Group(@At("script"))
	@Command(aliases = {"entityname", "changeentityname"},
			 desc = "Changes the name of the entity")
	public void changeEntityName(@Sender Player sender, @Text String newName) {
		if (!ScriptCommand.isEditingScript(sender)) {
			sender.sendMessage(ChatColor.RED + "You are not editing a script, use /script create");
			return;
		}

		ScriptCommand.getScript(sender).setEntityName(newName);
		sender.sendMessage(ChatColor.GREEN + "Your entity's name has been successfully changed!");
	}

	@Group(@At("script"))
	@Command(aliases = {"next", "nextstage", "newstage", "addstage"},
			 desc = "Add a new stage to the script")
	public void newStage(@Sender Player sender) {
		if (!ScriptCommand.isEditingScript(sender)) {
			sender.sendMessage(ChatColor.RED + "You are not editing a script, use /script create");
			return;
		}

		StagedScript script = ScriptCommand.getScript(sender);

		if (script.getCurrentActions().isEmpty()) {
			sender.sendMessage(ChatColor.RED + "You cannot add a new stage when the current stage is empty");
			return;
		}

		script.addNewStage();
		sender.sendMessage(ChatColor.GREEN + "Added a new stage to the script!");
	}

	@Group(@At("script"))
	@Command(aliases = {"stage", "switchstage", "editstage"},
			 desc = "Edit a previous stage")
	public void switchStage(@Sender Player sender, int stage) {
		if (!ScriptCommand.isEditingScript(sender)) {
			sender.sendMessage(ChatColor.RED + "You are not editing a script, use /script create");
			return;
		}

		StagedScript script = ScriptCommand.getScript(sender);

		if (script.getAmountOfStages() < stage) {
			sender.sendMessage(ChatColor.RED + "There is not a Stage " + stage + " in the script!");
			return;
		}

		script.setEditingStage(stage);
		sender.sendMessage(ChatColor.GREEN + "You are now editing Stage " + stage);
	}

	@Group(@At("script"))
	@Command(aliases = {"commit", "finish"},
			 desc = "Finalizes the script to a file")
	public void commitScript(@Sender Player sender) {
		if (!ScriptCommand.isEditingScript(sender)) {
			sender.sendMessage(ChatColor.RED + "You are not editing a script, use /script create");
			return;
		}

		StagedScript script = ScriptCommand.getScript(sender);
		if (!script.getActionsForStage(1).containsKey(StageActions.START)) {
			sender.sendMessage(
					ChatColor.RED + "You must specify a starting point for your script, use /script add start");
			return;
		}

		script.validateStages(sender); // TODO: BUG TESTING CODE
		try {
			if (script.commit()) {
				sender.sendMessage(
						ChatColor.GREEN + "Your script has been saved to the plugin folder as " + script.getScriptName()
								+ ".json");
				ScriptCommand.finishedScript(sender); // The script can't be edited after committed
			} else {
				sender.sendMessage(ChatColor.RED + "An unexpected internal error occurred, please check console!");
			}
		} catch (FileAlreadyExistsException ignored) {
			sender.sendMessage(ChatColor.RED + "A script with this name is already saved into the plugin folder!");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			sender.sendMessage(
					ChatColor.RED
							+ "An internal error occurred: The plugin directory does not exist and cannot be created");
		}
	}

}
