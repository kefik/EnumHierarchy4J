package cz.cuni.amis.utils.eh4j;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation marking some object to represent an enumerable object.
 * @author Jimmy
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value={ ElementType.FIELD })
public @interface AsEnumObject {
	
	/**
	 * Class of the enum we're registering for; if not specified, object's own class is used as enumClass.
	 * @return
	 */
	Class enumClass() default Object.class;
	
	/**
	 * To be used if enum object represents another enum class. 
	 * @return
	 */
	Class childClass() default Object.class;	

}
