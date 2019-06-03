package uk.co.harieo.StagePlay.scripts;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import net.minecraft.server.v1_12_R1.*;
import uk.co.harieo.FurCore.FurCore;
import uk.co.harieo.StagePlay.StagePlay;
import uk.co.harieo.StagePlay.components.StageComponent;
import uk.co.harieo.StagePlay.components.types.IntegerComponent;
import uk.co.harieo.StagePlay.components.types.LocationComponent;
import uk.co.harieo.StagePlay.components.types.StringComponent;
import uk.co.harieo.StagePlay.entities.ScriptedEntity;
import uk.co.harieo.StagePlay.events.ScriptExecuteEvent;

public class ScriptExecutor extends BukkitRunnable {

	private StagedScript script;
	private ScriptedEntity entity;

	private int stageIndex = 1;
	private Map<Integer, LinkedHashMap<StageAction, StageComponent>> stages = new HashMap<>();
	private List<StageAction> actionsPendingRemoval = new ArrayList<>();

	private boolean pendingDestination = false;
	private Location lastKnownLocation;
	private int pendingTime = 0;
	private boolean finalPendingTime = false;

	/**
	 * A runnable which executes a linear set of actions from a loaded {@link StagedScript}
	 *
	 * @param world to execute the script in
	 * @param script that will be executed
	 */
	public ScriptExecutor(World world, StagedScript script) {
		this.script = script;
		// Translating color codes here benefits simulations that aren't final
		this.entity = script.getEntityType()
				.newEntity(world, ChatColor.translateAlternateColorCodes('&', script.getEntityName()));

		addStartingPoint();
		addScriptActions();
		((CraftWorld) world).getHandle().addEntity(entity.getEntity());
	}

	/**
	 * Runs the script with default timing (1 action per second) and calls the {@link ScriptExecuteEvent}
	 *
	 * @param player the player that executed the script
	 */
	public void playerRunScript(Player player) {
		runTaskTimer(StagePlay.getInstance(), 20, 20); // 1 second intervals
		Bukkit.getPluginManager().callEvent(new ScriptExecuteEvent(this, entity, script, player));
	}

	@Override
	public void run() {
		if (!stages.containsKey(stageIndex) || !entity.getEntity()
				.isAlive()) { // The script has nothing left to execute and pending actions are resolved
			cancel();
			return;
		}

		Map<StageAction, StageComponent> actions = stages.get(stageIndex);
		if (actions == null) {
			cancel();
			throw new IllegalStateException("A script was executed with no applicable actions or stages");
		}

		// Prevents script from continuing until the entity is no longer in transit, preventing overlapping actions
		if (pendingDestination) {
			if (lastKnownLocation != null && entity.getCurrentLocation().distance(lastKnownLocation) == 0) {
				pendingDestination = false;
			} else {
				lastKnownLocation = entity.getCurrentLocation();
				return;
			}
		}

		// Triggered on the WAIT action
		if (pendingTime > 0) {
			pendingTime--;
			actions.remove(StageAction.WAIT); // There is n
			return;
		} else if (finalPendingTime) { // Triggered on STOP action
			entity.destroyEntity(); // No more time to wait and destroy variable is true
			cancel();
			return;
		}

		actionsPendingRemoval.clear();
		for (StageAction action : actions.keySet()) {
			switch (action) {
				case START: {
					break;
				}
				case WALK_TO: {
					Location location = getLocationFromGenericComponent(actions.get(action));
					if (!pendingDestination) {
						NavigationAbstract navigation = entity.getEntity().getNavigation();
						PathEntity pathEntity = navigation.a(location.getX(), location.getY(), location.getZ());
						navigation.a(pathEntity, 0.8);
						pendingDestination = true; // The entity is now in transit
					}
					break;
				}
				case TALK:
					speak(actions.get(action), false);
					break;
				case SHOUT:
					speak(actions.get(action), true);
					break;
				case WAIT:
					addPendingTime(actions.get(action), false);
					return;
				case STOP:
					addPendingTime(actions.get(action), true);
					return;
				default:
					cancel();
					throw new IllegalStateException("Attempt to execute unrecognised action");
			}
			// This helps to prevent repeating actions
			actionsPendingRemoval.add(action);
		}

		for (StageAction removeAction : actionsPendingRemoval) {
			stages.get(stageIndex).remove(removeAction); // Removing the action prevents it being repeated
		}

		stageIndex++;
	}

	/**
	 * Parses the starting point for the entity, which will be its spawn location
	 */
	private void addStartingPoint() {
		Map<StageAction, StageComponent> firstStageActions = script.getActionsForStage(1);
		if (!firstStageActions.containsKey(StageAction.START)) {
			cancel();
			throw new RuntimeException("Attempt to execute script with no START action");
		}

		LocationComponent component = (LocationComponent) firstStageActions.get(StageAction.START);
		entity.setLocation(component.getValue());
	}

	/**
	 * Add all actions from the {@link #script} to local fields so they can be executed
	 */
	private void addScriptActions() {
		for (int i = 1; i <= script.getAmountOfStages(); i++) {
			// As the actions will get removed in processing, we need a new map so the script is not edited
			LinkedHashMap<StageAction, StageComponent> newActionsMap = new LinkedHashMap<>(
					script.getActionsForStage(i));
			stages.put(i, newActionsMap);
		}
	}

	/**
	 * Adds more time to the {@link #pendingTime} field and sets {@link #finalPendingTime} to true which will destroy
	 * the entity when the time runs out, if specified.
	 *
	 * @param component which is an instance of {@link IntegerComponent} and contains the time to add
	 * @param isToStop whether or not this is a countdown to stop the script or not
	 */
	private void addPendingTime(StageComponent component, boolean isToStop) {
		IntegerComponent integerComponent = (IntegerComponent) component;
		pendingTime += integerComponent.getValue();
		finalPendingTime = isToStop;
	}

	/**
	 * Converts a {@link StageComponent} into a {@link LocationComponent} and returns the value of the {@link
	 * LocationComponent}
	 *
	 * @param component which is an instance of {@link LocationComponent}
	 * @return the value of the casted {@link LocationComponent}
	 */
	private Location getLocationFromGenericComponent(StageComponent component) {
		return ((LocationComponent) component).getValue();
	}

	/**
	 * Sends a message to all applicable players which appears to be from the entity
	 *
	 * @param component which is an instance of {@link StringComponent} containing the text to be spoken
	 * @param isShouting whether to shout (all players will hear) or talk (only players within 15 blocks will hear)
	 */
	private void speak(StageComponent component, boolean isShouting) {
		String text = ((StringComponent) component).getValue();
		Bukkit.getOnlinePlayers().forEach(player -> {
			// Shouting is to all players, talking (the opposite) is only to players within 15 blocks
			if (isShouting || entity.getCurrentLocation().distance(player.getLocation()) <= 15) {
				player.sendMessage(
						ChatColor.YELLOW + ChatColor.translateAlternateColorCodes('&', script.getEntityName())
								+ ChatColor.DARK_GRAY + " " + FurCore.ARROWS + " "
								+ ChatColor.GRAY + text);
			}
		});
	}

}
