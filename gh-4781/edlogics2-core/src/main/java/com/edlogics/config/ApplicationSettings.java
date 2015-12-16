/*
 *
 * Copyright EdLogics, LLC. All Rights Reserved.
 *
 * This software is the proprietary information of EdLogics, LLC.
 * Use is subject to license terms.
 *
 */
package com.edlogics.config;

import java.util.Collection;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.MessageSource;
import org.springframework.core.env.Environment;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.switchuser.SwitchUserGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.support.RequestContextUtils;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * Settings that are configured external to the application
 * Most likely these will come from application.yml
 *
 *
 * @author Christopher Savory
 *
 */
@Component
public class ApplicationSettings implements ApplicationContextAware {

	/**
	 * A random number that will be regenerated on App Startup
	 */
	private static double randomNumber = Math.random();

	/**
	 * Used to access properties and profiles
	 */
	private static Environment environment;

	/**
	 * Autowired inner class that contains application media specific props
	 */
	@Resource
	protected ApplicationMediaProperties applicationMediaConfig;

	/**
	 * Autowired inner class that contains application API specific props
	 */
	@Resource
	protected ApplicationApiProperties applicationApiProperties;

	/**
	 * Autowired inner class that contains application specific props
	 */
	@Resource
	protected ApplicationProperties applicationConfig;

	@Value("${server.session.timeout}")
	private String timeoutMinutes;

	@Value("${info.build.version}")
	private String buildVersion;

	@Override
	public void setApplicationContext( ApplicationContext context ) throws BeansException {
		environment = context.getEnvironment();
	}

	/**
	 * Checks if the given authentication is switched authentication and
	 * returns the value if it is, null otherwise.
	 * 
	 * @param authentication - Authentication to check for switched user.
	 * @return null or Authentication object
	 */
	public Authentication getOriginalUser( Authentication authentication ) {
		Authentication original = null;

		if ( authentication != null ) {
			// iterate over granted authorities and find the 'switch user' authority
			Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

			for ( GrantedAuthority auth : authorities ) {
				// check for switch user type of authority
				if ( auth instanceof SwitchUserGrantedAuthority ) {
					original = ( (SwitchUserGrantedAuthority) auth ).getSource();
				}
			}
		}
		return original;
	}

	/**
	 * @return the ApplicationMediaProperties object
	 */
	public ApplicationMediaProperties getApplicationMediaConfig() {
		return applicationMediaConfig;
	}

	/**
	 * @return the ApplicationMediaProperties object
	 */
	public ApplicationProperties getApplicationConfig() {
		return applicationConfig;
	}

	public ApplicationApiProperties getApplicationApiProperties() {
		return applicationApiProperties;
	}

	/**
	 * media specific application settings
	 */
	@Component
	@ConfigurationProperties("app.media")
	public static class ApplicationMediaProperties {

		private String urlRoot;

		private String avatarThumbDir;

		private String audioHead2headDir;

		private String badgesDir;

		private String emmiBaseUrl;

		/**
		 * @return the urlRoot
		 */
		public String getUrlRoot() {
			return urlRoot;
		}

		/**
		 * @param username the urlRoot to set
		 */
		public void setUrlRoot( String urlRoot ) {
			this.urlRoot = urlRoot;
		}

		/**
		 * @return the avatarThumbDir
		 */
		public String getAvatarThumbnailDirectory() {
			return avatarThumbDir;
		}

		/**
		 * @param avatarThumbDir the avatarThumbDir to set
		 */
		public void setAvatarThumbDir( String avatarThumbDir ) {
			this.avatarThumbDir = avatarThumbDir;
		}

		/**
		 * @return the audioHead2headDir
		 */
		public String getAudioHead2headDirectory() {
			return audioHead2headDir;
		}

		/**
		 * @param audioHead2headDir the audioHead2headDir to set
		 */
		public void setAudioHead2headDir( String audioHead2headDir ) {
			this.audioHead2headDir = audioHead2headDir;
		}

		/**
		 * @return the badgesDir
		 */
		public String getBadgesDirectory() {
			return badgesDir;
		}

		/**
		 * @param badgesDir the badgesDir to set
		 */
		public void setBadgesDir( String badgesDir ) {
			this.badgesDir = badgesDir;
		}

		/**
		 * @return
		 */
		public String getEmmiBaseUrl() {
			return emmiBaseUrl;
		}

		/**
		 * @param emmiBaseUrl
		 */
		public void setEmmiBaseUrl( String emmiBaseUrl ) {
			this.emmiBaseUrl = emmiBaseUrl;
		}
	}

	/**
	 * application settings
	 */
	@Component
	@ConfigurationProperties("app")
	public static class ApplicationProperties {

		private String baseUrl;
		private String googleAnalyticsTrackingId;

		/**
		 * @return the urlRoot
		 */
		public String getBaseUrl() {
			return baseUrl;
		}

		/**
		 * @param baseUrl
		 *            set the parameter passed in
		 */
		public void setBaseUrl( String baseUrl ) {
			this.baseUrl = baseUrl;
		}

		/**
		 * @return the googleAnalyticsTrackingId
		 */
		public String getGoogleAnalyticsTrackingId() {
			return googleAnalyticsTrackingId;
		}

		/**
		 * @param googleAnalyticsTrackingId
		 *            set the parameter passed in
		 */
		public void setGoogleAnalyticsTrackingId( String googleAnalyticsTrackingId ) {
			this.googleAnalyticsTrackingId = googleAnalyticsTrackingId;
		}
	}

	/**
	 * application settings
	 */
	@Component
	@ConfigurationProperties("app.api-endpoint")
	public static class ApplicationApiProperties {

		private String categoriesDomain;
		private String categoriesContext;
		private String questionsDomain;
		private String questionsContext;
		private String activitiesDomain;
		private String activitiesContext;

		/**
		 * @return the categoriesDomain
		 */
		public String getCategoriesDomain() {
			return categoriesDomain;
		}

		/**
		 * @param categoriesDomain the categoriesDomain to set
		 */
		public void setCategoriesDomain( String categoriesDomain ) {
			this.categoriesDomain = categoriesDomain;
		}

		/**
		 * @return the categoriesContext
		 */
		public String getCategoriesContext() {
			return categoriesContext;
		}

		/**
		 * @param categoriesContext the categoriesContext to set
		 */
		public void setCategoriesContext( String categoriesContext ) {
			this.categoriesContext = categoriesContext;
		}

		/**
		 * @return the questionsDomain
		 */
		public String getQuestionsDomain() {
			return questionsDomain;
		}

		/**
		 * @param questionsDomain the questionsDomain to set
		 */
		public void setQuestionsDomain( String questionsDomain ) {
			this.questionsDomain = questionsDomain;
		}

		/**
		 * @return the questionsContext
		 */
		public String getQuestionsContext() {
			return questionsContext;
		}

		/**
		 * @param questionsContext the questionsContext to set
		 */
		public void setQuestionsContext( String questionsContext ) {
			this.questionsContext = questionsContext;
		}

		/**
		 * @return the activitiesDomain
		 */
		public String getActivitiesDomain() {
			return activitiesDomain;
		}

		/**
		 * @param activitiesDomain the activitiesDomain to set
		 */
		public void setActivitiesDomain( String activitiesDomain ) {
			this.activitiesDomain = activitiesDomain;
		}

		/**
		 * @return the activitiesContext
		 */
		public String getActivitiesContext() {
			return activitiesContext;
		}

		/**
		 * @param activitiesContext the activitiesContext to set
		 */
		public void setActivitiesContext( String activitiesContext ) {
			this.activitiesContext = activitiesContext;
		}
	}

	public boolean isTest() {
		return environment.acceptsProfiles( "test" );
	}

	public boolean isLocal() {
		return environment.acceptsProfiles( "local" );
	}

	/**
	 * @return the timeoutMinutes
	 */
	public String getTimeoutMinutes() {
		return timeoutMinutes;
	}

	/**
	 * Gets the base Domain of the site with protocol. E.g. https://platform-dev.edlogics.com
	 * Only works in the context of a request, and probably on't work in a Scheduled or Async process.
	 *
	 * @return base url
	 */
	public static String getBaseUrl() {
		HttpServletRequest currentRequest = getCurrentRequest();
		UriComponentsBuilder uriComponentsBuilder =
				ServletUriComponentsBuilder.fromCurrentRequest().replacePath( "" ).replaceQuery( null );
		/**
		 * Due to a bug in ServletUriComponentsBuilder which doesn't use the forwarded
		 * port header in http/https/80/403 cases, we must manually unset the port number to null
		 * to avoid the mixup.
		 */
		if ( ( currentRequest.getServerPort() == 443 && "http".equals( currentRequest.getScheme() ) )
				|| ( currentRequest.getServerPort() == 80 && "https".equals( currentRequest.getScheme() ) ) ) {
			uriComponentsBuilder.port( null );
		}
		return uriComponentsBuilder.build().toUriString();
	}

	/**
	 * Gets the base Domain of the site without protocol. E.g. platform-dev.edlogics.com
	 * Only works in the context of a request, and probably on't work in a Scheduled or Async process.
	 *
	 * @return base domain
	 */
	public static String getBaseDomain() {
		return ServletUriComponentsBuilder.fromCurrentRequest().build().getHost();
	}

	/**
	 * Get the request through {@link RequestContextHolder}.
	 */
	public static HttpServletRequest getCurrentRequest() {
		RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
		Assert.state( requestAttributes != null, "Could not find current request via RequestContextHolder" );
		Assert.isInstanceOf( ServletRequestAttributes.class, requestAttributes );
		HttpServletRequest servletRequest = ( (ServletRequestAttributes) requestAttributes ).getRequest();
		Assert.state( servletRequest != null, "Could not find current HttpServletRequest" );
		return servletRequest;
	}

	/**
	 * Get the messages through {@link RequestContextUtils}.
	 */
	public static MessageSource getThemeMessageSource() {
		return RequestContextUtils.getTheme( getCurrentRequest() ).getMessageSource();
	}

	/**
	 * Returns the build number for non-local environments. For local, it's just a random number
	 * 
	 * @return
	 */
	public String getBuildVersion() {
		return isLocal() ? String.valueOf( randomNumber ) : this.buildVersion;
	}
}
