package uk.co.harieo.StagePlay.commands;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import app.ashcon.intake.Command;
import app.ashcon.intake.bukkit.parametric.annotation.Sender;
import app.ashcon.intake.group.At;
import app.ashcon.intake.group.Group;
import app.ashcon.intake.parametric.annotation.Text;
import java.io.FileNotFoundException;
import java.nio.file.FileAlreadyExistsException;
import java.util.*;
import javax.annotation.Nullable;
import uk.co.harieo.FurBridge.rank.Rank;
import uk.co.harieo.FurCore.ranks.RankCache;
import uk.co.harieo.StagePlay.components.DefinedComponents;
import uk.co.harieo.StagePlay.components.StageComponent;
import uk.co.harieo.StagePlay.components.types.*;
import uk.co.harieo.StagePlay.components.types.FaceDirectionComponent.Facing;
import uk.co.harieo.StagePlay.entities.StageableEntities;
import uk.co.harieo.StagePlay.scripts.StageActions;
import uk.co.harieo.StagePlay.scripts.StagedScript;
import uk.co.harieo.StagePlay.utils.Utils;

public class ScriptCommand {

	private static Map<UUID, StagedScript> scriptsInProgress = new HashMap<>();

	@Group(@At("script"))
	@Command(aliases = {"create", "new"},
			 desc = "Creates a new script",
			 usage = "[create/new]")
	public void createScript(@Sender Player sender, String scriptName, StageableEntities entityType,
			@Text String entityName) {
		if (!RankCache.getCachedInfo(sender).hasPermission(Rank.MODERATOR)) {
			sender.sendMessage(ChatColor.RED + "You must be a Moderator or above to use scripts");
			return;
		} else if (scriptsInProgress.containsKey(sender.getUniqueId())) {
			sender.sendMessage(ChatColor.RED
					+ "You cannot edit more than 1 script at a time, use /script cancel or /script commit");
			return;
		}

		scriptsInProgress.put(sender.getUniqueId(), new StagedScript(scriptName, entityType, entityName));
		sender.sendMessage(ChatColor.GREEN + "Your script has been created!");
	}

	@Group(@At("script"))
	@Command(aliases = {"name", "changename"},
			 desc = "Change the name of this script")
	public void changeScriptName(@Sender Player sender, String newName) {
		if (!scriptsInProgress.containsKey(sender.getUniqueId())) {
			sender.sendMessage(ChatColor.RED + "You are not editing a script, use /script create");
			return;
		}

		scriptsInProgress.get(sender.getUniqueId()).setScriptName(newName);
		sender.sendMessage(ChatColor.GREEN + "Your script name has been successfully changed!");
	}

	@Group(@At("script"))
	@Command(aliases = {"entity", "entitytype"},
			 desc = "Changes the type of entity that this script will load")
	public void changeEntityType(@Sender Player sender, StageableEntities newEntityType) {
		if (!scriptsInProgress.containsKey(sender.getUniqueId())) {
			sender.sendMessage(ChatColor.RED + "You are not editing a script, use /script create");
			return;
		}

		scriptsInProgress.get(sender.getUniqueId()).setEntityType(newEntityType);
		sender.sendMessage(ChatColor.GREEN + "The entity type has been successfully changed!");
	}

	@Group(@At("script"))
	@Command(aliases = {"entityname", "changeentityname"},
			 desc = "Changes the name of the entity")
	public void changeEntityName(@Sender Player sender, @Text String newName) {
		if (!scriptsInProgress.containsKey(sender.getUniqueId())) {
			sender.sendMessage(ChatColor.RED + "You are not editing a script, use /script create");
			return;
		}

		scriptsInProgress.get(sender.getUniqueId()).setEntityName(newName);
		sender.sendMessage(ChatColor.GREEN + "Your entity's name has been successfully changed!");
	}

	@Group(@At("script"))
	@Command(aliases = {"add", "action", "addaction"},
			 desc = "Add an action to the entity")
	public void addAction(@Sender Player sender, StageActions action, @Nullable @Text String argument) {
		if (!scriptsInProgress.containsKey(sender.getUniqueId())) {
			sender.sendMessage(ChatColor.RED + "You are not editing a script, use /script create");
			return;
		}

		DefinedComponents definedComponent = action.getComponent();
		StagedScript script = scriptsInProgress.get(sender.getUniqueId());
		Location location = sender.getLocation();

		StageComponent component;
		// If the component requires user input and there is no such input, we can't continue
		if (!definedComponent.doesUseLocation() && argument == null) {
			sender.sendMessage(
					ChatColor.RED + "The component for this action needs to be defined: /script add " + action.name()
							.toLowerCase() + " <component>");
			sender.sendMessage(ChatColor.YELLOW + definedComponent.getName() + ChatColor.GRAY + " - " + definedComponent
					.getDescription());
			return;
		} else if (definedComponent.equals(DefinedComponents.LOCATION)) {
			LocationComponent locationComponent = definedComponent.createComponent();
			locationComponent.setValue(location);
			component = locationComponent;
		} else if (definedComponent.equals(DefinedComponents.FACING)) {
			FaceDirectionComponent facingComponent = definedComponent.createComponent();
			facingComponent.setValue(new Facing(location.getYaw(), location.getPitch()));
			component = facingComponent;
		} else if (definedComponent.equals(DefinedComponents.DISTANCE)) {
			double distance;
			try {
				distance = Double.parseDouble(argument);
			} catch (NumberFormatException ignored) {
				sender.sendMessage(ChatColor.RED + "This distance is invalid (not a number): " + argument);
				return;
			}

			DoubleComponent doubleComponent = definedComponent.createComponent();
			doubleComponent.setValue(distance);
			component = doubleComponent;
		} else if (definedComponent.equals(DefinedComponents.SECONDS)) {
			int seconds;
			try {
				seconds = Integer.parseInt(argument);
			} catch (NumberFormatException ignored) {
				sender.sendMessage(ChatColor.RED + "This amount of seconds is invalid or not whole: " + argument);
				return;
			}

			IntegerComponent integerComponent = definedComponent.createComponent();
			integerComponent.setValue(seconds);
			component = integerComponent;
		} else if (definedComponent.equals(DefinedComponents.TEXT)) {
			StringComponent stringComponent = definedComponent.createComponent();
			stringComponent.setValue(argument);
			component = stringComponent;
		} else {
			sender.sendMessage(
					ChatColor.RED + "The component for that action is broken: Not recognised");
			sender.sendMessage(ChatColor.YELLOW + definedComponent.getName() + ChatColor.GRAY + " - " + definedComponent
					.getDescription());
			return;
		}

		script.addAction(action, component);
		sender.sendMessage(ChatColor.GREEN + "Added the " + action.name() + " action to your script at Stage "
				+ script.getCurrentStage());
	}

	@Group(@At("script"))
	@Command(aliases = {"next", "nextstage", "newstage", "addstage"},
			 desc = "Add a new stage to the script")
	public void newStage(@Sender Player sender) {
		if (!scriptsInProgress.containsKey(sender.getUniqueId())) {
			sender.sendMessage(ChatColor.RED + "You are not editing a script, use /script create");
			return;
		}

		StagedScript script = scriptsInProgress.get(sender.getUniqueId());

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
		if (!scriptsInProgress.containsKey(sender.getUniqueId())) {
			sender.sendMessage(ChatColor.RED + "You are not editing a script, use /script create");
			return;
		}

		StagedScript script = scriptsInProgress.get(sender.getUniqueId());

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
		if (!scriptsInProgress.containsKey(sender.getUniqueId())) {
			sender.sendMessage(ChatColor.RED + "You are not editing a script, use /script create");
			return;
		}

		StagedScript script = scriptsInProgress.get(sender.getUniqueId());
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
				scriptsInProgress.remove(sender.getUniqueId()); // The script can't be edited after committed
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

	@Group(@At("script"))
	@Command(aliases = {"list", "status"},
			 desc = "List the actions set for your script")
	public void listActions(@Sender Player sender) {
		if (!scriptsInProgress.containsKey(sender.getUniqueId())) {
			sender.sendMessage(ChatColor.RED + "You are not editing a script, use /script create");
			return;
		}

		StagedScript script = scriptsInProgress.get(sender.getUniqueId());
		Map<Integer, Map<StageActions, StageComponent>> stages = script.getAllActions();

		sender.sendMessage(ChatColor.GRAY + "Your Script: " + ChatColor.GREEN + script.getScriptName());
		sender.sendMessage(
				ChatColor.GRAY + "Entity: " + ChatColor.YELLOW + script.getEntityType().name() + ChatColor.GRAY
						+ " called " + ChatColor.GREEN + script.getEntityName());
		sender.sendMessage("");
		stages.forEach((stage, actions) -> {
			sender.sendMessage(ChatColor.LIGHT_PURPLE + "Stage " + stage);
			actions.forEach((action, component) -> {
				String componentValue;
				if (component instanceof LocationComponent) { // Locations are harder to read without conversion
					componentValue = Utils.convertLocationToString(((LocationComponent) component).getValue());
				} else { // Everything else has acceptable values as strings
					componentValue = component.getValue().toString();
				}

				sender.sendMessage(
						"     " + ChatColor.YELLOW + action.name() + ": " + ChatColor.GRAY + componentValue);
			});
		});
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
