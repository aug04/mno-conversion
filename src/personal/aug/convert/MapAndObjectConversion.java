package personal.aug.convert;

import java.util.Map;

import personal.aug.convert.annotations.Processing;

public abstract class MapAndObjectConversion {

	private Processing<MapAndObjectConversion> processing;
	
	public MapAndObjectConversion() {
		this.processing = new Processing<>();
	}
	
	public Map<Object, Object> toMap() throws Exception {
		return processing.toMap(this.getClass(), this);
	}
	
	public MapAndObjectConversion fromMap(Map<Object, Object> map) throws Exception {
		return processing.fromMap(map, this);
	}
}
