package uk.co.harieo.StagePlay.utils;

import org.bukkit.Location;

import java.lang.reflect.Field;

public class Utils {

	/**
	 * Retrieves private fields from the given class
	 *
	 * @param fieldName to be retrieved
	 * @param clazz to get the field from
	 * @param object instance of the field
	 * @return the field value
	 * @author XLordalX
	 */
	public static Object getPrivateField(String fieldName, Class clazz, Object object) {
		Field field;
		Object o = null;

		try {
			field = clazz.getDeclaredField(fieldName);
			field.setAccessible(true);
			o = field.get(object);
		} catch (NoSuchFieldException | IllegalAccessException e) {
			e.printStackTrace();
		}

		return o;
	}

	/**
	 * Converts a {@link Location} to a more human-readable version without Yaw/Pitch due to them being separated in
	 * this plugin
	 *
	 * @param location to be converted
	 * @return an easier to read String of the location
	 */
	public static String convertLocationToString(Location location) {
		return "X:" + location.getX() + ", Y:" + location.getY() + ", Z:" + location.getZ();
	}

}
