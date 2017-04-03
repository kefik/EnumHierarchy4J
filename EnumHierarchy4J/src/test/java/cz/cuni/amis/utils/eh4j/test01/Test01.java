package cz.cuni.amis.utils.eh4j.test01;

import java.util.HashSet;
import java.util.Set;

import junit.framework.Assert;

import org.junit.Test;

import cz.cuni.amis.utils.eh4j.shortcut.EH;

public class Test01 {

	static {
		DummyEElement element = DummyEElement.ROOM;
		DummyEEntity entity = DummyEEntity.FEATURE;
	}
	
	@Test
	public void test01() {
		Assert.assertNotNull(EH.type(DummyEElement.ROOM));
		Assert.assertNotNull(EH.type(DummyEElement.CORRIDOR));
		Assert.assertNotNull(EH.type(DummyEElement.ENTITY));
	}
	
	@Test
	public void test02() {
		Assert.assertNotNull(EH.type(DummyEElement.class));
		Assert.assertNotNull(EH.type(DummyEEntity.class));
	}
	
	@Test
	public void test03() {
		Set<DummyEElement> elements1 = new HashSet<DummyEElement>();
		for (DummyEElement element : DummyEElement.values()) {
			elements1.add(element);
		}
		Set<DummyEElement> elements2 = new HashSet<DummyEElement>();
		elements2.addAll(elements1); 
		
		for (Object element : EH.type(DummyEElement.class).enums()) {
			if (!elements1.contains(element)) {
				Assert.fail("EH.type(EElement.class) does not contain " + element);
			}
			elements2.remove(element);
		}
		if (!elements2.isEmpty()) Assert.fail("EH.type(EElement.class) does not contain all enums from EElement enum.");
	}
	
	@Test
	public void test04() {
		Set<Enum> elements1 = new HashSet<Enum>();
		for (DummyEElement element : DummyEElement.values()) {
			elements1.add(element);
		}
		for (DummyEEntity element : DummyEEntity.values()) {
			elements1.add(element);
		}
		Set<Enum> elements2 = new HashSet<Enum>();
		elements2.addAll(elements1); 
		
		for (Object element : EH.type(DummyEElement.class).enumsAll()) {
			if (!elements1.contains(element)) {
				Assert.fail("EH.type(EElement.class) does not contain " + element);
			}
			elements2.remove(element);
		}
		if (!elements2.isEmpty()) Assert.fail("EH.type(EElement.class) does not contain all enums from EElement and EEntity enum.");
	}
	
	public static void main(String[] args) {
		Test01 test = new Test01();
		
		test.test04();
	}
	
	
}
