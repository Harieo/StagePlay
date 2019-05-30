package uk.co.harieo.StagePlay.entities;

import org.bukkit.World;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;

import java.util.List;
import net.minecraft.server.v1_12_R1.EntityVillager;
import net.minecraft.server.v1_12_R1.PathfinderGoalSelector;
import uk.co.harieo.StagePlay.utils.Utils;

public class ScriptedVillager extends EntityVillager {

	public ScriptedVillager(World world) {
		super(((CraftWorld) world).getHandle());

		// Clear the usual villager goals
		List goalB = (List) Utils.getPrivateField("b", PathfinderGoalSelector.class, goalSelector);
		goalB.clear();
		List goalC = (List) Utils.getPrivateField("c", PathfinderGoalSelector.class, goalSelector);
		goalC.clear();
		List targetB = (List) Utils.getPrivateField("b", PathfinderGoalSelector.class, targetSelector);
		targetB.clear();
		List targetC = (List) Utils.getPrivateField("c", PathfinderGoalSelector.class, targetSelector);
		targetC.clear();
	}

}
