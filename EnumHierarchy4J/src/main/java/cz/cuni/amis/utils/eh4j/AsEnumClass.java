package cz.cuni.amis.utils.eh4j;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation marking some class to be an enum class.
 * @author Jimmy
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value={ ElementType.TYPE })
public @interface AsEnumClass {
	
	String name() default "";

}
