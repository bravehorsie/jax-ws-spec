/*
 * Copyright 2007 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.xml.ws.soap;

import java.lang.annotation.Documented;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.xml.ws.spi.WebServiceFeatureAnnotation;
import javax.xml.ws.WebServiceRef;
import javax.xml.ws.WebServiceRefs;
import javax.xml.ws.WebServiceProvider;

/**
 * This feature represents the use of MTOM with a 
 * web service.
 * <p>
 * This annotation MUST only be used in conjunction the
 * <code>javax.jws.WebService</code>, {@link WebServiceProvider},
 * {@link WebServiceRef} annotations.
 * When used with the <code>javax.jws.WebService</code> annotation this
 * annotation MUST only be used on the service endpoint implementation
 * class.
 * When used with a <code>WebServiceRef</code> annotation, this annotation
 * MUST only be used when a proxy instance is created. The injected SEI
 * proxy, and endpoint MUST honor the values of the <code>MTOM</code>
 * annotation.
 * <p>
 * The following describes the affects of this feature with respect
 * to being enabled or disabled:
 * <ul>
 *  <li> ENABLED: In this Mode, MTOM will be enabled.
 *  <li> DISABLED: In this Mode, MTOM will be disabled
 * </ul>
 * <p>
 * The {@link #threshold} property can be used to set the threshold 
 * value used to determine when binary data should be XOP encoded.
 *
 * @since JAX-WS 2.1
 */   
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@WebServiceFeatureAnnotation(id=MTOMFeature.ID,bean=MTOMFeature.class)
public @interface MTOM {
    /**
     * Specifies if this feature is enabled or disabled.
     */
    boolean enabled() default true; 
     
    /**
     * Property for MTOM threshold value. When MTOM is enabled, binary data above this 
     * size in bytes will be XOP encoded or sent as attachment. The value of this property 
     * MUST always be >= 0. Default value is 0.      
     */         
    int threshold() default 0;
}
