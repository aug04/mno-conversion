package personal.aug.convert;

import java.util.Map;

import personal.aug.convert.annotations.Processing;

public abstract class MapAndObjectConversion {

	private Processing<MapAndObjectConversion> processing;
	
	public MapAndObjectConversion() {
		this.processing = new Processing<>();
	}
	
	/**
	 * Convert the instance to a Map.
	 * 
	 * @return a Map if convert success, else return null.
	 * @author AUG
	 */
	public Map<Object, Object> toMap() {
		try {
			return processing.toMap(this.getClass(), this);
		} catch (Exception e) {
			// ignored
			e.printStackTrace();
		}
		
		return null;
	}
	
	/**
	 * Convert a Map to an this instance.
	 * 
	 * @param map is data to convert.
	 * @param <T> is generic type.
	 * @return an instance keep values from the map, else return null.
	 * @author AUG
	 */
	@SuppressWarnings("unchecked")
	public <T extends MapAndObjectConversion> T fromMap(Map<Object, Object> map) {
		try {
			return (T) processing.fromMap(map, this);
		} catch (Exception e) {
			// ignored
			e.printStackTrace();
		}
		
		return null;
	}
}
