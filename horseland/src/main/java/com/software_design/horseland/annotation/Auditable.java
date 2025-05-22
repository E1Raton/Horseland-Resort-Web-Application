package com.software_design.horseland.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// what "elements" can be annotated with this
@Target({ElementType.METHOD, ElementType.TYPE})

// availability: via reflection during execution
// makes the annotation visible to your AOP framework (or any
// reflection-based code) at runtime, so AuditAspect can detect
// and apply logging logic
@Retention(RetentionPolicy.RUNTIME)
public @interface Auditable {
    /** the operation performed (e.g. CREATE, UPDATE, ...) */
    String operation() default "";

    /** the entity on which the operation was performed */
    Class<?> entity() default Void.class;

    /** the username of the user responsible for the action */
    String username() default "";
}