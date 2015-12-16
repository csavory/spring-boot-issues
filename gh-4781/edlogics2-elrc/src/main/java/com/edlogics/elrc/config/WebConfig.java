/*
 *
 * Copyright EdLogics, LLC. All Rights Reserved.
 *
 * This software is the proprietary information of EdLogics, LLC.
 * Use is subject to license terms.
 *
 */
package com.edlogics.elrc.config;

import java.net.MalformedURLException;
import java.util.Collections;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.web.DefaultErrorAttributes;
import org.springframework.boot.autoconfigure.web.ErrorAttributes;
import org.springframework.boot.autoconfigure.web.ResourceProperties;
import org.springframework.boot.bind.RelaxedPropertyResolver;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.mobile.device.DeviceResolver;
import org.springframework.mobile.device.LiteDeviceResolver;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.filter.ShallowEtagHeaderFilter;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceChainRegistration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.mvc.WebContentInterceptor;
import org.springframework.web.servlet.resource.VersionResourceResolver;
import org.thymeleaf.extras.conditionalcomments.dialect.ConditionalCommentsDialect;
import org.thymeleaf.processor.IProcessor;
import org.thymeleaf.spring4.SpringTemplateEngine;
import org.thymeleaf.spring4.view.AjaxThymeleafViewResolver;
import org.thymeleaf.spring4.view.ThymeleafViewResolver;
import org.thymeleaf.templateresolver.TemplateResolver;

import ch.mfrey.thymeleaf.extras.cache.CacheDialect;

import com.edlogics.ElrcApplication;
import com.edlogics.common.Constants;
import com.edlogics.config.ApplicationSettings;
import com.edlogics.elrc.config.temporary.CssLinkReplacementResourceTransformer;
import com.edlogics.elrc.config.temporary.ProtocolRelativeCssLinkResourceTransformer;

/**
 * @author Christopher Savory
 *
 */
@Configuration
public class WebConfig extends WebMvcConfigurerAdapter implements EnvironmentAware {

	@Autowired
	ApplicationSettings applicationSettings;

	@Resource
	MessageSource messages;

	@Autowired
	SpringTemplateEngine springTemplateEngine;

	@Autowired
	private ResourceProperties resourceProperties = new ResourceProperties();

	@Autowired
	private Environment env;

	@Autowired
	private GlobalRequestAttributesInterceptor globalRequestAttributesInterceptor;

	@Autowired
	WebContentInterceptor webContentInterceptor;

	@Value("${resources.projectroot:}")
	private String projectRoot;

	@Value("${app.version:}")
	private String appVersion;

	@Value("${spring.fingerprinted-resources.enabled:}")
	private boolean fingerprintedResourcesEnabled;

	@Value("${spring.fingerprinted-resources.cache-period:}")
	private Integer fingerprintedCachePeriod;

	@Value("${app.media.url-root}")
	String mediaUrlRoot;

	private RelaxedPropertyResolver environment;

	private String getProjectRootRequired() {
		Assert.state( this.projectRoot != null, "Please set \"resources.projectRoot\" in application.yml" );
		return this.projectRoot;
	}

	@Override
	public void setEnvironment( Environment environment ) {
		this.environment = new RelaxedPropertyResolver( environment,
				"spring.mobile.devicedelegatingviewresolver." );
	}

	@Override
	public void addInterceptors( InterceptorRegistry registry ) {

		LocaleChangeInterceptor localeChangeInterceptor = new LocaleChangeInterceptor();
		localeChangeInterceptor.setParamName( "lang" );
		registry.addInterceptor( localeChangeInterceptor );
		registry.addInterceptor( globalRequestAttributesInterceptor );
		registry.addInterceptor( webContentInterceptor );
	}

	/**
	 * Adds ResourceHanlers for static Resources. Normally SpringBoot would add a resource handler for "/**" at
	 * WebMvcAutoConfiguration.WebMvcAutoConfigurationAdapter.addResourceHandlers, but we want different paths handled differently
	 * so we override that method here.
	 *
	 */
	@Override
	public void addResourceHandlers( ResourceHandlerRegistry registry ) {

		@SuppressWarnings("static-access")
		boolean localMode = applicationSettings.isLocal();

		//Uncomment this when we go to the Sagan project structure
		//String location = localMode ? "file:///" + getProjectRootRequired() + "/client/src/" : "classpath:/static/"; 
		String location = "classpath:/static/";
		Integer cachePeriod = this.resourceProperties.getCachePeriod();
		boolean useResourceCache = !localMode;
		String version = localMode ? "local" : this.appVersion;

		ProtocolRelativeCssLinkResourceTransformer cssLinkResourceTransformer = new ProtocolRelativeCssLinkResourceTransformer();
		CssLinkReplacementResourceTransformer cssLinkReplacementResourceTransformer = new CssLinkReplacementResourceTransformer( Constants.MEDIA_URL_ROOT_KEY_WITH_VARIABLE_SYNTAX, mediaUrlRoot );
		VersionResourceResolver versionResolver = new VersionResourceResolver()
				//.addFixedVersionStrategy( version, "/**/*.js" ) //Enable this if we use a JavaScript module loader
				.addContentVersionStrategy( "/**" );

		registry.addResourceHandler( "swagger-ui.html" )
				.addResourceLocations( "classpath:/META-INF/resources/" );

		ResourceChainRegistration registration = registry
				.addResourceHandler( "/**/*.js", "/**/*.css", "/**/*.less", "/**/*.png", "/**/*.gif", "/**/*.jpg", "/**/*.svg", "/**/*.ttf", "/**/*.woff", "/**/*.woff2", "/**/*.otf",
						"/**/ko-components/**/*.html",
						"/**/spa-artifacts/**/*.html" )
				.addResourceLocations( location )
				.setCachePeriod( this.fingerprintedCachePeriod )
				.resourceChain( useResourceCache )
				.addTransformer( cssLinkReplacementResourceTransformer );

		if ( fingerprintedResourcesEnabled ) {
			registration.addResolver( versionResolver ).addTransformer( cssLinkResourceTransformer );
		}
	}

	/**
	 * Since jackson-dataformat-xml was added to the POM, Spring started to use XML as the default when the Content-Type or Accepts header was not set.
	 * This puts the default back to JSON.
	 */
	@Override
	public void configureContentNegotiation( ContentNegotiationConfigurer configurer ) {
		configurer.defaultContentType( MediaType.APPLICATION_JSON );
	}

	@Bean
	public LocaleResolver localeResolver() {

		CookieLocaleResolver cookieLocaleResolver = new CookieLocaleResolver();
		cookieLocaleResolver.setDefaultLocale( StringUtils.parseLocaleString( "en" ) );
		return cookieLocaleResolver;
	}

	/**
	 * Will add ETags to responses; as well as 304 responses if appropriate
	 * 
	 * @return
	 */
	@Bean
	public ShallowEtagHeaderFilter shallowEtagHeaderFilter() {
		return new ShallowEtagHeaderFilter();
	}

	/**
	 * Needed to enable validation at the method level using Bean and Hibernate
	 * Validator.
	 *
	 * @return
	 */
	@Bean
	public MethodValidationPostProcessor getMethodValidationPostProcessor(
			MessageSource messageSource ) {
		MethodValidationPostProcessor methodValidationPostProcessor = new MethodValidationPostProcessor();
		methodValidationPostProcessor
				.setValidatorFactory( getLocalValidatorFactoryBean( messageSource ) );
		return methodValidationPostProcessor;
	}

	/**
	 * The basic configuration above will trigger JSR-303 to initialize using
	 * its default bootstrap mechanism. A JSR-303 provider, such as Hibernate
	 * Validator, is expected to be present in the classpath and will be
	 * detected automatically.
	 *
	 * @return
	 */
	@Bean(name = "validator")
	public LocalValidatorFactoryBean getLocalValidatorFactoryBean(
			MessageSource messageSource ) {
		LocalValidatorFactoryBean validatorFactoryBean = new LocalValidatorFactoryBean();
		validatorFactoryBean.setValidationMessageSource( messageSource );
		return validatorFactoryBean;
	}

	@Bean(name = "restTemplate")
	public RestTemplate getRestTemplate() {
		return new RestTemplate();
	}

	@Override
	public Validator getValidator() {
		return getLocalValidatorFactoryBean( messages );
	}

	@Bean(name = "deviceResolver")
	protected DeviceResolver getDeviceResolver() {
		return new LiteDeviceResolver();
	}

	@Bean(name = "thymeleafViewResolver")
	protected ThymeleafViewResolver getViewResolver() {

		// Get new ViewResolver
		ThymeleafViewResolver resolver = new CustomAjaxThymeleafViewResolver();
		resolver.setTemplateEngine( springTemplateEngine );

		// Setup encoding
		String encoding = this.environment.getProperty( "encoding", "UTF-8" );
		String type = this.environment.getProperty( "contentType", "text/html" );
		if ( !type.contains( encoding ) )
			type = type + ";charset=" + encoding;
		resolver.setCharacterEncoding( encoding );
		resolver.setContentType( type );

		// Set names
		resolver.setExcludedViewNames( this.environment.getProperty( "excludedViewNames", String[].class ) );
		resolver.setViewNames( this.environment.getProperty( "viewNames", String[].class ) );

		// This resolver acts as a fallback resolver (e.g. like a
		// InternalResourceViewResolver) so it needs to have low precedence
		resolver.setOrder( Ordered.LOWEST_PRECEDENCE - 5 );

		return resolver;
	}

	@Bean
	public ConditionalCommentsDialect conditionalCommentsDialect() {
		return new ConditionalCommentsDialect();
	}

	@Bean
	public CacheDialect cacheDialect() {
		@SuppressWarnings("static-access")
		boolean localMode = applicationSettings.isLocal();

		if ( localMode ) {
			//Return a benign CacheDialect that won't cache anything
			return new CacheDialect() {

				@Override
				public Set<IProcessor> getProcessors() {
					return Collections.emptySet();
				}
			};
		} else {
			return new CacheDialect();
		}
	}

	@Bean
	public ErrorAttributes errorAttributes() {
		return new DefaultErrorAttributes() {

			private final Logger logger = LoggerFactory.getLogger( getClass() );

			@Override
			public Map<String, Object> getErrorAttributes( RequestAttributes requestAttributes, boolean includeStackTrace ) {
				Map<String, Object> errorAttributes = super.getErrorAttributes( requestAttributes, includeStackTrace );
				errorAttributes.put( Constants.MEDIA_URL_ROOT_KEY, mediaUrlRoot );
				return errorAttributes;
			}

			@Override
			public ModelAndView resolveException( HttpServletRequest request,
					HttpServletResponse response, Object handler, Exception ex ) {
				logger.error( "Error caught for Method: " + request.getMethod() + "; and URI: " + request.getRequestURI() + "; " + ex.getMessage(), ex );
				return super.resolveException( request, response, handler, ex );
			}

		};
	}

	@Bean
	public WebContentInterceptor webContentInterceptor() {

		WebContentInterceptor webContentInterceptor = new WebContentInterceptor();

		boolean localMode = applicationSettings.isLocal();
		boolean addCacheHeaders = !localMode;

		if ( addCacheHeaders ) {
			Properties cacheMappings = new Properties();
			cacheMappings.setProperty( "/activity-service/activities**", "14400" ); // 4 hours
			cacheMappings.setProperty( "/health-info-service/categories**", "14400" ); // 4 hours
			cacheMappings.setProperty( "/feature-service/features**", "14400" ); // 4 hours
			cacheMappings.setProperty( "/message-service/messages**", "31536000" );  // 1 year
			webContentInterceptor.setCacheMappings( cacheMappings );
		}

		return webContentInterceptor;
	}

	public static class CustomAjaxThymeleafViewResolver extends AjaxThymeleafViewResolver {

		@Value("${spring.mobile.thymeleaf.cache-fallback-result:true}")
		public Boolean bCache;

		@Autowired
		private TemplateResolver templateResolver;

		@Autowired
		private SpringTemplateEngine templateEngine;

		@Override
		public boolean isCache() {
			return bCache;
		}

		/**
		 * Override to actually look for the resource to see if it
		 * exists. This view resolver caches its views after the first time, so
		 * the overhead of this check may be acceptible.
		 */
		@Override
		protected boolean canHandle( final String viewName, final Locale locale ) {
			Boolean exists = thymeleafViewExists( viewName );
			return ( exists == null || exists ) ? super.canHandle( viewName, locale ) : exists;
		}

		protected Boolean thymeleafViewExists( String viewName ) {

			// Ensure that the Engine/Resolver is initialized
			templateEngine.initialize();

			org.springframework.core.io.Resource res;
			String viewPath = templateResolver.getPrefix() + viewName + templateResolver.getSuffix();

			if ( viewPath.startsWith( "classpath:" ) )
				res = new ClassPathResource( viewPath.substring( 10 ) );
			else if ( viewPath.startsWith( "file:" ) )
				res = new FileSystemResource( viewPath.substring( 5 ) );
			else {
				try {
					res = new UrlResource( viewPath );
				} catch ( MalformedURLException e ) {
					logger.info( "Unrecognised resource " + viewName );
					return null;   // Can't decide, give up 
				}
			}

			return res.exists();
		}
	}

	@Component
	public static class GlobalRequestAttributesInterceptor extends HandlerInterceptorAdapter {

		@Value("${app.media.url-root}")
		String mediaUrlRoot;

		@Value("${app.resource.url-root:}")
		String resourceUrlRoot;

		@Override
		public boolean preHandle( HttpServletRequest request, HttpServletResponse response,
				Object handler ) throws Exception {
			request.setAttribute( Constants.MEDIA_URL_ROOT_KEY, mediaUrlRoot );
			request.setAttribute( Constants.RESOURCE_URL_ROOT_KEY, resourceUrlRoot );
			request.setAttribute( Constants.HOSTNAME, ElrcApplication.getHostname() );
			return true;
		}
	};
}