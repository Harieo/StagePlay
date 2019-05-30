package uk.co.harieo.StagePlay.components;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import uk.co.harieo.StagePlay.scripts.StageActions;

public abstract class StageComponent<V> {

	private V value; // The stored value for this component

	/**
	 * @return the current value for this component, which is null until set
	 */
	public V getValue() {
		return value;
	}

	/**
	 * Sets the currently stored value for this component
	 *
	 * @param value to be set
	 */
	public void setValue(V value) {
		this.value = value;
	}

	/**
	 * Parses the given {@link JsonElement} for the serialized component, assuming that the element definitely
	 * contains the correct serialized value
	 *
	 * @param element to be parsed
	 * @return the deserialized value
	 */
	public abstract V parseElement(JsonElement element);

	/**
	 * Adds this component to the given {@link JsonObject} with the corresponding action as the key
	 *
	 * @param action as the property key
	 * @param object to add the component to
	 */
	public abstract void addToJson(StageActions action, JsonObject object);

}
