package uk.co.harieo.StagePlay.scripts;

import uk.co.harieo.StagePlay.components.DefinedComponent;

public enum StageAction {

	START("The starting point for the entity", DefinedComponent.LOCATION),
	WALK_TO("Walks to this location", DefinedComponent.LOCATION),
	WAIT("Pause the script for a certain amount of time", DefinedComponent.SECONDS),
	TALK("Speak a line of text to nearby players", DefinedComponent.TEXT),
	SHOUT("Speak a line of text to all players", DefinedComponent.TEXT),
	STOP("Stop the script after a certain amount of seconds", DefinedComponent.SECONDS);

	private String usage;
	private DefinedComponent component;

	/**
	 * An action that the controlled entity can perform and the components required from the user to make the action work
	 *
	 * @param usage what the action is used to do
	 * @param requiredComponent the component needed to perform the action (e.g how far to go)
	 */
	StageAction(String usage, DefinedComponent requiredComponent) {
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
	public DefinedComponent getComponent() {
		return component;
	}

}
