package uk.co.harieo.StagePlay.events;

import org.bukkit.craftbukkit.v1_12_R1.entity.CraftEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;

import net.minecraft.server.v1_12_R1.Entity;
import net.minecraft.server.v1_12_R1.EntityInsentient;
import uk.co.harieo.StagePlay.entities.ScriptedEntity;

public class EntityDamageListener implements Listener {

	@EventHandler
	public void onEntityDamage(EntityDamageEvent event) {
		Entity entity = ((CraftEntity) event.getEntity()).getHandle();
		if (entity instanceof EntityInsentient && ScriptedEntity.isScripted((EntityInsentient) entity)) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onEntityDeath(EntityDeathEvent event) {
		Entity entity = ((CraftEntity) event.getEntity()).getHandle();
		if (entity instanceof EntityInsentient) {
			EntityInsentient entityInsentient = (EntityInsentient) entity;
			if (ScriptedEntity.isScripted(entityInsentient)) {
				ScriptedEntity.purgeEntity(entityInsentient);
			}
		}
	}

}
