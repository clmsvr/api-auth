package com.algaworks.algafood.auth.util;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Validated
@Component
@ConfigurationProperties("auth-server.jwt.keystore")
public class JwtKeyStoreProperties {

//	@NotNull
	private Resource jksLocation;
	
//	@NotBlank
	private String password;
	
//	@NotBlank
	private String keypairAlias;

}