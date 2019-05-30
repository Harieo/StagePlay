package uk.co.harieo.StagePlay.entities;

import org.bukkit.World;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import net.minecraft.server.v1_12_R1.*;
import uk.co.harieo.StagePlay.utils.Utils;

public class ScriptedEntity<T extends EntityInsentient> {

	private T entity;

	@SuppressWarnings("unchecked")
	public ScriptedEntity(Class<T> clazz, World world) {
		// TODO: Need to set position, add scripts

		try {
			Constructor<?> constructor = clazz.getConstructor(net.minecraft.server.v1_12_R1.World.class);
			entity = (T) constructor.newInstance(((CraftWorld)world).getHandle());
		} catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException e) {
			throw new RuntimeException(e);
		}

		List goalB = (List) Utils.getPrivateField("b", PathfinderGoalSelector.class, entity.goalSelector);
		goalB.clear();
		List goalC = (List) Utils.getPrivateField("c", PathfinderGoalSelector.class, entity.goalSelector);
		goalC.clear();
		List targetB = (List) Utils.getPrivateField("b", PathfinderGoalSelector.class, entity.targetSelector);
		targetB.clear();
		List targetC = (List) Utils.getPrivateField("c", PathfinderGoalSelector.class, entity.targetSelector);
		targetC.clear();
	}

}
