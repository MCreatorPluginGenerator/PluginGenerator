package org.cdc.framework.annotaion;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * &#064;Developer  user
 * &#064;CreatedIn  2026/9/9
 */
@Retention(RetentionPolicy.SOURCE)
@Target({ ElementType.TYPE})
public @interface AIGenerated {
    String type() default "DeepSeek-V4.1";
}
