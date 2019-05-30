package uk.co.harieo.StagePlay.scripts;

import org.bukkit.entity.Player;

import com.google.gson.JsonObject;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.util.HashMap;
import java.util.Map;
import uk.co.harieo.StagePlay.StagePlay;
import uk.co.harieo.StagePlay.components.StageComponent;
import uk.co.harieo.StagePlay.utils.ReportResult;

public class StagedScript {

	// TODO this class
	private String name; // This name will identify the saved file, if any
	private int amountOfStages = 0; // Total of stages
	private int stage = 0; // Current stage to add actions to

	private JsonObject mainJson = new JsonObject();
	private JsonObject mainStagesJson = new JsonObject();
	// The JSONs which store actions and components for each stage
	private Map<Integer, JsonObject> stageJsons = new HashMap<>();
	// Stores action as [stage, [action, component]] of which there can be multiple actions per stage
	private Map<Integer, Map<StageActions, StageComponent>> stagesOfActions = new HashMap<>();

	/**
	 * Creates a script of actions and components that can be run by a {@link uk.co.harieo.StagePlay.entities.ScriptedEntity}
	 *
	 * @param scriptName to identify the script after it is finalized
	 */
	public StagedScript(String scriptName) {
		this.name = scriptName;
		mainJson.addProperty("name", scriptName); // Sets the script name as the static identifier

		addNewStage(); // Handle stage 1
	}

	public void addNewStage() {
		if (getCurrentActions().isEmpty()) { // This should be validated by the command
			throw new IllegalStateException("Cannot progress stage without actions");
		}

		stage = amountOfStages + 1; // The stage that is being edited may not be the latest so make sure not to duplicate
		amountOfStages++;
		stagesOfActions.putIfAbsent(stage, new HashMap<>());
		stageJsons.putIfAbsent(stage, new JsonObject());
	}

	public void addAction(StageActions action, StageComponent component) {
		getCurrentActions().put(action, component);
		component.addToJson(action, getStageJson()); // The component handles adding to the JSON with keys and values
	}

	public void validateStages(Player player) {
		ReportResult report = new ReportResult(name + " Script Report");
		for (int i = 1; i <= amountOfStages; i++) {
			if (stagesOfActions.containsKey(amountOfStages) || stageJsons.containsKey(amountOfStages)) {
				report.addSuccessMessage("The amount of recorded stages matches the amount that have been created");
			} else {
				report.addErrorMessage("The amount of recorded stages DOESN'T match the amount of created stages");
			}

			if (stagesOfActions.containsKey(i)) {
				report.addSuccessMessage("Stage " + i + " is recorded");
			} else {
				report.addErrorMessage("Stage " + i + " has NOT been recorded");
			}

			if (stagesOfActions.get(i).isEmpty()) {
				report.addErrorMessage("Stage " + i + " has no actions in it");
			} else {
				report.addSuccessMessage("Stage " + i + " has at least 1 action");
			}

			if (stageJsons.containsKey(i)) {
				report.addSuccessMessage("Stage " + i + " has a JSON record of its recorded actions");
			} else {
				report.addErrorMessage("Stage " + i + " does not have a record of its recorded actions");
			}
		}
		report.sendReport(player);
	}

	public boolean commit() throws FileNotFoundException, FileAlreadyExistsException {
		for (int i = 1; i <= amountOfStages; i++) {
			mainStagesJson.add(String.valueOf(i), stageJsons.get(i));
		}

		mainJson.add("stages", mainStagesJson);

		StagePlay plugin = StagePlay.getInstance();
		File pluginDirectory = plugin.getDataFolder();
		if (!pluginDirectory.exists()) {
			if (pluginDirectory.mkdir()) {
				plugin.getLogger().info("Created plugin folder for StagePlay");
			} else {
				throw new FileNotFoundException("StagePlay has no plugin folder and cannot create one");
			}
		}

		File jsonFile = new File(pluginDirectory.getPath() + "/" + name + ".json");
		if (jsonFile.exists()) {
			throw new FileAlreadyExistsException("Script file " + name + ".json already exists");
		}

		try (FileWriter writer = new FileWriter(jsonFile)) {
			writer.write(mainJson.toString());
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	public Map<StageActions, StageComponent> getCurrentActions() {
		return stagesOfActions.get(stage);
	}

	private JsonObject getStageJson() {
		return stageJsons.get(stage);
	}

}
