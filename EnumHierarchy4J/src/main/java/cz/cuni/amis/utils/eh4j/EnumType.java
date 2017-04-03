package cz.cuni.amis.utils.eh4j;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Parallel to Java "enum" that cannot extend another enum; a.k.a. EnumClass.
 * 
 * Here we store catalog of ALL enum-instances ({@link IEnumObject}, implementated by {@link AsEnumObject}) that is implementing
 * or inheriting some {@link EnumChildClass} annotated class or interface.
 * 
 * @author Jimmy
 */
public class EnumType {
	
	private String name;
	
	private Class enumClass;
	
	private AsEnumClass enumClassAnnot;

	private Map<String, EnumObject> allEnumObjects = new HashMap<String, EnumObject>();
	
	private Map<String, EnumObject> ownEnumObjects = new HashMap<String, EnumObject>();
	
	private Map<String, Object> allEnums = new HashMap<String, Object>();
	
	private Map<String, Object> ownEnums = new HashMap<String, Object>();
	
	/**
	 * KEY   == own enum
	 * VALUE == enum class it represents
	 */
	private Map<EnumObject, EnumType> enum2ChildClass = new HashMap<EnumObject, EnumType>();
	private Map<EnumType, EnumObject> childClass2Enum = new HashMap<EnumType, EnumObject>();
	
	/**
	 * KEY   == enum within parent class representing THIS enum type
	 * VALUE == parent enum class
	 */
	private Map<Object, EnumType> enum2ParentClass = new HashMap<Object, EnumType>();
	
	/**
	 * Enum represented by this EnumType is extending following class.
	 */
	private Map<Class, EnumType> extending = new HashMap<Class, EnumType>(); 
		

		
	public EnumType(Class enumClass) {
		this.enumClass = enumClass;	
		this.enumClassAnnot = (AsEnumClass) enumClass.getAnnotation(AsEnumClass.class);
		this.name = (enumClassAnnot == null || enumClassAnnot.name().length() == 0 ? enumClass.getSimpleName() : enumClassAnnot.name()); 
	}
	
	// ============================
	// MIMICKING ENUM FUNCTIONALITY
	// ============================
	
	public String getName() {
		return name;
	}
	
	public Collection<Object> enums() {
		return ownEnums.values();
	}
	
	public <T> Collection<T> enums(Class<T> type) {
		return (Collection<T>) ownEnums.values();
	}
	
	public Object getEnum(String name) {
		return ownEnums.get(name);
	}
	
	public Collection<Object> enumsAll() {
		return allEnums.values();
	}
	
	public <T> Collection<? extends T> enumsAll(Class<T> type) {
		return (Collection<? extends T>) allEnums.values();
	}
	
	public Object getEnumAny(String name) {
		return allEnums.get(name);
	}
	
	// ============
	// ENUM OBJECTS
	// ============
	
	public Collection<EnumObject> enumObjects() {
		return ownEnumObjects.values();
	}
	
	public EnumObject enumObject(String name) {
		return ownEnumObjects.get(name);
	}
	
	public Collection<EnumObject> enumObjectsAll() {
		return allEnumObjects.values();
	}
	
	public EnumObject enumObjectAny(String name) {
		return allEnumObjects.get(name);
	}
	
	// =========================
	// ENUM HIERARCHY REFLECTION
	// =========================
	
	/**
	 * Is THIS ENUM TYPE the same type or children of 'enumClass'?
	 * @param enumClass
	 * @return
	 */
	public boolean isA(Class enumClass) {
		if (enumClass == null) return false;
		if (enumClass == this.enumClass) return true;
		for (EnumType parent : extending.values()) {
			return isA(enumClass);
		}
		return false;
	}
	
	/**
	 * Is THIS ENUM TYPE represented by enumInstance or is CHILD type of enumInstance CHILD ENUM TYPE?
	 * @param enumInstance
	 * @return
	 */
	public boolean isA(Object enumInstance) {
		EnumObject enumObject = Enums.getInstance().getEnumObject(enumInstance);
		if (enumObject.type == this) return false;
		return isA(enumObject.childType);
	}
	
	// =================
	// REGISTERING STUFF
	// =================

	protected void registerChildEnum(EnumObject enumObject) {
		if (allEnumObjects.containsKey(enumObject.name)) {
			if (allEnumObjects.get(enumObject.name) == enumObject) return;
			throw new RuntimeException("Enum object name clash for '" + enumObject.name + "'! Cannot register " + enumObject + " as there is already registered enum object " + allEnumObjects.get(enumObject.name) + ".");
		}
		
		allEnums.put(enumObject.name, enumObject.value);
		allEnumObjects.put(enumObject.name, enumObject);
		
		// RECURSIVELY REGISTER TO PARENTS AS WELL...
		for (EnumType parent : extending.values()) {
			parent.registerChildEnum(enumObject);
		}
	}
	
	protected void registerOwnEnum(EnumObject enumObject) {
		if (ownEnumObjects.containsValue(enumObject)) return;
				
		// REGISTER OBJECT		
		registerChildEnum(enumObject);
		ownEnums.put(enumObject.name, enumObject.value);
		ownEnumObjects.put(enumObject.name, enumObject);		
		
		// REGISTER CHILD CLASS
		registerChildClass(enumObject);
	}
	
	protected void registerChildClass(EnumObject enumObject) {
		registerOwnEnum(enumObject);
		if (enumObject.childType == null) return;
		enum2ChildClass.put(enumObject, enumObject.childType);
		
		// REGISTER ALL ENUMS FROM CHILD CLASS
		for (EnumObject obj : enumObject.childType.allEnumObjects.values()) {
			registerChildEnum(obj);
		}
		
		// REGISTER AS PARENT CLASS
		enumObject.childType.registerParentClass(enumObject, this);
	}
	
	protected void registerParentClass(EnumObject enumObject, EnumType enumParentType) {
		enum2ParentClass.put(enumObject, enumParentType);
	}
	
	// =====
	// DEBUG
	// =====
	
	@Override
	public String toString() {
		return "EnumType[" + getName() + ", #all=" + allEnumObjects.size() + ", #own=" + ownEnumObjects.size() + "]";
	}
	
}
