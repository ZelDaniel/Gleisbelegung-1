package org.gleisbelegung.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Marker that the annotated element is thread-safe.
 */
@Target({
        ElementType.METHOD,
        ElementType.CONSTRUCTOR,
        ElementType.TYPE,
})
@Documented
@Retention(value=RetentionPolicy.RUNTIME)
public @interface Threadsafe {
}
