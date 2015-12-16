/*
 *
 * Copyright EdLogics, LLC. All Rights Reserved.
 *
 * This software is the proprietary information of EdLogics, LLC.
 * Use is subject to license terms.
 *
 */
package com.edlogics.common.workflow.action;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.Assert;
import org.springframework.web.servlet.support.RequestContextUtils;
import org.springframework.webflow.action.MultiAction;
import org.springframework.webflow.execution.RequestContext;

import com.edlogics.users.domain.User;

/**
 * @author jlanpher
 *
 */
public abstract class BaseAction extends MultiAction {

	public BaseAction() {
		super();
	}

	/**
	 * @param target
	 */
	public BaseAction( Object target ) {
		super( target );
	}

	/**
	 * Gets the currently authenticated user and makes that user
	 * available to all child actions.
	 *
	 * @return
	 */
	public User getWebFlowUser() {
		Object details = SecurityContextHolder.getContext().getAuthentication().getDetails();
		if ( details instanceof User ) {
			return (User) details;
		}
		return null;
	}

	/**
	 * Helper Method.
	 *
	 * Used to get the current Locale.
	 *
	 * @return
	 */
	public Locale getLocale() {
		return LocaleContextHolder.getLocale();
	}

	/**
	 * Helper Method.
	 *
	 * Used to get the native HttpServletRequest.
	 *
	 * @param requestContext
	 * @return
	 */
	public HttpServletRequest getNativeRequest( RequestContext requestContext ) {
		Object nativeRequest = requestContext.getExternalContext().getNativeRequest();
		return nativeRequest instanceof HttpServletRequest ? (HttpServletRequest) nativeRequest : null;
	}

	/**
	 * Helper Method.
	 *
	 * Used to get the native HttpSession.
	 *
	 * @param requestContext
	 * @return
	 */
	public HttpSession getNativeSession( RequestContext requestContext ) {
		return getNativeRequest( requestContext ).getSession();
	}

	/**
	 * Helper Method.
	 *
	 * Used to centralize the adding of an object to flow scope.
	 *
	 * @param requestContext
	 * @param attributeName
	 * @param value
	 */
	public <T> void addFlowScopedAttribute( RequestContext requestContext, String attributeName, T value ) {
		requestContext.getFlowScope().put( attributeName, value );
	}

	/**
	 * Helper Method.
	 *
	 * Used to centralize the getting of an object from flow scope.
	 *
	 * @param requestContext
	 * @param attributeName
	 * @param requiredType
	 */
	public <T> T getFlowScopedAttribute( RequestContext requestContext, String attributeName, Class<T> requiredType ) {
		return requestContext.getFlowScope().get( attributeName, requiredType );
	}

	/**
	 * Helper Method.
	 *
	 * Used to centralize the removal of an object from flow scope.
	 *
	 * @param requestContext
	 * @param attributeName
	 * @param value
	 */
	public void removeFlowScopedAttribute( RequestContext requestContext, String attributeName ) {
		requestContext.getFlowScope().remove( attributeName );
	}
	
	/**
	 * Helper Method.
	 *
	 * Used to centralize the adding of an object to request scope.
	 *
	 * @param requestContext
	 * @param attributeName
	 * @param value
	 */
	public <T> void addRequestScopedAttribute( RequestContext requestContext, String attributeName, T value ) {
		requestContext.getRequestScope().put( attributeName, value );
	}

	/**
	 * Helper Method.
	 *
	 * Used to centralize the getting of an object from request scope.
	 *
	 * @param requestContext
	 * @param attributeName
	 * @param requiredType
	 */
	public <T> T getRequestScopedAttribute( RequestContext requestContext, String attributeName, Class<T> requiredType ) {
		return requestContext.getRequestScope().get( attributeName, requiredType );
	}

	/**
	 * Helper Method.
	 *
	 * Used to centralize the removal of an object from request scope.
	 *
	 * @param requestContext
	 * @param attributeName
	 * @param value
	 */
	public void removeRequestScopedAttribute( RequestContext requestContext, String attributeName ) {
		requestContext.getRequestScope().remove( attributeName );
	}
	
	/**
	 * Helper Method.
	 *
	 * Used to centralize the adding of an object to view scope.
	 *
	 * @param requestContext
	 * @param attributeName
	 * @param value
	 */
	public <T> void addViewScopedAttribute( RequestContext requestContext, String attributeName, T value ) {
		requestContext.getViewScope().put( attributeName, value );
	}

	/**
	 * Helper Method.
	 *
	 * Used to centralize the getting of an object from view scope.
	 *
	 * @param requestContext
	 * @param attributeName
	 * @param requiredType
	 */
	public <T> T getViewScopedAttribute( RequestContext requestContext, String attributeName, Class<T> requiredType ) {
		return requestContext.getViewScope().get( attributeName, requiredType );
	}

	/**
	 * Helper Method.
	 *
	 * Used to centralize the removal of an object from view scope.
	 *
	 * @param requestContext
	 * @param attributeName
	 * @param value
	 */
	public void removeViewScopedAttribute( RequestContext requestContext, String attributeName ) {
		requestContext.getViewScope().remove( attributeName );
	}

	/**
	 * Helper Method.
	 *
	 * Used to centralize the adding of an object to HttpSession scope.
	 *
	 * @param requestContext
	 * @param attributeName
	 * @param value
	 */
	public <T> void addNativeSessionAttribute( RequestContext requestContext, String attributeName, T value ) {
		getNativeSession( requestContext ).setAttribute( attributeName, value );
	}

	@SuppressWarnings("unchecked")
	public <T> T getNativeSessionAttribute( RequestContext requestContext, String attributeName, Class<T> requiredType ) {
		Object value = getNativeSession( requestContext ).getAttribute( attributeName );
		Assert.notNull( requiredType, "The required type to assert is required" );
		if ( value != null && !requiredType.isInstance( value ) ) {
			throw new IllegalArgumentException( "The attribute name '" + attributeName + "' has value [" + value
					+ "] that is not of expected type [" + requiredType + "], instead it is of type ["
					+ value.getClass().getName() + "]" );
		}
		return (T) value;
	}

	/**
	 * Helper Method.
	 *
	 * Used to centralize the removal of an object from HttpSession scope.
	 *
	 * @param requestContext
	 * @param label
	 * @param value
	 */
	public void removeNativeSessionAttribute( RequestContext requestContext, String attributeName ) {
		getNativeSession( requestContext ).removeAttribute( attributeName );
	}

	/**
	 * Method that gets the Themed MessageSource
	 * 
	 * @param requestContext
	 * @return MessageSource
	 */
	public MessageSource getMessageSourceByTheme( RequestContext requestContext ) {
		return RequestContextUtils.getTheme( getNativeRequest( requestContext ) ).getMessageSource();
	}
}