package uk.co.harieo.StagePlay.enums;

import org.bukkit.Sound;

public enum StageAction {

	WALK_FORWARD("Walk in the direction it's facing for a certain amount of blocks", Component.DISTANCE),
	WALK_TO("Walks to this location", Component.LOCATION),
	FACE("Face this direction", Component.FACE),
	STOP("Stop walking"),
	TALK("Speak a line of text", Component.TEXT),
	WAIT("Wait for a certain amount of seconds", Component.SECONDS),
	MAKE_SOUND("Make the entity generate a certain sound at its location", Component.CUSTOM_TYPE);

	private String usage;
	private Component[] components;

	StageAction(String usage, Component... requiredComponents) {
		this.usage = usage;
		this.components = requiredComponents;
	}

	/**
	 * @return a string which describes this action to the user
	 */
	public String getUsage() {
		return usage;
	}

	public Component[] getComponents() {
		return components;
	}

	public enum Component {
		TEXT("A single line of text", "This is a single line of text"),
		DISTANCE("An amount of blocks to be moved", "3"),
		LOCATION("A location to indicate a destination or start point", "X:3, Y:15, Z:3"),
		FACE("A yaw and pitch that the entity will face", "-90, -4 (East, Normal Height)"),
		SECONDS("A number of seconds", "5"),
		CUSTOM_TYPE("A name or type of defined Minecraft object", "Cow Moo Sound: ENTITY_COW_AMBIENT");

		private String description;
		private String example;

		Component(String description, String example) {
			this.description = description;
			this.example = example;
		}

		public String getDescription() {
			return description;
		}

		public String getExample() {
			return example;
		}
	}
}
