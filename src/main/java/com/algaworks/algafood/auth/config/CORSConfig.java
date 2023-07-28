package com.algaworks.algafood.auth.config;

import java.util.Arrays;

//Fonte: https://spring.io/blog/2015/06/08/cors-support-in-spring-framework#filter-based-cors-support

import java.util.Collections;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CORSConfig {

	/*
	Nota sobre o allowCredentials
	A configuração de allowCredentials como "true", combinada com allowedOrigins como "*", não é 
	suportada pelos navegadores. 
	Recomendamos utilizar a opção allowCredentials como false, caso o allowedOrigins seja "*".
	Só é possível utilizar a opção allowCredentials como "true", 
	caso sejam especificadas as Origins permitidas. 
	

	Esta solução necessita habilitar cors() no http chain para 
	a pagina de login e para o endpoint de token.
	https://docs.spring.io/spring-security/reference/reactive/integrations/cors.html
	 */
	@Bean
	CorsConfigurationSource corsConfigurationSource() 
	{
		CorsConfiguration configuration = new CorsConfiguration();
		configuration.setAllowedOrigins(Arrays.asList("*"));
		configuration.setAllowedMethods(Arrays.asList("*"));
		configuration.setAllowedHeaders(Arrays.asList("*"));
		configuration.setAllowCredentials(false);	
		
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
		return source;
	}	

// solução sugerida pelo thiago da algaworks
// NAO pode habilitar o cors() nos chains da configuração do servidor.
//	@Bean
	public FilterRegistrationBean<CorsFilter> corsFilter() {
		
		CorsConfiguration config = new CorsConfiguration();
		config.setAllowCredentials(false);
		config.setAllowedOrigins(Collections.singletonList("*"));
		config.setAllowedMethods(Collections.singletonList("*"));
		config.setAllowedHeaders(Collections.singletonList("*"));
		
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", config);

		FilterRegistrationBean<CorsFilter> bean = new FilterRegistrationBean<>();
		bean.setFilter(new CorsFilter(source));
		bean.setOrder(Ordered.HIGHEST_PRECEDENCE);
		
		return bean;
	}	
}