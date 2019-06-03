package uk.co.harieo.StagePlay;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import app.ashcon.intake.Intake;
import app.ashcon.intake.bukkit.BukkitIntake;
import app.ashcon.intake.parametric.AbstractModule;
import app.ashcon.intake.parametric.Injector;
import java.util.Arrays;
import uk.co.harieo.StagePlay.commands.ScriptCommand;
import uk.co.harieo.StagePlay.commands.modules.StageActionModule;
import uk.co.harieo.StagePlay.commands.modules.StageDefinedComponentModule;
import uk.co.harieo.StagePlay.commands.modules.StageEntityModule;
import uk.co.harieo.StagePlay.commands.subcommands.ScriptActionCommands;
import uk.co.harieo.StagePlay.commands.subcommands.ScriptEditingCommands;
import uk.co.harieo.StagePlay.commands.subcommands.ScriptExecutionCommands;
import uk.co.harieo.StagePlay.commands.subcommands.ScriptHelpCommands;
import uk.co.harieo.StagePlay.entities.ScriptedEntity;
import uk.co.harieo.StagePlay.events.ScriptedEntityListener;

public class StagePlay extends JavaPlugin {

	private static StagePlay instance;

	@Override
	public void onEnable() {
		instance = this;

		registerCommands(new ScriptCommand(), new ScriptActionCommands(), new ScriptEditingCommands(), new ScriptExecutionCommands(), new ScriptHelpCommands());
		registerListeners(new ScriptedEntityListener());
	}

	@Override
	public void onDisable() {
		getLogger().info("Cleaning up all scripted entities...");
		ScriptedEntity.destroyAllEntities();
		getLogger().info("Cleaned all entities successfully");
	}

	/**
	 * Registers the plugin listeners
	 *
	 * @param listeners to be registered
	 */
	private void registerListeners(Listener... listeners) {
		Arrays.stream(listeners).forEach(listener -> Bukkit.getPluginManager().registerEvents(listener, this));
	}

	/**
	 * Registers commands to the Intake system in bulk and injects the necessary {@link
	 * app.ashcon.intake.parametric.Module} parameters
	 */
	private void registerCommands(Object... commands) {
		Injector injector = Intake.createInjector();
		// Bind modules
		injector.install(new StageActionModule());
		injector.install(new StageEntityModule());
		injector.install(new StageDefinedComponentModule());
		new BukkitIntake(this, injector, commands);
	}

	public static StagePlay getInstance() {
		return instance;
	}

}
