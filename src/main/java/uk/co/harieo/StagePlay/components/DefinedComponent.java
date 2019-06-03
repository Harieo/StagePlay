package uk.co.harieo.StagePlay.components;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import uk.co.harieo.StagePlay.components.types.*;

public enum DefinedComponent {

	TEXT("Text", "A simple line of text", "This is a line of text.", StringComponent.class),
	DISTANCE("Distance", "An amount of blocks in a straight line", "5.5", DoubleComponent.class),
	LOCATION("Location", "A 3D (x, y, z) location of a destination or starting point", "X:5 Y:15 Z:15.7",
			LocationComponent.class),
	SECONDS("Seconds", "An amount of time in seconds", "6", IntegerComponent.class);

	private String name;
	private String description;
	private String example;
	private Class<? extends StageComponent> componentClass;

	/**
	 * A component is a representation of data that is needed to complete an action. For example, the 'walk forward'
	 * action needs the amount of blocks to walk which is represented by the distance component.
	 *
	 * @param simpleName that is displayed to the user
	 * @param description to describe to the user what they need to input for this component
	 * @param example of the component when it is used
	 * @param componentClass which data type of handler is used for this component
	 */
	DefinedComponent(String simpleName, String description, String example,
			Class<? extends StageComponent> componentClass) {
		this.name = simpleName;
		this.description = description;
		this.example = example;
		this.componentClass = componentClass;
	}

	/**
	 * @return the user-friendly name of this component
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return a description of this component
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @return an example of how the data in this component is represented
	 */
	public String getExample() {
		return example;
	}

	/**
	 * @return whether the player's location can be used without manual input
	 */
	public boolean doesUseLocation() {
		return componentClass == LocationComponent.class;
	}

	/**
	 * Creates a new instance of the handling class for this component
	 *
	 * @param <T> representing which handling class is required
	 * @return the new handling class for this component
	 */
	@SuppressWarnings("unchecked")
	public <T extends StageComponent> T createComponent() {
		try {
			Constructor<?> component = componentClass.getConstructor();
			return (T) component.newInstance();
		} catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException e) {
			throw new RuntimeException(e);
		}
	}

}
