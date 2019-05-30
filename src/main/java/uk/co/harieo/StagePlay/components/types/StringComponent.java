package uk.co.harieo.StagePlay.components.types;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import uk.co.harieo.StagePlay.components.StageComponent;
import uk.co.harieo.StagePlay.scripts.StageActions;

public class StringComponent extends StageComponent<String> {

	@Override
	public String parseElement(JsonElement element) {
		return element.getAsString();
	}

	@Override
	public void addToJson(StageActions action, JsonObject object) {
		object.addProperty(action.name(), getValue());
	}

}
