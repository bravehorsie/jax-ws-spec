/*
 * Copyright 2007 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.xml.ws;

import javax.xml.ws.soap.Addressing;
import javax.xml.ws.spi.WebServiceFeatureAnnotation;
import java.lang.annotation.Documented;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * The <code>WebServiceRef</code> annotation is used to
 * define a reference to a web service and
 * (optionally) an injection target for it.
 *
 * Web service references are resources in the Java EE 5 sense.
 * <p>
 * {@link WebServiceFeatureAnnotation} annotations
 * (for example, {@link Addressing})
 * can be used in conjunction with <code>WebServiceRef</code>.
 * It has no affect when a <code>WebServiceRef</code> is used
 * to specify a generated service class. But when
 * it is used with a <code>WebServiceRef</code> that specifies
 * a service endpoint interface (SEI), the injected SEI proxy
 * MUST be configured with the annotation's web service feature.
 *
 * <p>
 * For example, in the code below, the injected 
 * <code>StockQuoteProvider</code> proxy MUST
 * have WS-Addressing enabled as specifed by the
 * {@link Addressing}
 * annotation.
 *
 * <code>
 * <pre>
 *    public class MyClient {
 *       &#64;Addressing
 *       &#64;WebServiceRef(StockQuoteService.class)
 *       private StockQuoteProvider stockQuoteProvider;
 *       ...
 *    }
 * </pre>
 * </code>
 * 
 * @see javax.annotation.Resource
 * @see WebServiceFeatureAnnotation
 *
 * @since JAX-WS 2.0
 *
**/

@Target({ElementType.TYPE, ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface WebServiceRef {
     /**
      * The JNDI name of the resource.  For field annotations,
      * the default is the field name.  For method annotations,
      * the default is the JavaBeans property name corresponding
      * to the method.  For class annotations, there is no default
      * and this MUST be specified.
      */
     String name() default "";

     /**
      * The Java type of the resource.  For field annotations,
      * the default is the type of the field.  For method annotations,
      * the default is the type of the JavaBeans property.
      * For class annotations, there is no default and this MUST be
      * specified.
      */
     Class type() default Object.class ;

     /**
      * A product specific name that this resource should be mapped to.
      * The name of this resource, as defined by the <code>name</code>
      * element or defaulted, is a name that is local to the application
      * component using the resource.  (It's a name in the JNDI
      * <code>java:comp/env</code> namespace.)  Many application servers
      * provide a way to map these local names to names of resources
      * known to the application server.  This mapped name is often a
      * <i>global</i> JNDI name, but may be a name of any form. <p>
      *
      * Application servers are not required to support any particular
      * form or type of mapped name, nor the ability to use mapped names.
      * The mapped name is product-dependent and often installation-dependent.
      * No use of a mapped name is portable.
      */
     String mappedName() default "";

     /**
      * The service class, always a type extending
      * <code>javax.xml.ws.Service</code>. This element MUST be specified
      * whenever the type of the reference is a service endpoint interface.
      */
     Class value() default Object.class ;

     /**
      * A URL pointing to the WSDL document for the web service.
      * If not specified, the WSDL location specified by annotations
      * on the resource type is used instead.
      */
     String wsdlLocation() default "";
}
