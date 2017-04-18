package cz.cuni.amis.utils.eh4j;

import cz.cuni.amis.utils.eh4j.shortcut.EH;

public class EnumObject {
	
	public final String name;
	public final Object enumInstance;
	public final EnumType type;	
	public final EnumType childType; 

	public EnumObject(String name, Object enumInstance, EnumType type, EnumType childType) {
		this.name = name;
		this.enumInstance = enumInstance;
		this.type = type;
		this.childType = childType;
	}
	
	@Override
	public int hashCode() {
		return enumInstance.hashCode();
	}
	
	public boolean isExactly(EnumType enumType) {
		return type.isExactly(enumType);
	}
	
	public boolean isExactly(Class enumClass) {
		return type.isExactly(enumClass);		
	}
	
	public boolean isA(EnumType enumType) {
		return type.isA(enumType);
	}
	
	public boolean isA(Class enumClass) {
		return type.isA(enumClass);		
	}
	
	public <T> T getAs(Class<T> enumClass) {
		if (type.isExactly(enumClass)) return (T) enumInstance;
		return (T) type.getAs(enumClass).enumInstance;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (!(obj instanceof EnumObject)) return false;
		return enumInstance == ((EnumObject)obj).enumInstance;
	}
	
	@Override
	public String toString() {
		return "EnumObject[" + name + "," + type + "]";
	}

}
