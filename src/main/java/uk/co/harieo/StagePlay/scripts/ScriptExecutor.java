package uk.co.harieo.StagePlay.scripts;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;

import java.util.Map;
import net.minecraft.server.v1_12_R1.*;
import uk.co.harieo.StagePlay.components.StageComponent;
import uk.co.harieo.StagePlay.components.types.FaceDirectionComponent;
import uk.co.harieo.StagePlay.components.types.FaceDirectionComponent.Facing;
import uk.co.harieo.StagePlay.components.types.LocationComponent;
import uk.co.harieo.StagePlay.components.types.StringComponent;
import uk.co.harieo.StagePlay.entities.PathfinderGoalSpeak;
import uk.co.harieo.StagePlay.entities.PathfinderGoalWalkTo;
import uk.co.harieo.StagePlay.entities.ScriptedEntity;

public class ScriptExecutor {

	public static void executeScript(World world, StagedScript script) {
		ScriptedEntity scriptedEntity = script.getEntityType().newEntity(world);

		for (int i = 1; i <= script.getAmountOfStages(); i++) {
			Map<StageActions, StageComponent> actions = script.getActionsForStage(i);
			for (StageActions action : actions.keySet()) {
				switch (action) {
					case START: {
						scriptedEntity.setLocation(getLocationFromGenericComponent(actions.get(action)));
						break;
					}
					case FACE: {
						Facing facing = ((FaceDirectionComponent) actions.get(action)).getValue();
						scriptedEntity.addGoalAction(new PathfinderGoalLookAtPlayer(scriptedEntity.getEntity(),
								EntityHuman.class, facing.getYaw(), facing.getPitch()));
						break;
					}
					case WALK_TO:
						scriptedEntity.addGoalAction(new PathfinderGoalWalkTo(scriptedEntity.getEntity(),
								getLocationFromGenericComponent(actions.get(action))));
						break;
					case TALK:
						addSpeakPathfinder(scriptedEntity, actions.get(action), false);
						break;
					case SHOUT:
						addSpeakPathfinder(scriptedEntity, actions.get(action), true);
						break;
					case STOP:
						return;
					default:
						throw new IllegalStateException("Attempt to execute unrecognised action");
				}
			}
		}

		// Spawn the entity after actions are added for safety
		((CraftWorld) world).getHandle().addEntity(scriptedEntity.getEntity());
	}

	private static Location getLocationFromGenericComponent(StageComponent component) {
		return ((LocationComponent) component).getValue();
	}

	private static void addSpeakPathfinder(ScriptedEntity scriptedEntity, StageComponent component,
			boolean isShouting) {
		String text = ((StringComponent) component).getValue();
		scriptedEntity.addGoalAction(
				new PathfinderGoalSpeak(scriptedEntity.getEntity(), scriptedEntity.getCurrentLocation(),
						text, isShouting));
	}

}
