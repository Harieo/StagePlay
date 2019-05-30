package uk.co.harieo.StagePlay.scripts;

import com.google.gson.JsonObject;
import java.util.HashMap;
import java.util.Map;
import uk.co.harieo.StagePlay.components.StageComponent;

public class StagedScript {

	// TODO this class

	private String name; // This name will identify the saved file, if any

	private JsonObject jsonObject;
	private Map<Integer, Map<StageActions, StageComponent>> stagesOfComponents = new HashMap<>();

	/**
	 * Creates a script of actions and components that can be run by a {@link uk.co.harieo.StagePlay.entities.ScriptedEntity}
	 *
	 * @param scriptName to identify the script after it is finalized
	 */
	public StagedScript(String scriptName) {
		this.name = scriptName;
		this.jsonObject = new JsonObject();
		jsonObject.addProperty("name", scriptName); // Sets the script name as the static identifier
	}

}
