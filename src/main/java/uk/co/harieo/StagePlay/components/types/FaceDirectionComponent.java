package uk.co.harieo.StagePlay.components.types;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import uk.co.harieo.StagePlay.components.StageComponent;
import uk.co.harieo.StagePlay.components.types.FaceDirectionComponent.Facing;
import uk.co.harieo.StagePlay.scripts.StageActions;

public class FaceDirectionComponent extends StageComponent<Facing> {

	@Override
	public Facing parseElement(JsonElement element) {
		String[] stringDirection = element.getAsString().split(":");
		return new Facing(Float.parseFloat(stringDirection[0]), Float.parseFloat(stringDirection[1]));
	}

	@Override
	public void addToJson(StageActions action, JsonObject object) {
		object.addProperty(action.name(), getValue().getYaw() + ":" + getValue().getPitch());
	}

	public static class Facing {
		private float yaw;
		private float pitch;

		public Facing(float yaw, float pitch) {
			this.yaw = yaw;
			this.pitch = pitch;
		}

		public float getPitch() {
			return pitch;
		}

		public float getYaw() {
			return yaw;
		}

		public void setPitch(float pitch) {
			this.pitch = pitch;
		}

		public void setYaw(float yaw) {
			this.yaw = yaw;
		}

		@Override
		public String toString() {
			return "Yaw:" + yaw + ", Pitch:" + pitch;
		}
	}

}
