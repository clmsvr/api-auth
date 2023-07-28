package com.algaworks.algafood.auth.util;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ProtocolResolver;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.util.Base64;

/**
Para carregar essa classe e suas configurações antes mesmo da aplicação inciar, 
crie um arquivo chamado :

   spring.factories
    
na pasta:
 
   src/main/resources/META-INF

em seguida, adicione a seguinte configuração, contendo o caminho completo 
da classe Base64ProtocolResolver:
    
    org.springframework.context.ApplicationContextInitializer=com.algaworks.algafood.core.io.Base64ProtocolResolver
 */

@Component
public class Base64ProtocolResolver implements ProtocolResolver,
		ApplicationContextInitializer<ConfigurableApplicationContext> {

	@Override
	public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
		configurableApplicationContext.addProtocolResolver(this);
	}

	@Override
	public Resource resolve(String location, ResourceLoader resourceLoader) {
		if (location.startsWith("base64:")) {
			byte[] decodedResource = Base64.getDecoder().decode(location.substring(7));
			return new ByteArrayResource(decodedResource);
		}

		return null;
	}

}