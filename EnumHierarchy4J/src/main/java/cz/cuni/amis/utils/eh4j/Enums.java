package cz.cuni.amis.utils.eh4j;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class Enums {
	
	private static Object mutex = new Object();
	
	private static Enums instance;
	
	public static Enums getInstance() {
		if (instance == null) {
			synchronized(mutex) {
				if (instance == null) {
					return instance = new Enums();					
				}				
			}
			return instance;		
		}
		return instance;
	}
	
	/**
	 * Class => EnumType
	 */
	private Map<Class, EnumType> class2Type = new HashMap<Class, EnumType>();
	
	/**
	 * Enum object => EnumObject
	 */
	private Map<Object, EnumObject> object2Enum = new HashMap<Object, EnumObject>();	
	
	/**
	 * Enum instance name => EnumObject
	 */
	private Map<String, EnumObject> name2Object = new HashMap<String, EnumObject>();
	
	private void Enums() {		
	}
	
	/**
	 * Creates or returns {@link EnumType} representing 'enumClass'.
	 * @param enumClass
	 * @return
	 */
	public EnumType registerEnumClass(Class enumClass) {
		if (enumClass == null) return null;
		EnumType enumType = class2Type.get(enumClass);
		if (enumType == null) {
			synchronized(mutex) {
				enumType = class2Type.get(enumClass);
				if (enumType == null) {
					enumType = new EnumType(enumClass);
					class2Type.put(enumClass, enumType);
					registerEnums(enumClass);
				}				
			}
		}	
		return enumType;		
	}
	
	/**
	 * Reflectively iterate over static fields, locate {@link AsEnumObject}s automatically registering them... 
	 * Automatically creates {@link EnumType}s when needed.
	 * @param enumClass
	 */
	public void registerEnums(Class enumClass) {
		if (enumClass == null) return;
		try {
			for (Field field : enumClass.getFields()) {
				// PROBE ONLY STATIC FIELDS
				if (!java.lang.reflect.Modifier.isStatic(field.getModifiers())) continue;
				
				// PROBE ONLY PUBLIC FIELDS
				if (!java.lang.reflect.Modifier.isPublic(field.getModifiers())) continue;
				
				Object e = field.get(enumClass);
				if (e == null) {
					continue;
				}
				if (field.isAnnotationPresent(AsEnumObject.class)) {
					registerEnumObject(field.getName(), e, (AsEnumObject)field.getAnnotation(AsEnumObject.class));
				} else 
				if (e.getClass() == enumClass) {
					registerEnumObject(field.getName(), e, null);
				}
			}
		} catch (Exception e) {
			throw new RuntimeException("Failed to registerEnums for class " + enumClass, e);
		}
	}
	
	/**
	 * Register 'enumObject' under 'name'; enum class is automatically determined, either as its base class or from annotation.
	 * @param name
	 * @param enumInstance
	 */
	public void registerEnumObject(String name, Object enumInstance, AsEnumObject annotation) {
		Class enumClass = enumInstance.getClass();
		Class enumChildClass = null;
		
		if (annotation != null) {
			if (annotation.enumClass() != Object.class) {
				enumClass = annotation.getClass();
			}
			if (annotation.childClass() != Object.class) {
				enumChildClass = annotation.childClass();
			}
		}
		
		registerEnumObject(name, enumInstance, enumClass, enumChildClass);
	}

	/**
	 * Register 'enumObject' under key 'name' into 'enumClass'; if 'enumChildClass' is not null, then the object represents another enum-class, register child class as well...
	 * @param name
	 * @param enumInstance
	 * @param enumClass
	 * @param enumChildClass
	 */
	private void registerEnumObject(String name, Object enumInstance, Class enumClass, Class enumChildClass) {
		EnumType enumType      = registerEnumClass(enumClass);
		EnumType enumChildType = registerEnumClass(enumChildClass);
				
		EnumObject enumObject = object2Enum.get(enumInstance);
		if (enumObject == null) {
			synchronized(mutex) {
				enumObject = object2Enum.get(enumInstance);
				if (enumObject == null) {
					enumObject = new EnumObject(name, enumInstance, enumType, enumChildType);
					object2Enum.put(enumInstance, enumObject);
					if (name2Object.containsKey(enumObject.name)) {
						throw new RuntimeException("Enum object name clash for '" + enumObject.name + "'! Cannot register " + enumObject + " as there is already registered enum object " + name2Object.get(enumObject.name) + ".");
					}
					name2Object.put(enumObject.name, enumObject);
				}
			}
		}
		
		enumType.registerOwnEnum(enumObject);		
	}

	/**
	 * Returns {@link EnumType} for given 'enumClass'.
	 * @param enumClass
	 * @return
	 */
	public EnumType getEnumType(Class enumClass) {
		return class2Type.get(enumClass);
	}
	
	/**
	 * Returns {@link EnumObject} representing user 'enumInstanceName'.
	 * @param enumInstance
	 * @return
	 */
	public EnumObject getEnumObject(String enumInstanceName) {
		return name2Object.get(enumInstanceName);
	}
	
	/**
	 * Returns {@link EnumObject} representing user 'enumInstance'.
	 * @param enumInstance
	 * @return
	 */
	public EnumObject getEnumObject(Object enumInstance) {
		return object2Enum.get(enumInstance);
	}
	
	/**
	 * Returns {@link EnumType} for given user 'enumInstance'.
	 * @param enumInstance
	 * @return
	 */
	public EnumType getEnumType(Object enumInstance) {
		EnumObject obj = getEnumObject(enumInstance);
		if (obj == null) return null;
		return obj.type;
	}

}
