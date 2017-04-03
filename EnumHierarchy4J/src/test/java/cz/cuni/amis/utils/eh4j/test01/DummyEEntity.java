package cz.cuni.amis.utils.eh4j.test01;

import cz.cuni.amis.utils.eh4j.Enums;

public enum DummyEEntity {

	MONSTER,
	HERO,
	FEATURE;
	
	static {
		Enums.getInstance().registerEnumClass(DummyEEntity.class);
	}
	
}
