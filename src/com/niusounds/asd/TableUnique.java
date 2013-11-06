package com.niusounds.asd;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface TableUnique {
	String[] indexedColumn();

	Conflict conflict() default Conflict.NONE;
}
