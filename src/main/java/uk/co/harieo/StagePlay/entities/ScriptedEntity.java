package uk.co.harieo.StagePlay.entities;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import net.minecraft.server.v1_12_R1.*;
import uk.co.harieo.StagePlay.utils.Utils;

public class ScriptedEntity<T extends EntityInsentient> {

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
			entity = (T) constructor.newInstance(((CraftWorld)world).getHandle());
		} catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException e) {
			throw new RuntimeException(e);
		}

		this.goalSelector = entity.goalSelector;
		this.targetSelector = entity.targetSelector;
		this.spawnWorld = world;

		LinkedHashSet goalB = (LinkedHashSet) Utils.getPrivateField("b", PathfinderGoalSelector.class, entity.goalSelector);
		goalB.clear();
		LinkedHashSet goalC = (LinkedHashSet) Utils.getPrivateField("c", PathfinderGoalSelector.class, entity.goalSelector);
		goalC.clear();
		LinkedHashSet targetB = (LinkedHashSet) Utils.getPrivateField("b", PathfinderGoalSelector.class, entity.targetSelector);
		targetB.clear();
		LinkedHashSet targetC = (LinkedHashSet) Utils.getPrivateField("c", PathfinderGoalSelector.class, entity.targetSelector);
		targetC.clear();

		entity.onGround = true; // Fixes an NMS issue with unnatural spawning and movement
		entity.setCustomName(customName);
		entity.setCustomNameVisible(true);
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

	public T getEntity() {
		return entity;
	}

}
