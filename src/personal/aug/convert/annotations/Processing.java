package personal.aug.convert.annotations;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import personal.aug.convert.MapAndObjectConversion;

public class Processing<T extends MapAndObjectConversion> {

	public Map<Object, Object> toMap(Class<?> clazz, Object instance) throws Exception {
		Map<Object, Object> result = null;
		
		if (clazz != null 
				&& instance != null 
				&& (instance.getClass() == clazz)) {
			result = new HashMap<>();
			
			Field[] fields = clazz.getDeclaredFields();
			if (fields.length > 0) {
				for (Field field : fields) {
					field.setAccessible(true);
					Object value = field.get(instance);
					String key = field.getName();
					
					Annotation[] annotations = field.getAnnotations();
					for (Annotation ann : annotations) {
						if (ann instanceof MapKey) {
							String _key = ((MapKey) ann).value();
							if (_key != null && !_key.isEmpty()) key = _key;
						}
					}
					
					try {
						final String className = value.getClass().getName();
						if (!className.startsWith("java.lang.")) {
							Field[] valueFields = value.getClass().getDeclaredFields();
							if (valueFields != null && valueFields.length > 0) {
								value = toMap(value.getClass(), value);
							}
						}
					} catch (Exception e) {
						// ignored
					}
					
					result.put(key, value);
				}
			}
		}
		
		return result;
	}
	
	public T fromMap(Map<Object, Object> map, T instance) throws Exception {
		
		if (instance != null) {
			Field[] fields = instance.getClass().getDeclaredFields();
			if (fields.length > 0) {
				for (Field field : fields) {
					field.setAccessible(true);
					String key = field.getName();
					if (key.equals("serialVersionUID")) continue;
					
					Annotation[] annotations = field.getAnnotations();
					for (Annotation ann : annotations) {
						if (ann instanceof MapKey) {
							String _key = ((MapKey) ann).value();
							if (_key != null && !_key.isEmpty()) key = _key;
						}
					}
					
					Object value = map.containsKey(key) ? map.get(key) : null;
					field.set(instance, value);
				}
			}
		}
		
		return instance;
	}
}
