package personal.aug.convert;

import java.util.Map;

import personal.aug.convert.annotations.Processing;

public abstract class MapAndObjectConversion {

	private Processing processing;
	
	public MapAndObjectConversion() {
		this.processing = new Processing();
	}
	
	public Map<Object, Object> toMap() throws Exception {
		return processing.toMap(this.getClass(), this);
	}
	
	public void fromMap(Map<Object, Object> map) throws Exception {
		processing.fromMap(map, this);
	}
}
