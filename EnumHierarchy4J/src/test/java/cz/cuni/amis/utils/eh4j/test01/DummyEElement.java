package cz.cuni.amis.utils.eh4j.test01;

import cz.cuni.amis.utils.eh4j.AsEnumObject;
import cz.cuni.amis.utils.eh4j.Enums;

public enum DummyEElement {

	ROOM,
	CORRIDOR,	
	@AsEnumObject(childClass = DummyEEntity.class)
	ENTITY;
	
	static {
		Enums.getInstance().registerEnumClass(DummyEElement.class);
	}
	
}
