package uk.co.harieo.StagePlay.entities;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;

import java.util.LinkedHashSet;
import net.minecraft.server.v1_12_R1.EntityVillager;
import net.minecraft.server.v1_12_R1.PathfinderGoalSelector;
import uk.co.harieo.StagePlay.utils.Utils;

public class ScriptedVillager extends EntityVillager {

	public ScriptedVillager(World world) {
		super(((CraftWorld) world).getHandle());

		LinkedHashSet goalB = (LinkedHashSet) Utils.getPrivateField("b", PathfinderGoalSelector.class, goalSelector);
		goalB.clear();
		LinkedHashSet goalC = (LinkedHashSet) Utils.getPrivateField("c", PathfinderGoalSelector.class, goalSelector);
		goalC.clear();
		LinkedHashSet targetB = (LinkedHashSet) Utils.getPrivateField("b", PathfinderGoalSelector.class, targetSelector);
		targetB.clear();
		LinkedHashSet targetC = (LinkedHashSet) Utils.getPrivateField("c", PathfinderGoalSelector.class, targetSelector);
		targetC.clear();

		goalSelector.a(0, new PathfinderGoalWalkTo(this, new Location(world, -37, 65, -314)));
	}

}
