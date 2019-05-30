package uk.co.harieo.StagePlay.utils;

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

}
