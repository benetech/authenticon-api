package org.benetech.authenticon.api;

import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

public class StaticResourceConfiguration extends WebMvcConfigurerAdapter {

	//@Configuration
	private static final String[] CLASSPATH_RESOURCE_LOCATIONS = {
			"classpath:/META-INF/resources/", 
			//"classpath:/resources/",
			"classpath:/static/",
			"classpath:/resources/images/",
			"classpath:/public/" 
			};

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/**").addResourceLocations(CLASSPATH_RESOURCE_LOCATIONS);
	}
}
