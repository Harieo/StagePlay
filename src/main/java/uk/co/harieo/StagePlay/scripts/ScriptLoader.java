package uk.co.harieo.StagePlay.scripts;

import org.bukkit.ChatColor;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import uk.co.harieo.StagePlay.StagePlay;
import uk.co.harieo.StagePlay.components.StageComponent;
import uk.co.harieo.StagePlay.components.types.*;
import uk.co.harieo.StagePlay.entities.StageableEntity;

public class ScriptLoader {

	private static Map<String, StagedScript> loadedScripts = new HashMap<>();

	/**
	 * Loads a JSON encoded script file created from the {@link StagedScript} editor into the cache, which can then be
	 * retrieved via {@link #getScript(String)}
	 *
	 * @param scriptName that the script was saved under (file name without file extension)
	 * @return whether the script was valid or not (an invalid script may be corrupt or edited by an end user improperly)
	 * @throws FileNotFoundException if no such script is found in the plugin folder
	 */
	public static boolean loadScript(String scriptName) throws FileNotFoundException {
		if (isScriptLoaded(scriptName)) {
			return true; // Script is valid and already loaded
		}

		File scriptFile = new File(StagePlay.getInstance().getDataFolder().getPath() + "/" + scriptName + ".json");
		if (!scriptFile.exists()) {
			throw new FileNotFoundException("No such script: " + scriptName + ".json");
		}

		try (FileReader reader = new FileReader(scriptFile)) {
			JsonParser parser = new JsonParser();
			JsonObject fullScript = parser.parse(reader).getAsJsonObject();

			String entityName;
			StageableEntity entityType;
			int amountOfStages;
			JsonObject stagesObject;
			if (fullScript.has("entityName")
					&& fullScript.has("entityType")
					&& fullScript.has("amountOfStages")
					&& fullScript.has("stages")) {
				entityName = ChatColor.translateAlternateColorCodes('&', fullScript.get("entityName").getAsString().trim());
				entityType = StageableEntity.valueOf(fullScript.get("entityType").getAsString());
				amountOfStages = fullScript.get("amountOfStages").getAsInt();
				stagesObject = fullScript.getAsJsonObject("stages");
			} else {
				return false;
			}

			StagedScript script = new StagedScript(scriptName, entityType, entityName);

			if (!deserializeStages(script, stagesObject, amountOfStages)) { // If it fails to deserialize
				return false;
			}

			loadedScripts.put(scriptName, script);
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * @return a map of script names and the attached loaded script
	 */
	public static Map<String, StagedScript> getLoadedScripts() {
		return loadedScripts;
	}

	/**
	 * Retrieves a loaded script by the script name
	 *
	 * @param scriptName of the script (not the file name)
	 * @return the script or null if no such script has been loaded
	 */
	public static StagedScript getScript(String scriptName) {
		return loadedScripts.getOrDefault(scriptName, null);
	}

	public static boolean isScriptLoaded(String scriptName) {
		return loadedScripts.containsKey(scriptName);
	}

	/**
	 * Converts an action and serialized component into their code counterparts. The component must match the action
	 * as the action determines which component deserializer will be used.
	 *
	 * @param action to load the component for
	 * @param serializedComponent JSON encoded component value
	 * @return the loaded component
	 */
	private static StageComponent convertToComponent(StageAction action, JsonElement serializedComponent) {
		StageComponent component;
		switch (action.getComponent()) {
			case TEXT:
				StringComponent stringComponent = new StringComponent();
				stringComponent.setValue(stringComponent.parseElement(serializedComponent));
				component = stringComponent;
				break;
			case SECONDS:
				IntegerComponent integerComponent = new IntegerComponent();
				integerComponent.setValue(integerComponent.parseElement(serializedComponent));
				component = integerComponent;
				break;
			case DISTANCE:
				DoubleComponent doubleComponent = new DoubleComponent();
				doubleComponent.setValue(doubleComponent.parseElement(serializedComponent));
				component = doubleComponent;
				break;
			case LOCATION:
				LocationComponent locationComponent = new LocationComponent();
				locationComponent.setValue(locationComponent.parseElement(serializedComponent));
				component = locationComponent;
				break;
			default:
				// This would be more than an invalid script and, therefore, it's an exception
				throw new IllegalStateException(
						"Attempt to load script with invalid component: " + action.getComponent().getName());
		}
		return component;
	}

	/**
	 * Using the JSON encoded 'stages' object, will loop and deserialize all actions with their components, adding them
	 * to the provided script in turn. The script provided should be blank or else the script will be added onto rather
	 * than made from scratch.
	 *
	 * @param script to add the deserialized values to
	 * @param serializedStages the JSON object containing all stages, actions and components
	 * @param amountOfStages the amount of stages the deserializer should parse
	 * @return whether the values were deserialized successfully
	 */
	private static boolean deserializeStages(StagedScript script, JsonObject serializedStages, int amountOfStages) {
		for (int i = 1; i <= amountOfStages; i++) {
			JsonObject stageJson;
			if (serializedStages.has(String.valueOf(i))) {
				stageJson = serializedStages.getAsJsonObject(String.valueOf(i));
			} else {
				return false;
			}

			for (Entry<String, JsonElement> entry : stageJson.entrySet()) {
				StageAction action = StageAction.valueOf(entry.getKey());

				StageComponent component = convertToComponent(action, entry.getValue());
				if (component == null) {
					return false;
				}

				script.addAction(action, component);
			}

			if (i + 1 <= amountOfStages) { // Prevent adding a stage above the last stage
				script.addNewStage();
			}
		}
		return true;
	}

}
