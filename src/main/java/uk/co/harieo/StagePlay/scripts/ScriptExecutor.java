package uk.co.harieo.StagePlay.scripts;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
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

public class ScriptExecutor extends BukkitRunnable {

	private StagedScript script;
	private ScriptedEntity entity;

	private int stageIndex = 1;
	private Map<Integer, LinkedHashMap<StageAction, StageComponent>> stages = new HashMap<>();
	private List<StageAction> actionsPendingRemoval = new ArrayList<>();

	private boolean pendingDestination = false;
	private Location lastKnownLocation;
	private int pendingTime = 0;

	public ScriptExecutor(World world, StagedScript script) {
		this.script = script;
		this.entity = script.getEntityType().newEntity(world);

		addStartingPoint();
		addScriptActions();
		((CraftWorld) world).getHandle().addEntity(entity.getEntity());
	}

	public void runScript() {
		runTaskTimer(StagePlay.getInstance(), 20, 20); // 1 second intervals
	}

	@Override
	public void run() {
		if (!stages.containsKey(stageIndex)) {
			cancel();
			return;
		}

		Map<StageAction, StageComponent> actions = stages.get(stageIndex);
		if (actions == null) {
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
			actions.remove(StageAction.WAIT); // The wait action has been enacted, don't repeat
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
					IntegerComponent component = (IntegerComponent) actions.get(action);
					pendingTime += component.getValue();
					return;
				default:
					throw new IllegalStateException("Attempt to execute unrecognised action");
			}
			// This helps to prevent repeating actions
			actionsPendingRemoval.add(action);
		}

		for (StageAction removeAction : actionsPendingRemoval) {
			stages.get(stageIndex).remove(removeAction);
		}

		stageIndex++;
	}

	private void addStartingPoint() {
		Map<StageAction, StageComponent> firstStageActions = script.getActionsForStage(1);
		if (!firstStageActions.containsKey(StageAction.START)) {
			throw new RuntimeException("Attempt to execute script with no START action");
		}

		LocationComponent component = (LocationComponent) firstStageActions.get(StageAction.START);
		entity.setLocation(component.getValue());
	}

	private void addScriptActions() {
		for (int i = 1; i <= script.getAmountOfStages(); i++) {
			// As the actions will get removed in processing, we need a new map so the script is not edited
			LinkedHashMap<StageAction, StageComponent> newActionsMap = new LinkedHashMap<>(
					script.getActionsForStage(i));
			stages.put(i, newActionsMap);
		}
	}

	private Location getLocationFromGenericComponent(StageComponent component) {
		return ((LocationComponent) component).getValue();
	}

	private void speak(StageComponent component, boolean isShouting) {
		String text = ((StringComponent) component).getValue();
		Bukkit.getOnlinePlayers().forEach(player -> {
			// Shouting is to all players, talking (the opposite) is only to players within 15 blocks
			if (isShouting || entity.getCurrentLocation().distance(player.getLocation()) <= 15) {
				player.sendMessage(
						ChatColor.YELLOW + script.getEntityName() + ChatColor.DARK_GRAY + FurCore.ARROWS + " "
								+ ChatColor.GRAY + text);
			}
		});
	}

}
