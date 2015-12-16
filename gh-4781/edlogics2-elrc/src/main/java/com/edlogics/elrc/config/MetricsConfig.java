/*
 *
 * Copyright EdLogics, LLC. All Rights Reserved.
 *
 * This software is the proprietary information of EdLogics, LLC.
 * Use is subject to license terms.
 *
 */
package com.edlogics.elrc.config;

import static com.google.common.base.CaseFormat.LOWER_UNDERSCORE;
import static com.google.common.base.CaseFormat.UPPER_CAMEL;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManagerFactory;

import net.sf.ehcache.Ehcache;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.SessionFactory;
import org.hibernate.stat.EntityStatistics;
import org.hibernate.stat.QueryStatistics;
import org.hibernate.stat.SecondLevelCacheStatistics;
import org.hibernate.stat.Statistics;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.endpoint.AbstractEndpoint;
import org.springframework.boot.actuate.endpoint.PublicMetrics;
import org.springframework.boot.actuate.metrics.Metric;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ReflectionUtils;

/**
 * @author Christopher Savory
 *
 */
@Configuration
public class MetricsConfig {

	@Autowired
	private EntityManagerFactory emf;
	@Autowired
	private CacheManager cacheManager;
	@Value("${endpoints.metrics-hibernate-queries.enabled}")
	private boolean queriesMetricsEnabled;
	@Value("${endpoints.metrics-hibernate-entities.enabled}")
	private boolean entitiesMetricsEnabled;
	@Value("${endpoints.metrics-hibernate-second-level-cache.enabled}")
	private boolean secondLevelCacheMetricsEnabled;

	@Bean
	@ConditionalOnProperty(prefix = "endpoints.metrics-hibernate", name = "enabled", havingValue = "true")
	public HibernatePublicMetrics hibernatePublicMetrics() {
		return new HibernatePublicMetrics();
	}

	@Bean
	@ConditionalOnProperty(prefix = "endpoints.metrics-ehcache", name = "enabled", havingValue = "true")
	public EhCachePublicMetrics ehCachePublicMetrics() {
		return new EhCachePublicMetrics();
	}

	@Bean
	public CacheContentsEndpoint cacheEntriesEndpoint() {
		return new CacheContentsEndpoint();
	}

	abstract class AbstractPublicMetrics implements PublicMetrics {

		protected String formatString( String value ) {
			//condense all the whitespace to a single space, then with an underscore
			value = value.replaceAll( "[\\s]+", " " ).replace( " ", "_" );

			String[] values = value.split( "\\." );

			for ( int i = 0; i < values.length; i++ ) {
				values[i] = UPPER_CAMEL.to( LOWER_UNDERSCORE, values[i] );
			}

			return StringUtils.join( values, '_' ) + ".";
		}

		protected void addMetrics( List<Metric<?>> metrics, Object statistics, String prefix ) {
			Method[] methods = ReflectionUtils.getAllDeclaredMethods( statistics.getClass() );
			for ( Method method : methods ) {
				if ( StringUtils.startsWithAny( method.getName(), "get", "cache", "local" ) ) {
					String property = ( prefix != null ? prefix.toLowerCase() : "" ) + UPPER_CAMEL.to( LOWER_UNDERSCORE, method.getName().substring( method.getName().startsWith( "get" ) ? 3 : 5 ) );
					if ( method.getReturnType() == long.class || method.getReturnType() == int.class ) {
						Number value = (Number) ReflectionUtils.invokeMethod( method, statistics );
						metrics.add( new Metric<Long>( property, value.longValue() ) );
					} else if ( method.getReturnType() == double.class || method.getReturnType() == float.class ) {
						Number value = (Number) ReflectionUtils.invokeMethod( method, statistics );
						metrics.add( new Metric<Double>( property, value.doubleValue() ) );
					}
				}
			}
		}
	}

	class HibernatePublicMetrics extends AbstractPublicMetrics {

		@Override
		public Collection<Metric<?>> metrics() {
			SessionFactory sessionFactory = emf.unwrap( SessionFactory.class );
			if ( sessionFactory != null ) {
				Statistics cacheStatistics = sessionFactory.getStatistics();
				if ( cacheStatistics != null ) {
					List<Metric<?>> metrics = new ArrayList<>();
					addMetrics( metrics, cacheStatistics, null );

					if ( queriesMetricsEnabled ) {
						String[] queries = cacheStatistics.getQueries();
						for ( String query : queries ) {
							QueryStatistics queryStatistics = sessionFactory.getStatistics().getQueryStatistics( query );
							addMetrics( metrics, queryStatistics, "hibernate.queries." + formatString( query.toLowerCase() ) );
						}
					}
					if ( entitiesMetricsEnabled ) {
						String[] entityNames = cacheStatistics.getEntityNames();
						for ( String entityName : entityNames ) {
							EntityStatistics entityStatistics = sessionFactory.getStatistics().getEntityStatistics( entityName );
							addMetrics( metrics, entityStatistics, "hibernate.entities." + formatString( entityName ) );
						}
					}
					if ( secondLevelCacheMetricsEnabled ) {
						String[] secondLevelCacheRegionNames = cacheStatistics.getSecondLevelCacheRegionNames();
						for ( String regionName : secondLevelCacheRegionNames ) {
							SecondLevelCacheStatistics secondLevelCacheStatistics = sessionFactory.getStatistics().getSecondLevelCacheStatistics( regionName );
							addMetrics( metrics, secondLevelCacheStatistics, "hibernate.second_level_caches." + formatString( regionName ) );
						}
					}
					return metrics;
				}
			}
			return Collections.emptySet();
		}
	}

	class EhCachePublicMetrics extends AbstractPublicMetrics {

		@Override
		public Collection<Metric<?>> metrics() {
			if ( cacheManager != null ) {
				Collection<String> cacheNames = cacheManager.getCacheNames();
				if ( CollectionUtils.isNotEmpty( cacheNames ) ) {
					List<Metric<?>> metrics = new ArrayList<>();

					for ( String cacheName : cacheNames ) {
						Ehcache cache = (Ehcache) cacheManager.getCache( cacheName ).getNativeCache();
						if ( cache != null ) {
							addMetrics( metrics, cache.getStatistics(), "ehcache." + formatString( cacheName ) );
						}
					}

					return metrics;
				}
			}
			return Collections.emptySet();
		}
	}

	@ConfigurationProperties(prefix = "endpoints.cachecontents", ignoreUnknownFields = false)
	class CacheContentsEndpoint extends AbstractEndpoint<Map<String, Object>> {

		/**
		 * Create a new {@link CacheContentsEndpoint} instance.
		 */
		public CacheContentsEndpoint() {
			super( "cachecontents" );
		}

		@Override
		public Map<String, Object> invoke() {
			Map<String, Object> result = new LinkedHashMap<String, Object>();

			SessionFactory sessionFactory = emf.unwrap( SessionFactory.class );
			if ( sessionFactory != null ) {
				Statistics cacheStatistics = sessionFactory.getStatistics();
				if ( cacheStatistics != null ) {

					String[] secondLevelCacheRegionNames = cacheStatistics.getSecondLevelCacheRegionNames();
					for ( String regionName : secondLevelCacheRegionNames ) {
						//Don't try to add the query cache here, will result in a hibernate thowing an error.  The query cache contents are already exposed through /metrics anyways
						if ( !"org.hibernate.cache.internal.StandardQueryCache".equals( regionName ) && !"shortTimeQueryCache".equals( regionName )
								&& !"org.hibernate.cache.spi.UpdateTimestampsCache".equals( regionName ) ) {
							SecondLevelCacheStatistics secondLevelCacheStatistics = sessionFactory.getStatistics().getSecondLevelCacheStatistics( regionName );
							@SuppressWarnings("rawtypes")
							Map cacheEntries = secondLevelCacheStatistics.getEntries();
							for ( Object key : cacheEntries.keySet() ) {
								result.put( "hibernate.second_level_caches." + regionName + "." + key, cacheEntries.get( key ) );
							}
						}
					}
				}
			}

			return result;
		}
	}

}