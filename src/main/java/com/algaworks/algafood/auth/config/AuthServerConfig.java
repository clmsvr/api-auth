package com.algaworks.algafood.auth.config;

import java.io.InputStream;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.Duration;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.authorization.InMemoryOAuth2AuthorizationConsentService;
import org.springframework.security.oauth2.server.authorization.JdbcOAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsentService;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.client.InMemoryRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.JdbcRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.OAuth2TokenFormat;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.MediaTypeRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import com.algaworks.algafood.auth.domain.Usuario;
import com.algaworks.algafood.auth.domain.UsuarioRepository;
import com.algaworks.algafood.auth.service.AuthQueryService;
import com.algaworks.algafood.auth.service.AuthQueryServiceImpl;
import com.algaworks.algafood.auth.util.JwtKeyStoreProperties;
import com.algaworks.algafood.auth.util.SecurityProperties;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;

@Configuration
@EnableWebSecurity //permite que nossa configuracao substitua as configurações default de seguranca dos Starters do Spring Security - https://stackoverflow.com/questions/44671457/what-is-the-use-of-enablewebsecurity-in-spring
public class AuthServerConfig {
	
 	//A Spring Security filter chain para os Endpoints de AUTORIZACAO (protocolo OAUTH2)
	@Bean
	@Order(1)
	public SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http)
	throws Exception {
		
		//Customizado abaixo para configurar o endpoint "/oauth2/consent"
		//OAuth2AuthorizationServerConfiguration.applyDefaultSecurity(http);
		applyDefaultSecurity_Custom(http);
		
		http.getConfigurer(OAuth2AuthorizationServerConfigurer.class)
			.oidc(Customizer.withDefaults());	// Enable OpenID Connect 1.0
		
		http
			// Redirect to the login page when not authenticated from the
			// authorization endpoint
//			.exceptionHandling((exceptions) -> exceptions
//				.authenticationEntryPoint(
//					new LoginUrlAuthenticationEntryPoint("/login"))
//			)
			.exceptionHandling((exceptions) -> exceptions
					.defaultAuthenticationEntryPointFor(
						new LoginUrlAuthenticationEntryPoint("/login"),
						new MediaTypeRequestMatcher(MediaType.TEXT_HTML)
					)
			)		
			.cors(Customizer.withDefaults())
			// Accept access tokens for User Info and/or Client Registration
//          .oauth2ResourceServer(OAuth2ResourceServerConfigurer::jwt);
		    .oauth2ResourceServer((resourceServer) -> resourceServer
				.jwt(Customizer.withDefaults()));
		
		return http.build();
	}
	
	//Copia do metodo "OAuth2AuthorizationServerConfiguration.applyDefaultSecurity'
	//para adicionar uma customização para o endpoint "/oauth2/consent"
	public static void applyDefaultSecurity_Custom(HttpSecurity http) 
	throws Exception 
	{
		OAuth2AuthorizationServerConfigurer authorizationServerConfigurer =
				new OAuth2AuthorizationServerConfigurer();
        
		//Customização 
		authorizationServerConfigurer.authorizationEndpoint(cust -> 
			cust.consentPage("/oauth2/consent")
		);
        
		//Este requestMatcher tem todos os endpois de Autorização
		//Vide: org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings.builder()
		RequestMatcher endpointsMatcher = authorizationServerConfigurer
				.getEndpointsMatcher();

		http
		    //!!! The http.securityMatcher() states that this HttpSecurity is applicable only to URLs that start with os endpoints deste ResquestMatcher.
		    //https://docs.spring.io/spring-security/reference/servlet/configuration/java.html#_multiple_httpsecurity_instances
			.securityMatcher(endpointsMatcher)
			
			.authorizeHttpRequests(authorize ->
				authorize.anyRequest().authenticated()
			)
			//.csrf(csrf -> csrf.ignoringRequestMatchers(endpointsMatcher))
			.csrf(csrf -> csrf.disable()) //meu
			.apply(authorizationServerConfigurer);
	}
	
	//A Spring Security filter chain for authentication.
	//A Spring Security filter chain para os DEMAIS Endpoints, incluindo os de Login
	@Bean
	@Order(2)
	public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http)
			throws Exception {
		
		//observe que nao chama: .securityMatcher()
		http
			.authorizeHttpRequests(cust -> cust
				.anyRequest().authenticated()
			)
			.csrf(cust -> cust.disable())
			.cors(Customizer.withDefaults())
			 // Form login handles the redirect to the login page from the
			 // authorization server filter chain
			.formLogin(cust -> cust.loginPage("/login").permitAll())
			 //este logout nao funciona para a Conceções de escopo
			.logout(cust -> cust.logoutRequestMatcher(new AntPathRequestMatcher("/logout")).logoutSuccessUrl("/").permitAll());

		return http.build();
	}
	
	@Bean
	public AuthorizationServerSettings providerSettings(SecurityProperties props) {
	    return AuthorizationServerSettings.builder()
	      .issuer(props.getProviderUrl()) //"http://auth-server:8081"
	      .build();
	}
	
	//Clients Apps
	@Bean
	public RegisteredClientRepository registeredClientRepository1(
			PasswordEncoder passwordEncoder, JdbcOperations jdbcOperations) 
	{

		RegisteredClient algafoodbackend = RegisteredClient.withId("algafood-web")
				.clientId("algafood-web")
				.clientSecret(passwordEncoder.encode("123"))
				.clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
				.authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
//nao funciona com client_credentials. Refresh token sao usados para evitar 
//pedir novamente as credenciais para o Resource_owner.				
//				.authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
				.scope("write")
				.scope("read")
				.tokenSettings(
						TokenSettings.builder()
						.accessTokenFormat(OAuth2TokenFormat.REFERENCE)//Opaque Tokens
						.accessTokenTimeToLive(Duration.ofMinutes(30))
//						//nao faz sentido para client credentials
//						.reuseRefreshTokens(false)
//						.refreshTokenTimeToLive(Duration.ofMinutes(60*12))						
						.build())
				.build();
		
		RegisteredClient postman1 = RegisteredClient.withId("postman1")
				.clientId("postman1")
				.clientSecret(passwordEncoder.encode("123"))
				.clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
				.authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
				.authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
				.authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
				//escopos que o cliente pode usar
				.scope("write")
				.scope("read")
				.tokenSettings(
						TokenSettings.builder()
						.accessTokenFormat(OAuth2TokenFormat.SELF_CONTAINED)//Opaque Tokens
						.accessTokenTimeToLive(Duration.ofMinutes(60*3))
						.reuseRefreshTokens(false)
						.refreshTokenTimeToLive(Duration.ofMinutes(60*12))
						.build())
				.redirectUri("https://oidcdebugger.com/debug") //nao pode usar 'localhost'
				.redirectUri("http://127.0.0.1:8080/authorize")
				.redirectUri("https://oauth.pstmn.io/v1/callback")//posman
				.redirectUri("http://localhost:8080/swagger-ui/oauth2-redirect.html")//swagger-ui				
				.postLogoutRedirectUri("http://127.0.0.1:8080/") // ??????
				.clientSettings(ClientSettings.builder()
						.requireAuthorizationConsent(true)//!!! cliente precisa autorizar um escopo
						//.requireProofKey(false)  //testar: obrigar o uso de PKCE ou state ??
						.build())
				.build();
		
//A implementacao AUTHORIZATION_CODE aceita uso de PKCE
// dependendo se o client usa ou nao o "code_challenge"
		RegisteredClient javascript1 = RegisteredClient.withId("javascript1")
				.clientId("javascript1")
				.clientSecret(passwordEncoder.encode("123"))
				//CLIENT_SECRET_BASIC autenticação BASIC
				//CLIENT_SECRET_POST  credenciais são enviadas como parametros no POST (client_id, client_secret)
				//Com CLIENT_SECRET_POST e PKCE podemos passar apenas "client_id" , configurando a senha vazia "".
				.clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
				.authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
				.authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
				.authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
				.scope("write")
				.scope("read")
				.scope(OidcScopes.OPENID)
				.tokenSettings(
						TokenSettings.builder()
						.accessTokenFormat(OAuth2TokenFormat.SELF_CONTAINED) //JWT
						.accessTokenTimeToLive(Duration.ofMinutes(30))
						.reuseRefreshTokens(false)
						.refreshTokenTimeToLive(Duration.ofMinutes(60*12))
						.build())
				.redirectUri("http://127.0.0.1:5555/index.html") //servidor de teste VSCode nao pode usar 'localhost'
				.clientSettings(ClientSettings.builder()
						.requireAuthorizationConsent(true)//!!! nao consede permissões automaticamente. O usuario TEM que consentir no processo de login e autorizar um dos escopos
						.requireProofKey(false)//se 'true': obriga o uso de PKCE no authorization_code
						.build())
				.build();

		
				RegisteredClient device1 = RegisteredClient.withId("device1")
						.clientId("device1")
						.clientSecret(passwordEncoder.encode("123"))
						.clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
						.authorizationGrantType(AuthorizationGrantType.DEVICE_CODE)
						.authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
						.scope("write")
						.scope("read")
						.scope(OidcScopes.OPENID)
						.tokenSettings(
								TokenSettings.builder()
								.accessTokenFormat(OAuth2TokenFormat.SELF_CONTAINED) //JWT
								.accessTokenTimeToLive(Duration.ofMinutes(30))
								.reuseRefreshTokens(false)
								.refreshTokenTimeToLive(Duration.ofMinutes(60*12))
								.build())
						.clientSettings(ClientSettings.builder()
								.requireAuthorizationConsent(true)//!!! nao consede permissões automaticamente. O usuario TEM que consentir no processo de login e autorizar um dos escopos
								.build())
						.build();
				
		//Usado somente para introspecção
		RegisteredClient resourceServer = RegisteredClient.withId("resource-server")
				.clientId("resource-server")
				.clientSecret(passwordEncoder.encode("123"))
				.clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
				.authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
				//.scope("write")
				//.scope("read")
				//.tokenSettings(
				//		TokenSettings.builder()
				//		.accessTokenFormat(OAuth2TokenFormat.REFERENCE)
				//		.accessTokenTimeToLive(Duration.ofMinutes(30)).build())
				.build();

//salva a configuração de clientes no banco de dados. 		
//		JdbcRegisteredClientRepository rep = 
//				new JdbcRegisteredClientRepository(jdbcOperations);
//		rep.save(postman1);
//		rep.save(javascript1);
//		rep.save(device1);
//		rep.save(algafoodbackend);
//		rep.save(resourceServer);
//		//return rep;
	
		return new InMemoryRegisteredClientRepository(Arrays.asList(
				algafoodbackend,resourceServer,postman1,javascript1,device1));
	}

	
//Este metodo implementa o uso do banco de dados para os Clientes.
//Descomentar e eliminar o metodo acima In Memory.
//    @Bean
    public RegisteredClientRepository registeredClientRepository(
    		PasswordEncoder passwordEncoder,
    		JdbcOperations jdbcOperations) 
    {
        return new JdbcRegisteredClientRepository(jdbcOperations);
    }
    
    
    //UTIL quando os clients estao gravados no banco pois eh muito dificil alterar lá.
    //Fonte: https://www.appsdeveloperblog.com/spring-authorization-server-tutorial/
    /*
    requireAuthorizationConsent: This property determines whether the authorization server requires user consent for each authorization request or not. If set to false, the user consent screen is skipped, and the authorization server grants the requested scopes automatically.
    requireProofKey: This property determines whether the authorization server requires proof of possession of a key for each authorization request or not. If set to false, the authorization server does not enforce PKCE (Proof Key for Code Exchange) validation.
    The method returns a ClientSettings object with these properties set to false, which means that the authorization server does not require user consent or proof of key for any client.    
     */
    //@Bean
    public ClientSettings clientSettings() {
        return ClientSettings.builder()
                .requireAuthorizationConsent(false)
                .requireProofKey(false)
                .build();
    }    

//Salva todas as altorizações geradas.
//
//Este metodo implementa a persistencia das autorizações com os Tokens no Banco de dados.
//só descomentar o Bean
//
//	//Habilitar JDBC para armazenar os tokens. 
//  //Util para tokens opacos : REFERENCE
    @Bean
    public OAuth2AuthorizationService oAuth2AuthorizationService(
    		JdbcOperations jdbcOperations,
    		RegisteredClientRepository registeredClientRepository) 
    {
        return new JdbcOAuth2AuthorizationService(
                jdbcOperations,
                registeredClientRepository
        );
    }	
    
    
	
	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	

//Foi implmentado via banco de dados na classe JpaUserDetailService	
//	//Resource Owner
//	@Bean
	public UserDetailsService userDetailsService(PasswordEncoder enc) {
		
		UserDetails userDetails = User
				.withUsername("claudio")
				.password(enc.encode("123"))
				.roles("admin")
				.build();

		return new InMemoryUserDetailsManager(userDetails);
	}	
	
    @Bean
    public OAuth2AuthorizationConsentService consentService(
    		//JdbcOperations jdbcOperations,
    		RegisteredClientRepository clientRepository) 
    {
    	return new InMemoryOAuth2AuthorizationConsentService();
    	
    	//nao vejo porque gravar isso no banco de dados.
        //return new JdbcOAuth2AuthorizationConsentService(jdbcOperations, clientRepository);
    }
    
//Solucao algaworks
    @Bean
    public JWKSource<SecurityContext> jwkSource(JwtKeyStoreProperties properties) 
    throws Exception 
    {
    	//23.9
    	//obter o keystore (do classpath)
//		var jksResource = new ClassPathResource("keystores/algafood.jks");
//		char[] keyStorePass = "authserver".toCharArray();
//		var keypairAlias = "authserver";
		
    	//obter o keystore (do aplication.properties - armazenado no formato BASE64)
    	//ver Base64ProtocolResolver.class criada paraconverter os dados em "Resource"
        char[] keyStorePass = properties.getPassword().toCharArray();
        String keypairAlias = properties.getKeypairAlias();
        Resource jksResource = properties.getJksLocation();
        
        //ler o keystore
        InputStream inputStream = jksResource.getInputStream();
        KeyStore keyStore = KeyStore.getInstance("JKS");//java key store
        keyStore.load(inputStream, keyStorePass);

        RSAKey rsaKey = RSAKey.load(keyStore, keypairAlias, keyStorePass);

        return new ImmutableJWKSet<>(new JWKSet(rsaKey));
    }
    
    
//Gera uma chave diferente toda vez que o servidor levanta,
//
	//Each authorization server needs its signing key for tokens to keep a proper 
	//boundary between security domains. Let's generate a 2048-byte RSA key:
	//@Bean
	public JWKSource<SecurityContext> jwkSource() {
		KeyPair keyPair = generateRsaKey();
		RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
		RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
		
		//JWK - Public and private RSA JSON Web Key. 
		RSAKey rsaKey = new RSAKey.Builder(publicKey)
				.privateKey(privateKey)
				.keyID(UUID.randomUUID().toString())
				.build();
		//Represent a JSON object that contains an array of JSON Web Keys (JWKs)
		JWKSet jwkSet = new JWKSet(rsaKey);
		return new ImmutableJWKSet<>(jwkSet);
	}
    
	private static KeyPair generateRsaKey() {
		KeyPair keyPair;
		try {
			KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
			keyPairGenerator.initialize(2048);
			keyPair = keyPairGenerator.generateKeyPair();
		}
		catch (Exception ex) {
			throw new IllegalStateException(ex);
		}
		return keyPair;
	}

	@Bean
	public JwtDecoder jwtDecoder(JWKSource<SecurityContext> jwkSource) {
		return OAuth2AuthorizationServerConfiguration.jwtDecoder(jwkSource);
	}
	
	
    
	
	//Customizar o token jwt com outros dados do usuario e suas Grants.
	//Para adicionar outros dados do usuario é preciso novo acesso ao banco , 
	//por isso o repositorio com dependencia de injecao.
	
    @Bean
    public OAuth2TokenCustomizer<JwtEncodingContext> jwtCustomizer(
    		UsuarioRepository usuarioRepository) 
    {
        return jwtEncodingContext -> {
            Authentication authentication = jwtEncodingContext.getPrincipal();
            
            //verificar - nem todos os fluxos tem informação do usuario. Ex: client crredentials
            if (authentication.getPrincipal() instanceof User) {
                User user = (User) authentication.getPrincipal();

                Usuario usuario = usuarioRepository.findByEmail(user.getUsername()).orElseThrow();

                Set<String> authorities = new HashSet<>();
                for (GrantedAuthority authority : user.getAuthorities()) {
                    authorities.add(authority.getAuthority());
                }

                //Os valores precisam ser strings
                jwtEncodingContext.getClaims().claim("usuario_id", usuario.getId().toString());
                jwtEncodingContext.getClaims().claim("authorities", authorities);
            }
        };
    }	
    
    
    @Bean
    public AuthQueryService auth2AuthorizationQueryService(
    		JdbcOperations jdbcOperations,
    		RegisteredClientRepository repository) 
    {
        return new AuthQueryServiceImpl(jdbcOperations, repository);
    }    
}
