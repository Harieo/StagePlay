package uk.co.harieo.StagePlay.components.types;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import uk.co.harieo.StagePlay.components.StageComponent;
import uk.co.harieo.StagePlay.scripts.StageActions;

public class LocationComponent extends StageComponent<Location> {

	@Override
	public Location parseElement(JsonElement element) {
		String[] serializedLocation = element.getAsString().split(":");
		return new Location(Bukkit.getWorld(serializedLocation[0]),
				Double.parseDouble(serializedLocation[1]),
				Double.parseDouble(serializedLocation[2]),
				Double.parseDouble(serializedLocation[3]));
	}

	@Override
	public void addToJson(StageActions action, JsonObject object) {
		object.addProperty(action.name(),
				getValue().getWorld().getName() + ":" + getValue().getX() + ":" + getValue().getY() + ":"
						+ getValue().getZ());
	}

}
