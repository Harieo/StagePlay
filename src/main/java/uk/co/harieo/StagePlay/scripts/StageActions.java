package uk.co.harieo.StagePlay.scripts;

import uk.co.harieo.StagePlay.components.DefinedComponents;

public enum StageActions {

	WALK_FORWARD("Walk in the direction it's facing for a certain amount of blocks", DefinedComponents.DISTANCE),
	WALK_TO("Walks to this location", DefinedComponents.LOCATION),
	FACE("Face this direction", DefinedComponents.FACING),
	STOP("Stop walking"),
	TALK("Speak a line of text", DefinedComponents.TEXT),
	WAIT("Wait for a certain amount of seconds", DefinedComponents.SECONDS);

	private String usage;
	private DefinedComponents[] components;

	/**
	 * An action that the controlled entity can perform and the components required from the user to make the action work
	 *
	 * @param usage what the action is used to do
	 * @param requiredComponents the component(s) needed to perform the action (e.g how far to go)
	 */
	StageActions(String usage, DefinedComponents... requiredComponents) {
		this.usage = usage;
		this.components = requiredComponents;
	}

	/**
	 * @return a string which describes this action to the user
	 */
	public String getUsage() {
		return usage;
	}

	/**
	 * @return the components needed from the user to perform this action
	 */
	public DefinedComponents[] getComponents() {
		return components;
	}

}
