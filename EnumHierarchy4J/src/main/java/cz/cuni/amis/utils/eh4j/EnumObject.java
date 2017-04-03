package cz.cuni.amis.utils.eh4j;

public class EnumObject {
	
	public final String name;
	public final Object value;
	public final EnumType type;	
	public final EnumType childType; 

	public EnumObject(String name, Object value, EnumType type, EnumType childType) {
		this.name = name;
		this.value = value;
		this.type = type;
		this.childType = childType;
	}
	
	@Override
	public int hashCode() {
		return value.hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (!(obj instanceof EnumObject)) return false;
		return value == ((EnumObject)obj).value;
	}
	
	@Override
	public String toString() {
		return "EnumObject[" + name + "," + type + "]";
	}

}
