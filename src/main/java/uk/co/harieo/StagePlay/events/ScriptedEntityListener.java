package uk.co.harieo.StagePlay.events;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;

import net.minecraft.server.v1_12_R1.Entity;
import net.minecraft.server.v1_12_R1.EntityInsentient;
import uk.co.harieo.FurBridge.rank.Rank;
import uk.co.harieo.FurCore.ranks.RankCache;
import uk.co.harieo.StagePlay.entities.ScriptedEntity;
import uk.co.harieo.StagePlay.scripts.StagedScript;

public class ScriptedEntityListener implements Listener {

	/**
	 * Prevents any scripted entity from being damaged
	 */
	@EventHandler
	public void onEntityDamage(EntityDamageEvent event) {
		Entity entity = ((CraftEntity) event.getEntity()).getHandle();
		if (entity instanceof EntityInsentient && ScriptedEntity.isScripted((EntityInsentient) entity)) {
			event.setCancelled(true);
		}
	}

	/**
	 * In the event a scripted entity is killed by an unnatural source, remove it from the list of living scripted
	 * entities
	 */
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

	/**
	 * Sends a message to all administrators when a script has been executed
	 */
	@EventHandler
	public void onScriptExecute(ScriptExecuteEvent event) {
		StagedScript script = event.getScript();
		Bukkit.getOnlinePlayers().forEach(player -> {
			if (RankCache.getCachedInfo(player).hasPermission(Rank.ADMINISTRATOR)) {
				player.sendMessage(ChatColor.GRAY + "The " + ChatColor.YELLOW + script.getScriptName() + ChatColor.GRAY
						+ " script has been executed by " + ChatColor.GREEN + event.getExecutingPlayer().getName());
			}
		});
	}

}
