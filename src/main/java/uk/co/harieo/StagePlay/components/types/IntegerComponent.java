package uk.co.harieo.StagePlay.components.types;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import uk.co.harieo.StagePlay.components.StageComponent;
import uk.co.harieo.StagePlay.scripts.StageActions;

public class IntegerComponent extends StageComponent<Integer> {

	@Override
	protected Integer parseElement(JsonElement element) {
		return element.getAsInt();
	}

	@Override
	protected void addToJson(StageActions action, JsonObject object) {
		object.addProperty(action.name(), getValue());
	}
}
