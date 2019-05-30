package uk.co.harieo.StagePlay;

import org.bukkit.plugin.java.JavaPlugin;

public class StagePlay extends JavaPlugin {

	private static StagePlay instance;

	@Override
	public void onEnable() {
		instance = this;
	}

	public static StagePlay getInstance() {
		return instance;
	}

}
