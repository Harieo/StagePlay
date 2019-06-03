package uk.co.harieo.StagePlay.scripts;

import org.bukkit.Location;

import com.google.gson.JsonObject;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import uk.co.harieo.StagePlay.StagePlay;
import uk.co.harieo.StagePlay.components.StageComponent;
import uk.co.harieo.StagePlay.components.types.LocationComponent;
import uk.co.harieo.StagePlay.entities.StageableEntity;
import uk.co.harieo.StagePlay.utils.ReportResult;
import uk.co.harieo.StagePlay.utils.ReportResult.ResultType;

public class StagedScript {

	private String scriptName; // This name will identify the saved file, if any
	private String entityName; // The name of the entity when spawned
	private StageableEntity entityType; // The will identify which type of entity to spawn on load
	private int amountOfStages = 0; // Total of stages
	private int stage = 0; // Current stage to add actions to

	private JsonObject mainJson = new JsonObject();
	private JsonObject mainStagesJson = new JsonObject();
	// The JSONs which store actions and components for each stage
	private Map<Integer, JsonObject> stageJsons = new HashMap<>();
	// Stores action as [stage, [action, component]] of which there can be multiple actions per stage
	private Map<Integer, LinkedHashMap<StageAction, StageComponent>> stagesOfActions = new HashMap<>();

	/**
	 * Creates a script of actions and components that can be run by a {@link uk.co.harieo.StagePlay.entities.ScriptedEntity}
	 *
	 * @param scriptName to identify the script after it is finalized
	 */
	public StagedScript(String scriptName, StageableEntity entityType, String entityName) {
		this.scriptName = scriptName;
		this.entityName = entityName;
		this.entityType = entityType;

		addNewStage(); // Handle stage 1
	}

	/**
	 * @return the player-assigned name of this script
	 */
	public String getScriptName() {
		return scriptName;
	}

	/**
	 * Set the name of this script
	 *
	 * @param name to set the name to
	 */
	public void setScriptName(String name) {
		this.scriptName = name;
	}

	/**
	 * @return the name of the entity
	 */
	public String getEntityName() {
		return entityName;
	}

	/**
	 * Sets the name of the entity when spawned
	 *
	 * @param entityName to set the name to
	 */
	public void setEntityName(String entityName) {
		this.entityName = entityName;
	}

	/**
	 * @return the type of entity this script will be used on
	 */
	public StageableEntity getEntityType() {
		return entityType;
	}

	/**
	 * Sets the type of entity this script will be used on
	 *
	 * @param entityType to set the entity type to
	 */
	public void setEntityType(StageableEntity entityType) {
		this.entityType = entityType;
	}

	/**
	 * Adds 1 new stage after the current highest stage and switches to it for editing. The current stage must not be
	 * empty of actions for a new stage to be added, which should be validated before calling this method.
	 */
	public void addNewStage() {
		if (stage != 0 && getCurrentActions().isEmpty()) { // This should be validated by the command
			throw new IllegalStateException("Cannot progress stage without actions");
		}

		stage = amountOfStages
				+ 1; // The stage that is being edited may not be the latest so make sure not to duplicate
		amountOfStages++;
		stagesOfActions.putIfAbsent(stage, new LinkedHashMap<>());
		stageJsons.putIfAbsent(stage, new JsonObject());
	}

	/**
	 * @return the stage that is currently being edited
	 */
	public int getCurrentStage() {
		return stage;
	}

	/**
	 * Sets the stage that is currently being edited
	 *
	 * @param stage to edit
	 */
	public void setEditingStage(int stage) {
		if (amountOfStages < stage) {
			throw new IllegalArgumentException("Attempting to switch to stage which does not exist");
		}

		this.stage = stage;
	}

	/**
	 * Adds a new action with its relevant component to the current stage of the script
	 *
	 * @param action to be performed on this stage
	 * @param component that describes the action
	 */
	public void addAction(StageAction action, StageComponent component) {
		getCurrentActions().put(action, component);
		component.addToJson(action, getStageJson()); // The component handles adding to the JSON with keys and values
	}

	/**
	 * Code to detect erroneous values in the current script in the event that an unknown error is persisting
	 *
	 * @return whether the script is valid or not
	 */
	public ReportResult validateStages() {
		ReportResult report = new ReportResult(scriptName + " Script Report");

		Location lastApplicableLocation; // The last location that the entity was moved to
		if (getActionsForStage(1).containsKey(StageAction.START)) {
			report.addCheckMessage("Stage 1 contains a starting point for the entity", ResultType.SUCCESS);
			lastApplicableLocation = ((LocationComponent) getActionsForStage(1).get(StageAction.START)).getValue();
		} else {
			report.addCheckMessage("Stage 1 does NOT contain a starting point for the entity", ResultType.ERROR);
			return report; // Further validation requires a starting position as reference
		}

		for (int i = 1; i <= amountOfStages; i++) {
			Map<StageAction, StageComponent> actions = getActionsForStage(i);

			if (actions.isEmpty()) {
				report.addCheckMessage("Stage " + i + " is redundant: Has no actions", ResultType.WARN);
				continue; // Without any actions, there is nothing more to validate here
			}

			for (StageAction action : actions.keySet()) {
				if (action.equals(StageAction.WALK_TO)) {
					Location location = ((LocationComponent) actions.get(action)).getValue();
					if (location.distance(lastApplicableLocation) > 20) {
						report.addCheckMessage("A " + StageAction.WALK_TO.name()
										+ " (Stage " + i + ") action is more than 20 blocks from the last location, "
										+ "which will cause an execution error",
								ResultType.ERROR);
					} else {
						report.addCheckMessage(
								"The entity at Stage " + i + " walks no more than 20 blocks from the last location",
								ResultType.SUCCESS);
					}

					lastApplicableLocation = location;
				}
			}
		}

		return report;
	}

	/**
	 * Commits the current script to a JSON file in the plugin folder so that it can be loaded/executed
	 *
	 * @return whether the commit was successful
	 * @throws FileNotFoundException when the plugin folder cannot be accessed
	 * @throws FileAlreadyExistsException when another script exists with the same name, preventing it being saved
	 */
	public boolean commit() throws FileNotFoundException, FileAlreadyExistsException {
		if (validateStages().hasEncounteredError()) { // This should be done more thoroughly on command
			return false;
		}

		for (int i = 1; i <= amountOfStages; i++) {
			mainStagesJson.add(String.valueOf(i), stageJsons.get(i));
		}

		mainJson.addProperty("name", scriptName); // Sets the script name as the static identifier
		mainJson.addProperty("entityName", entityName);
		mainJson.addProperty("entityType", entityType.name());
		mainJson.addProperty("amountOfStages", amountOfStages); // Used on loading
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

		File jsonFile = new File(pluginDirectory.getPath() + "/" + scriptName + ".json");
		if (jsonFile.exists()) {
			throw new FileAlreadyExistsException("Script file " + scriptName + ".json already exists");
		}

		try (FileWriter writer = new FileWriter(jsonFile)) {
			writer.write(mainJson.toString());
			stageJsons.clear(); // Save some memory in the editor
			stagesOfActions.clear();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * @return the total amount of stages in this script
	 */
	public int getAmountOfStages() {
		return amountOfStages;
	}

	/**
	 * Get a map of all actions in a specific stage with their matching components
	 *
	 * @param stage to get the actions of
	 * @return a map of the actions and their matching components
	 */
	public Map<StageAction, StageComponent> getActionsForStage(int stage) {
		return stagesOfActions.getOrDefault(stage, null);
	}

	/**
	 * @return a map of the actions and their matching components for the current editing stage
	 */
	public Map<StageAction, StageComponent> getCurrentActions() {
		return stagesOfActions.get(stage);
	}

	/**
	 * @return a multi-dimensional map with all stages and a map of all actions for that stage
	 */
	public Map<Integer, LinkedHashMap<StageAction, StageComponent>> getAllActions() {
		return stagesOfActions;
	}

	public void removeLatestStage() {
		amountOfStages--;
		stage = amountOfStages; // Prevent editing a removed stage

		int removedStage = amountOfStages + 1;
		stageJsons.remove(removedStage);
		stagesOfActions.remove(removedStage);
	}

	/**
	 * @return the {@link JsonObject} containing the recorded actions for the current stage
	 */
	private JsonObject getStageJson() {
		return stageJsons.get(stage);
	}

}
