package personal.aug.convert.annotations;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import personal.aug.convert.MapAndObjectConversion;

public class Processing<T extends MapAndObjectConversion> {

	@SuppressWarnings("unchecked")
	public Map<Object, Object> toMap(Class<?> clazz, Object instance) throws Exception {
		Map<Object, Object> result = null;

		if (clazz != null && instance != null && (instance.getClass() == clazz)) {
			result = new HashMap<>();

			if (instance instanceof Map<?, ?>) {
				System.out.println("GO TO HERE");
				Map<Object, Object> instanceMap = (Map<Object, Object>) instance;
				for (Map.Entry<Object, Object> entry : instanceMap.entrySet()) {
					if (entry != null) {
						Object value = entry.getValue();
						if (!value.getClass().getName().startsWith("java.lang")) {
							instanceMap.put(entry.getKey(), toMap(entry.getValue().getClass(), entry.getValue()));
						}
					}
				}

				result = instanceMap;
			} else {
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
								if (_key != null && !_key.isEmpty())
									key = _key;
							}
						}

						try {
							final String className = value.getClass().getName();
							if (!className.startsWith("java.lang.")) {
								if (value instanceof Map<?, ?>) {
									// do nothing
								} else if (value instanceof Iterable<?>) {
									Iterable<?> iterable = (Iterable<?>) value;
									if (iterable != null && iterable.iterator() != null) {
										List<Object> listValue = new ArrayList<>();
										Iterator<?> it = iterable.iterator();
										while (it.hasNext()) {
											Object obj = it.next();
											listValue.add(toMap(obj.getClass(), obj));
										}

										value = listValue;
									}
								} else if (value.getClass().isArray()) {
									Object[] arrayValue = (Object[]) value;
									if (arrayValue != null && arrayValue.length > 0) {
										List<Object> listValue = new ArrayList<>();
										for (int i = 0; i < arrayValue.length; i++) {
											Object obj = arrayValue[i];
											listValue.add(toMap(obj.getClass(), obj));
										}

										value = listValue;
									}
								} else if (value instanceof java.util.Date) {
									value = ((java.util.Date) value).getTime();
								} else if (value instanceof java.sql.Date) {
									value = ((java.sql.Date) value).getTime();
								} else if (value instanceof java.sql.Timestamp) {
									value = ((java.sql.Timestamp) value).getTime();
								} else if (value instanceof java.sql.Time) {
									value = ((java.sql.Time) value).getTime();
								} else {
									Field[] valueFields = value.getClass().getDeclaredFields();
									if (valueFields != null && valueFields.length > 0) {
										value = toMap(value.getClass(), value);
									}
								}
							}
						} catch (Exception e) {
							// ignored
						}

						result.put(key, value);
					}
				}
			}
		}

		return result;
	}

	/**
	 * 
	 * @param map      is holder value
	 * @param instance is object keep value after convert complete
	 * @return generic object
	 * @throws Exception when convert error
	 * @author AUG
	 */
	public T fromMap(Map<Object, Object> map, T instance) throws Exception {
		if (instance != null) {
			Field[] fields = instance.getClass().getDeclaredFields();
			if (fields.length > 0) {
				for (Field field : fields) {
					field.setAccessible(true);
					String key = field.getName();
					if (key.equals("serialVersionUID"))
						continue;

					Annotation[] annotations = field.getAnnotations();
					for (Annotation ann : annotations) {
						if (ann instanceof MapKey) {
							String _key = ((MapKey) ann).value();
							if (_key != null && !_key.isEmpty())
								key = _key;
						}
					}

					Object value = map.containsKey(key) ? map.get(key) : null;
					setFieldValue(instance, field, value);
				}
			}
		}

		return instance;
	}

	private void setFieldValue(Object obj, Field field, Object value) throws Exception {
		final String simpleName = field.getType().getSimpleName();
		if (value == null) {
			value = getPrimitiveValue(simpleName);

			field.set(obj, value);
			return;
		}

		try {
			switch (simpleName.toLowerCase()) {
			case "long":
				value = Long.valueOf(value.toString());
				break;
			case "double":
				value = Double.valueOf(value.toString());
				break;
			case "short":
				value = Short.valueOf(value.toString());
				break;
			case "integer":
				value = Integer.valueOf(value.toString());
				break;
			case "float":
				value = Float.valueOf(value.toString());
				break;
			case "boolean":
				value = Boolean.valueOf(value.toString());
				break;
			}

			field.set(obj, value);
		} catch (Exception e) {
			value = getPrimitiveValue(simpleName);
			field.set(obj, value);
		}
	}

	private Object getPrimitiveValue(String simpleName) {
		Object value = null;
		if ("short".equals(simpleName))
			value = Integer.valueOf("0").shortValue();
		else if ("int".equals(simpleName))
			value = Integer.valueOf("0").intValue();
		else if ("float".equals(simpleName))
			value = Integer.valueOf("0").floatValue();
		else if ("double".equals(simpleName))
			value = Integer.valueOf("0").doubleValue();
		else if ("long".equals(simpleName))
			value = Integer.valueOf("0").longValue();
		else if ("boolean".equals(simpleName))
			value = false;

		return value;
	}
}
