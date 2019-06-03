package uk.co.harieo.StagePlay.components.types;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import uk.co.harieo.StagePlay.components.StageComponent;
import uk.co.harieo.StagePlay.scripts.StageAction;

public class IntegerComponent extends StageComponent<Integer> {

	@Override
	public Integer parseElement(JsonElement element) {
		return element.getAsInt();
	}

	@Override
	public void addToJson(StageAction action, JsonObject object) {
		object.addProperty(action.name(), getValue());
	}
}
