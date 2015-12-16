/*
 *
 * Copyright EdLogics, LLC. All Rights Reserved.
 *
 * This software is the proprietary information of EdLogics, LLC.
 * Use is subject to license terms.
 *
 */
package com.edlogics.common.security.web.bind.support;

import java.lang.annotation.Annotation;

import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import com.edlogics.common.security.web.bind.annotation.AuthenticationDetails;

/**
 * Allows resolving the {@link Authentication#getDetails()} using the {@link AuthenticationDetails} annotation. For example, the following {@link Controller}:
 *
 * <pre>
 * @Controller
 * public class MyController {
 *     @RequestMapping("/user/current/show")
 *     public String show(@AuthenticationDetails User customUser) {
 *         // do something with CustomUser
 *         return "view";
 *     }
 * </pre>
 *
 * <p>
 * Will resolve the User argument using {@link Authentication#getDetails()} from the {@link SecurityContextHolder}. If the {@link Authentication} or
 * {@link Authentication#getDetails()} is null, it will return null. If the types do not match, null will be returned unless
 * {@link AuthenticationDetails#errorOnInvalidType()} is true in which case a {@link ClassCastException} will be thrown.
 * </p>
 *
 * <p>
 * Alternatively, users can create a custom meta annotation as shown below:
 * </p>
 *
 * <pre>
 * 
 * 
 * 
 * &#064;Target({ ElementType.PARAMETER })
 * &#064;Retention(RetentionPolicy.RUNTIME)
 * &#064;AuthenticationDetails
 * public @interface AuthenticatedUser {}
 * </pre>
 *
 * <p>
 * The custom annotation can then be used instead. For example:
 * </p>
 *
 * <pre>
 * @Controller
 * public class MyController {
 *     @RequestMapping("/user/current/show")
 *     public String show(@AuthenticatedUser User user) {
 *         // do something with User
 *         return "view";
 *     }
 * </pre>
 *
 *
 * @author edlogics
 *
 */
public final class AuthenticationDetailsArgumentResolver implements HandlerMethodArgumentResolver {

	public AuthenticationDetailsArgumentResolver() {
		super();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.web.method.support.HandlerMethodArgumentResolver#supportsParameter(org.springframework.core.MethodParameter)
	 */
	@Override
	public boolean supportsParameter( MethodParameter parameter ) {
		return findMethodAnnotation( AuthenticationDetails.class, parameter ) != null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.web.method.support.HandlerMethodArgumentResolver#resolveArgument(
	 * org.springframework.core.MethodParameter,
	 * org.springframework.web.method.support.ModelAndViewContainer,
	 * org.springframework.web.context.request.NativeWebRequest,
	 * org.springframework.web.bind.support.WebDataBinderFactory)
	 */
	@Override
	public Object resolveArgument(
			MethodParameter parameter,
			ModelAndViewContainer mavContainer,
			NativeWebRequest webRequest,
			WebDataBinderFactory binderFactory ) throws Exception {

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if ( authentication == null ) {
			return null;
		}
		Object details = authentication.getDetails();
		if ( details != null && !parameter.getParameterType().isAssignableFrom( details.getClass() ) ) {
			AuthenticationDetails authenticationDetails = findMethodAnnotation( AuthenticationDetails.class, parameter );
			if ( authenticationDetails.errorOnInvalidType() ) {
				throw new ClassCastException( details + " is not assiable to " + parameter.getParameterType() );
			} else {
				return null;
			}
		}
		return details;
	}

	/**
	 * Obtains the specified {@link Annotation} on the specified {@link MethodParameter}.
	 *
	 * @param annotationClass the class of the {@link Annotation} to find on the {@link MethodParameter}
	 * @param parameter the {@link MethodParameter} to search for an {@link Annotation}
	 * @return the {@link Annotation} that was found or null.
	 */
	private <T extends Annotation> T findMethodAnnotation( Class<T> annotationClass, MethodParameter parameter ) {
		T annotation = parameter.getParameterAnnotation( annotationClass );
		if ( annotation != null ) {
			return annotation;
		}
		Annotation[] annotationsToSearch = parameter.getParameterAnnotations();
		for ( Annotation toSearch : annotationsToSearch ) {
			annotation = AnnotationUtils.findAnnotation( toSearch.annotationType(), annotationClass );
			if ( annotation != null ) {
				return annotation;
			}
		}
		return null;
	}
}