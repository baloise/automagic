package groovy.transform

import java.lang.annotation.ElementType
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.lang.annotation.Target

/**
 * see https://github.com/groovy/groovy-eclipse/issues/1235#issuecomment-805660498
 */
@Target([ElementType.METHOD, ElementType.CONSTRUCTOR, ElementType.TYPE, ElementType.FIELD])
@Retention(RetentionPolicy.RUNTIME)
@interface Generated {
	// empty
}