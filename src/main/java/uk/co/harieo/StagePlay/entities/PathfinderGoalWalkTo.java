package uk.co.harieo.StagePlay.entities;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import net.minecraft.server.v1_12_R1.EntityInsentient;
import net.minecraft.server.v1_12_R1.NavigationAbstract;
import net.minecraft.server.v1_12_R1.PathEntity;
import net.minecraft.server.v1_12_R1.PathfinderGoal;

public class PathfinderGoalWalkTo extends PathfinderGoal {

	private EntityInsentient entity;
	private Location destination;
	private NavigationAbstract navigation;

	public PathfinderGoalWalkTo(EntityInsentient entity, Location destination) {
		this.entity = entity;
		entity.onGround = true; // Fixes NMS bug with spawning an entity in this manner
		this.destination = destination;
		this.navigation = this.entity.getNavigation();
	}

	@Override
	public boolean a() {
		return true;
	}

	@Override
	public void c() {
		Bukkit.broadcastMessage("X:" + destination.getX() + " Y:" + destination.getY() + " Z:" + destination.getZ());
		PathEntity pathEntity = navigation.a(destination.getBlockX(), destination.getBlockY() - 1, destination.getBlockZ());
		Bukkit.broadcastMessage("Path Entity is null: " + (pathEntity == null));
		Bukkit.broadcastMessage("Path Entity: " + navigation.a(pathEntity, 0.5));
	}

}
