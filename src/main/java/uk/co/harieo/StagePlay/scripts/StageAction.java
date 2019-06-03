package uk.co.harieo.StagePlay.scripts;

import uk.co.harieo.StagePlay.components.DefinedComponents;

public enum StageAction {

	START("The starting point for the entity", DefinedComponents.LOCATION),
	WALK_TO("Walks to this location", DefinedComponents.LOCATION),
	WAIT("Pause the script for a certain amount of time", DefinedComponents.SECONDS),
	TALK("Speak a line of text to nearby players", DefinedComponents.TEXT),
	SHOUT("Speak a line of text to all players", DefinedComponents.TEXT);

	private String usage;
	private DefinedComponents component;

	/**
	 * An action that the controlled entity can perform and the components required from the user to make the action work
	 *
	 * @param usage what the action is used to do
	 * @param requiredComponent the component needed to perform the action (e.g how far to go)
	 */
	StageAction(String usage, DefinedComponents requiredComponent) {
		this.usage = usage;
		this.component = requiredComponent;
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
	public DefinedComponents getComponent() {
		return component;
	}

}