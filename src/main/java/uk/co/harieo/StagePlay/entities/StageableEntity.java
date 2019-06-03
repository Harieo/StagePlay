package uk.co.harieo.StagePlay.entities;

import org.bukkit.World;

import net.minecraft.server.v1_12_R1.*;

public enum StageableEntity {

	// TODO: Add more version-specific entities

	ZOMBIE(EntityZombie.class),
	VILLAGER(EntityVillager.class),
	SPIDER(EntitySpider.class),
	SLIME(EntitySlime.class);

	private Class<? extends EntityInsentient> clazz;

	/**
	 * Represents an entity that can be scripted using the {@link ScriptedEntity} framework
	 *
	 * @param entityClass of the type of entity to be scripted which extends {@link EntityInsentient} to allow for
	 * scripting via NMS Pathfinder goals
	 */
	StageableEntity(Class<? extends EntityInsentient> entityClass) {
		this.clazz = entityClass;
	}

	/**
	 * Creates a new instance of {@link ScriptedEntity} using this entity type
	 *
	 * @param world to spawn the entity in
	 * @return the script-able entity
	 */
	public ScriptedEntity newEntity(World world) {
		return new ScriptedEntity<>(clazz, world);
	}

}
