package uk.co.harieo.StagePlay.entities;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import net.minecraft.server.v1_12_R1.*;
import uk.co.harieo.StagePlay.utils.Utils;

public class ScriptedEntity<T extends EntityInsentient> {

	public static List<ScriptedEntity> spawnedEntities = new ArrayList<>();

	private T entity;
	private PathfinderGoalSelector goalSelector;
	private PathfinderGoalSelector targetSelector;

	private Location location;
	private World spawnWorld;
	private int actionIndex = 0;

	@SuppressWarnings("unchecked")
	ScriptedEntity(Class<T> clazz, World world, String customName) {
		try {
			Constructor<?> constructor = clazz.getConstructor(net.minecraft.server.v1_12_R1.World.class);
			entity = (T) constructor.newInstance(((CraftWorld) world).getHandle());
		} catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException e) {
			throw new RuntimeException(e);
		}

		this.goalSelector = entity.goalSelector;
		this.targetSelector = entity.targetSelector;
		this.spawnWorld = world;

		LinkedHashSet goalB = (LinkedHashSet) Utils
				.getPrivateField("b", PathfinderGoalSelector.class, entity.goalSelector);
		goalB.clear();
		LinkedHashSet goalC = (LinkedHashSet) Utils
				.getPrivateField("c", PathfinderGoalSelector.class, entity.goalSelector);
		goalC.clear();
		LinkedHashSet targetB = (LinkedHashSet) Utils
				.getPrivateField("b", PathfinderGoalSelector.class, entity.targetSelector);
		targetB.clear();
		LinkedHashSet targetC = (LinkedHashSet) Utils
				.getPrivateField("c", PathfinderGoalSelector.class, entity.targetSelector);
		targetC.clear();

		entity.onGround = true; // Fixes an NMS issue with unnatural spawning and movement
		entity.setCustomName(customName);
		entity.setCustomNameVisible(true);

		spawnedEntities.add(this);
	}

	public PathfinderGoalSelector getGoalSelector() {
		return goalSelector;
	}

	public PathfinderGoalSelector getTargetSelector() {
		return targetSelector;
	}

	public void setLocation(Location location) {
		entity.setLocation(location.getX(), location.getY(), location.getZ(), location.getYaw(),
				location.getPitch());
		this.location = location;
	}

	public Location getCurrentLocation() {
		return new Location(spawnWorld, entity.locX, entity.locY, entity.locZ);
	}

	public boolean isLocated() {
		return location != null;
	}

	public void addGoalAction(PathfinderGoal goal) {
		goalSelector.a(actionIndex, goal);
		actionIndex++;
	}

	public void addTargetAction(PathfinderGoal goal) {
		targetSelector.a(actionIndex, goal);
		actionIndex++;
	}

	public void destroyEntity() {
		entity.die();
		spawnedEntities.remove(entity);
	}

	public T getEntity() {
		return entity;
	}

	/**
	 * Checks whether an entity is controlled by the {@link uk.co.harieo.StagePlay.scripts.ScriptExecutor}
	 *
	 * @param entity to check if it is scripted
	 * @return whether the given entity is scripted
	 */
	public static boolean isScripted(EntityInsentient entity) {
		for (ScriptedEntity scriptedEntity : spawnedEntities) {
			if (scriptedEntity.getEntity().equals(entity)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Destroy any entities controlled by the {@link uk.co.harieo.StagePlay.scripts.ScriptExecutor} within a specified
	 * distance
	 *
	 * @param player to destroy entities around
	 * @param distance from the player to be destroyed
	 */
	public static void destroyNearbyEntities(Player player, double distance) {
		for (ScriptedEntity entity : spawnedEntities) {
			if (entity.getCurrentLocation().distance(player.getLocation()) <= distance) {
				entity.destroyEntity();
			}
		}
	}

	/**
	 * Deletes entities from the cache to indicate it is no longer controlled (does not destroy the entity)
	 *
	 * @param entityInsentient to remove from the cache
	 */
	public static void purgeEntity(EntityInsentient entityInsentient) {
		for (ScriptedEntity scriptedEntity : spawnedEntities) {
			if (scriptedEntity.getEntity().equals(entityInsentient)) {
				spawnedEntities.remove(scriptedEntity);
			}
		}
	}

}
