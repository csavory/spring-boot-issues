/*
 *
 * Copyright EdLogics, LLC. All Rights Reserved.
 *
 * This software is the proprietary information of EdLogics, LLC.
 * Use is subject to license terms.
 *
 */
package com.edlogics;

import static org.springframework.util.StringUtils.commaDelimitedListToStringArray;
import static org.springframework.util.StringUtils.trimAllWhitespace;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.MutableDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.context.embedded.MimeMappings;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Scope;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.hateoas.config.EnableEntityLinks;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.context.WebApplicationContext;

import com.edlogics.common.events.ElrcApplicationStartedEvent;
import com.edlogics.common.security.config.annotation.web.servlet.configuration.SpringSecurityAuditorAware;

/**
 * This is the main class for Spring Boot to do the application startup and configuration
 *
 * @author Christopher Savory
 *
 */
@SpringBootApplication
@EnableConfigurationProperties
@EnableTransactionManagement
@EnableAspectJAutoProxy
@EnableJpaAuditing
@EnableEntityLinks
@EnableAsync
@ServletComponentScan
@EnableCaching
public class ElrcApplication extends SpringBootServletInitializer implements EmbeddedServletContainerCustomizer {

	protected Logger logger = LoggerFactory.getLogger( getClass() );

	/**
	 * Holds Spring Boot configuration properties
	 */
	protected Properties props = new Properties();

	@Value("${app.timezone}")
	String elrcTimezone;

	public ElrcApplication() {}

	/**
	 * For JPA Auditing
	 *
	 * @return
	 */
	@Bean
	public SpringSecurityAuditorAware auditorAware() {
		return new SpringSecurityAuditorAware();
	}

	/**
	 * @return the Scheduler used by ArcadeServiceImpl
	 */
	@Bean
	public ThreadPoolTaskScheduler scheduler() {
		ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
		scheduler.setPoolSize( 10 );
		return scheduler;
	}

	/**
	 * @return the TaskExecutor used by MailGunSendEmailService
	 */
	@Bean
	public ThreadPoolTaskExecutor taskExecutor() {
		ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
		// http://docs.spring.io/spring/docs/current/spring-framework-reference/htmlsingle/#scheduling-task-namespace-executor
		taskExecutor.setCorePoolSize( 100 );
		taskExecutor.setMaxPoolSize( 100 );
		return taskExecutor;
	}

	/**
	 * Bean to hold the system maintenance date time. Defaults to a far future date time.
	 *
	 * @return
	 */
	@Bean
	public MutableDateTime maintenanceDateTime() {
		MutableDateTime farFuture = MutableDateTime.now( DateTimeZone.forID( elrcTimezone ) );
		// Set to year 9999 to make easy to check for far future value
		// There is no easy way to do a max Date with Joda time so this is the workaround
		farFuture.setYear( 9999 );
		return farFuture;
	}

	/**
	 * If the application is currently in maintenance as specified by the maintenance date time being set.
	 *
	 * @return Is the current date time > maintenance date time?
	 */
	@Bean
	@Scope(WebApplicationContext.SCOPE_REQUEST)
	public boolean inMaintenance() {
		DateTime currentDateTime = DateTime.now( DateTimeZone.forID( elrcTimezone ) );
		return ( currentDateTime.getMillis() - maintenanceDateTime().getMillis() ) > 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.boot.web.SpringBootServletInitializer#configure(org.springframework.boot.builder.SpringApplicationBuilder)
	 */
	@Override
	protected SpringApplicationBuilder configure( SpringApplicationBuilder application ) {
		application.sources( ElrcApplication.class );

		// Set active profiles.
		List<String> profiles = new ArrayList<String>();

		String hostName = ElrcApplication.getHostname();

		if ( StringUtils.isNotBlank( System.getProperty( "spring.profiles.active" ) ) ) {
			for ( String profile : commaDelimitedListToStringArray( trimAllWhitespace( System.getProperty( "spring.profiles.active" ) ) ) ) {
				profiles.add( profile );
			}
		} else {
			profiles.add( "local" );
		}

		logger.info( "Spring Boot configuration: profiles = " + profiles );
		application.profiles( profiles.toArray( new String[profiles.size()] ) );

		// Set additional properties. Note: this API does not exist in 0.5.0.M5
		// or earlier.
		logger.info( "Spring Boot configuration: properties = " + props );
		application.properties( props );// New API

		return application;
	}

	@Override
	public void customize( ConfigurableEmbeddedServletContainer container ) {
		MimeMappings mappings = new MimeMappings( MimeMappings.DEFAULT );
		mappings.add( "ico", "image/vnd.microsoft.icon" );
		mappings.add( "woff", "application/font-woff" );
		mappings.add( "ttf", "application/octet-stream" );
		container.setMimeMappings( mappings );
	}

	/**
	 * This is called when running locally as Spring Boot, but not from Tomcat
	 *
	 * @param args
	 */
	public static void main( String[] args ) {

		System.setProperty( "jsse.enableSNIExtension", "false" );

		// Create an instance and invoke run(); Allows the constructor to perform
		// Initialization regardless of whether we are running as an application
		// or in a container.
		new ElrcApplication().runAsJavaApplication( args );
	}

	/**
	 * Run the application using Spring Boot. <code>SpringApplication.run</code> tells Spring Boot to use this class as the initialiser for the whole
	 * application (via the class annotations above). This method is only used
	 * when running as a Java application.
	 *
	 * @param args
	 *            Any command line arguments.
	 */
	protected void runAsJavaApplication( String[] args ) {
		SpringApplicationBuilder application = new SpringApplicationBuilder();
		configure( application );

		//Start the Server
		application.run( args );

		logger.info( "ELRC Started, notifying listeners ..." );
		application.context().publishEvent( new ElrcApplicationStartedEvent( "ELRC Application Started" ) );
	}

	/**
	 * get the host's hostname
	 * return hostname
	 */
	public static String getHostname() {
		String hostname = null;
		try {
			hostname = InetAddress.getLocalHost().getHostName();
		} catch ( UnknownHostException e ) {
			e.printStackTrace();
		}

		return hostname;
	}
}