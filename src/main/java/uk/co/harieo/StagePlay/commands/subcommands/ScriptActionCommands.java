package uk.co.harieo.StagePlay.commands.subcommands;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import app.ashcon.intake.Command;
import app.ashcon.intake.bukkit.parametric.annotation.Sender;
import app.ashcon.intake.group.At;
import app.ashcon.intake.group.Group;
import app.ashcon.intake.parametric.annotation.Text;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.annotation.Nullable;
import uk.co.harieo.FurBridge.rank.Rank;
import uk.co.harieo.FurCore.ranks.RankCache;
import uk.co.harieo.StagePlay.commands.ScriptCommand;
import uk.co.harieo.StagePlay.components.DefinedComponent;
import uk.co.harieo.StagePlay.components.StageComponent;
import uk.co.harieo.StagePlay.components.types.*;
import uk.co.harieo.StagePlay.scripts.ScriptLoader;
import uk.co.harieo.StagePlay.scripts.StageAction;
import uk.co.harieo.StagePlay.scripts.StagedScript;
import uk.co.harieo.StagePlay.utils.Utils;

public class ScriptActionCommands {

	@Group(@At("script"))
	@Command(aliases = {"add", "action", "addaction"},
			 desc = "Add an action to the entity")
	public void addAction(@Sender Player sender, StageAction action, @Nullable @Text String argument) {
		if (!ScriptCommand.isEditingScript(sender)) {
			sender.sendMessage(ChatColor.RED + "You are not editing a script, use /script create");
			return;
		}

		DefinedComponent definedComponent = action.getComponent();
		StagedScript script = ScriptCommand.getScript(sender);
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
		} else if (definedComponent.equals(DefinedComponent.LOCATION)) {
			LocationComponent locationComponent = definedComponent.createComponent();
			locationComponent.setValue(location);
			component = locationComponent;
		} else if (definedComponent.equals(DefinedComponent.DISTANCE)) {
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
		} else if (definedComponent.equals(DefinedComponent.SECONDS)) {
			int seconds;
			try {
				seconds = Integer.parseInt(argument.trim());
				if (seconds < 1) {
					sender.sendMessage(ChatColor.RED + "You cannot specify 0 seconds as the action would be redundant");
					return;
				}
			} catch (NumberFormatException ignored) {
				sender.sendMessage(ChatColor.RED + "This amount of seconds is invalid or not whole: " + argument);
				return;
			}

			IntegerComponent integerComponent = definedComponent.createComponent();
			integerComponent.setValue(seconds);
			component = integerComponent;
		} else if (definedComponent.equals(DefinedComponent.TEXT)) {
			StringComponent stringComponent = definedComponent.createComponent();
			stringComponent.setValue(argument.trim());
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
	@Command(aliases = {"delete", "remove"},
			 desc = "Remove an action from the current stage")
	public void removeAction(@Sender Player sender, StageAction action) {
		if (!ScriptCommand.isEditingScript(sender)) {
			sender.sendMessage(ChatColor.RED
					+ "You are not editing a script, use /script create or /script status <loaded-script>");
			return;
		}

		StagedScript script = ScriptCommand.getScript(sender);
		Map<StageAction, StageComponent> actions = script.getCurrentActions();
		if (actions.containsKey(action)) {
			actions.remove(action);
			sender.sendMessage(
					ChatColor.GREEN + "Removed " + action.name() + " from Stage " + script.getCurrentStage());
		} else {
			sender.sendMessage(ChatColor.RED
					+ "This stage has no such action, to change the stage being edited: /script stage <stage>");
		}
	}

	@Group(@At("script"))
	@Command(aliases = {"list", "status"},
			 desc = "List the actions set for your script")
	public void listActions(@Sender Player sender, @Nullable String loadedScriptName) {
		// If they have provided a script name, they are searching for another script
		if (!ScriptCommand.isEditingScript(sender) && loadedScriptName == null) {
			sender.sendMessage(ChatColor.RED
					+ "You are not editing a script, use /script create or /script status <loaded-script>");
			return;
		} else if (!RankCache.getCachedInfo(sender).hasPermission(Rank.MODERATOR)) {
			sender.sendMessage(ChatColor.RED + "You must be a Moderator or above to use scripts");
			return;
		}

		StagedScript script;
		if (loadedScriptName == null) { // Expecting the player to be editing a script in-progress
			script = ScriptCommand.getScript(sender);
		} else if (ScriptLoader.isScriptLoaded(loadedScriptName)) { // Else they want to see a loaded script
			script = ScriptLoader.getScript(loadedScriptName);
		} else {
			sender.sendMessage(ChatColor.RED + "No completed scripts are loaded with the name " + loadedScriptName);
			return;
		}

		Map<Integer, LinkedHashMap<StageAction, StageComponent>> stages = script.getAllActions();

		sender.sendMessage(ChatColor.GRAY + "Your Script: " + ChatColor.GREEN + script.getScriptName());
		sender.sendMessage(
				ChatColor.GRAY + "Entity: " + ChatColor.YELLOW + script.getEntityType().name() + ChatColor.GRAY
						+ " called " + ChatColor.translateAlternateColorCodes('&', script.getEntityName()));
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

}
