package com.daumsoft.analyticsManager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.scheduling.annotation.EnableScheduling;

//@EnableDiscoveryClient
@SpringBootApplication
//@EnableZuulProxy
@EnableScheduling
public class AnalyticsManagerApplication  extends SpringBootServletInitializer{

	@Override protected SpringApplicationBuilder
	  configure(SpringApplicationBuilder application) { return
	      application.sources(AnalyticsManagerApplication.class);
	  }

	public static void main(String[] args) {

    System.out.println(" ====== AnalyticsManagerApplication Start ====== ");
		SpringApplication.run(AnalyticsManagerApplication.class, args);
	}

}
