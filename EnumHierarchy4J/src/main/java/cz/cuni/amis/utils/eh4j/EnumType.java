package cz.cuni.amis.utils.eh4j;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.swing.text.html.parser.Entity;

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
	private Map<EnumObject, EnumType> enum2ParentClass = new HashMap<EnumObject, EnumType>();
	
	/**
	 * KEY   == parent enum class
	 * VALUE == enum within parent class representing THIS enum type
	 */
	private Map<EnumType, EnumObject> parentClass2Enum = new HashMap<EnumType, EnumObject>();
	
	/**
	 * KEY   == enum within parent class representing THIS enum type
	 * VALUE == parent enum class
	 * 
	 * CACHE - contains all parent classes in here
	 */
	private Map<EnumObject, EnumType> enum2ParentClassAll = new HashMap<EnumObject, EnumType>();
	
	/**
	 * KEY   == parent enum class
	 * VALUE == enum within parent class representing THIS enum type
	 * 
	 * CACHE - contains all parent classes in here
	 */
	private Map<EnumType, EnumObject> parentClass2EnumAll = new HashMap<EnumType, EnumObject>();
	
	
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
	 * enumType SHOULD BE a parent of this type ... we try to find this.
	 * @param enumType
	 * @return
	 */
	protected EnumObject findParentOf(EnumType enumType) {
		if (this == enumType) return null;
		EnumObject result = parentClass2EnumAll.get(enumType);
		if (result != null) return result;	
		result = parentClass2Enum.get(enumType);
		if (result != null) {
			parentClass2EnumAll.put(enumType, result);
			return result;
		}
		for (EnumType parentType : parentClass2Enum.keySet()) {
			result = parentType.findParentOf(enumType);
			if (result != null) {
				parentClass2EnumAll.put(enumType, result);
				return result;
			}			
		}	
		return null;
	}
	
	/**
	 * enumClass SHOULD BE a parent of this type ... we try to find this.
	 * @param enumClass
	 * @return
	 */
	protected EnumObject findParentOf(Class enumClass) {
		EnumType enumType = Enums.getInstance().getEnumType(enumClass);
		return findParentOf(enumType);		
	}
	
	/**
	 * Is THIS ENUM TYPE the same type of 'enumClass'?
	 * @param enumClass
	 * @return
	 */
	public boolean isExactly(Class enumClass) {
		if (enumClass == null) return false;
		return enumClass == this.enumClass;
	}
	
	/**
	 * Is THIS ENUM TYPE represented by enumType?
	 * @param enumType
	 * @return
	 */
	public boolean isExactly(EnumType enumType) {
		if (enumType == null) return false;
		return this == enumType;
	}
	
	/**
	 * Is THIS ENUM TYPE the same type or children of 'enumClass'?
	 * @param enumClass
	 * @return
	 */
	public boolean isA(Class enumClass) {
		if (enumClass == null) return false;
		if (enumClass == this.enumClass) return true;
		EnumType enumType = Enums.getInstance().getEnumType(enumClass);
		findParentOf(enumType); // populates parentClass2EnumAll
		return parentClass2EnumAll.containsKey(enumType);
	}
	
	/**
	 * Is THIS ENUM TYPE represented by enumInstance or is CHILD type of enumInstance CHILD ENUM TYPE?
	 * @param enumInstance
	 * @return
	 */
	public boolean isA(EnumType enumType) {
		if (enumType == null) return false;
		if (this == enumType) return true;
		findParentOf(enumType); // populates parentClass2EnumAll
		return parentClass2EnumAll.containsKey(enumType);
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
	
	/**
	 * 'enumClass' should be parent of this {@link EnumType} and we return instance of EnumObject
	 * that is representing THIS ENUM TYPE within 'enumClass'. 
	 * @param enumClass
	 * @return
	 */
	public EnumObject getAs(Class enumClass) {
		EnumType enumType = Enums.getInstance().getEnumType(enumClass);
		if (enumClass == null) return null;
		findParentOf(enumType); // populates parentClass2EnumAll
		return parentClass2EnumAll.get(enumType);
	}
	
	// =================
	// REGISTERING STUFF
	// =================

	protected void registerChildEnum(EnumObject enumObject) {
		if (allEnumObjects.containsKey(enumObject.name)) {
			if (allEnumObjects.get(enumObject.name) == enumObject) return;
			throw new RuntimeException("Enum object name clash for '" + enumObject.name + "'! Cannot register " + enumObject + " as there is already registered enum object " + allEnumObjects.get(enumObject.name) + ".");
		}
		
		allEnums.put(enumObject.name, enumObject.enumInstance);
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
		ownEnums.put(enumObject.name, enumObject.enumInstance);
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
		parentClass2Enum.put(enumParentType, enumObject);
	}
	
	// =====
	// DEBUG
	// =====
	
	@Override
	public String toString() {
		return "EnumType[" + getName() + ", #all=" + allEnumObjects.size() + ", #own=" + ownEnumObjects.size() + "]";
	}
	
}
