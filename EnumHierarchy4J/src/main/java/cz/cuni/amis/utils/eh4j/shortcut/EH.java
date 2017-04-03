package cz.cuni.amis.utils.eh4j.shortcut;

import java.util.Collection;

import cz.cuni.amis.utils.eh4j.EnumObject;
import cz.cuni.amis.utils.eh4j.EnumType;
import cz.cuni.amis.utils.eh4j.Enums;

/**
 * Shortcuts for quickly enumerating enums
 * @author Jimmy
 */
public class EH {

	/**
	 * Returns {@link EnumType} for given 'enumClass'.
	 * @param enumClass
	 * @return
	 */
	public static EnumType type(Class enumClass) {
		return Enums.getInstance().getEnumType(enumClass);
	}
	
	/**
	 * Returns {@link EnumType} for given user 'enumInstance'.
	 * @param enumInstance
	 * @return
	 */
	public static EnumType type(Object enumInstance) {
		return Enums.getInstance().getEnumType(enumInstance);
	}
	
	// ===========
	// TRANSLATION
	// ===========
	
	/**
	 * Returns {@link EnumObject} internally representing user 'enumInstance'.
	 * @param enumInstance
	 * @return
	 */
	public static EnumObject getEnumObject(Object enumInstance) {
		return Enums.getInstance().getEnumObject(enumInstance);
	}
	
	// ===================
	// USER ENUM INSTANCES
	// ===================
	
	/**
	 * Returns user enum instances defined for given 'enumClass'.
	 * @param enumClass
	 * @return
	 */
	public static <T> Collection<T> enums(Class<T> enumClass) {
		return type(enumClass).enums(enumClass);
	}
	
	/**
	 * Returns all (own+inherited) user enum instances defined for given 'enumClass'.
	 * @param enumClass
	 * @return
	 */
	public static <T> Collection<? extends T> enumsAll(Class<T> enumClass) {
		return type(enumClass).enumsAll(enumClass);
	}
	
	/**
	 * Returns user enum instances defined for given 'enumClass'.
	 * @param enumClass
	 * @param asEnums
	 * @return
	 */
	public static <T> Collection<T> enums(Class enumClass, Class<T> asEnums) {
		return type(enumClass).enums(asEnums);
	}
	
	/**
	 * Returns all (own+inherited) user enum instances defined for given 'enumClass'.
	 * @param enumClass
	 * @param asEnums
	 * @return
	 */
	public static <T> Collection<? extends T> enumsAll(Class enumClass, Class<T> asEnums) {
		return type(enumClass).enumsAll(asEnums);
	}
	
	// ============
	// ENUM OBJECTS
	// ============
	
	/**
	 * Returns internal {@link EnumObject} representation of user enum instances defined for given 'enumClass'.
	 * @param enumClass
	 * @return
	 */
	public static Collection<EnumObject> enumObjects(Class enumClass) {
		return type(enumClass).enumObjects();
	}
	
	/**
	 * Returns all (own+inherited) internal {@link EnumObject} representation of user enum instances defined for given 'enumClass'.
	 * @param enumClass
	 * @return
	 */
	public static Collection<EnumObject> enumObjectsAll(Class enumClass) {
		return type(enumClass).enumObjectsAll();
	}
	
	// ==============
	// ENUM HIERARCHY
	// ==============
	
	public static boolean isA(Object enumInstance, Object ofThisEnumInstance) {
		if (enumInstance == ofThisEnumInstance) return true;
		EnumType type = type(enumInstance);
		if (type == null) return false;
		return type.isA(ofThisEnumInstance);
	}
	
}
