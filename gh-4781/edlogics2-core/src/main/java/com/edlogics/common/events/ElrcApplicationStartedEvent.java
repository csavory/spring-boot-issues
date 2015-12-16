package com.edlogics.common.events;

import org.springframework.context.ApplicationEvent;

/**
 * Custom event used to broadcast when the ELRC application startup has been completed
 * and all available API endpoints are ready to be queried.
 *
 * @author Danilo Bonilla
 */
public class ElrcApplicationStartedEvent extends ApplicationEvent {

	/**
	 * Create a new ApplicationEvent.
	 *
	 * @param source the component that published the event (never {@code null})
	 */
	public ElrcApplicationStartedEvent( Object source ) {
		super( source );
	}
}
