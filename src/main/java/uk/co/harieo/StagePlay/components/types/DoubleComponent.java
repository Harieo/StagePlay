package uk.co.harieo.StagePlay.components.types;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import uk.co.harieo.StagePlay.components.StageComponent;
import uk.co.harieo.StagePlay.scripts.StageActions;

public class DoubleComponent extends StageComponent<Double> {

	@Override
	public Double parseElement(JsonElement element) {
		return element.getAsDouble();
	}

	@Override
	public void addToJson(StageActions action, JsonObject object) {
		object.addProperty(action.name(), getValue());
	}

}