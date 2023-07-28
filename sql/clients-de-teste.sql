INSERT INTO `oauth2_registered_client` 
(`id`, `client_id`, `client_id_issued_at`, `client_secret`, `client_secret_expires_at`, 
 `client_name`, `client_authentication_methods`, `authorization_grant_types`, `redirect_uris`, 
 `post_logout_redirect_uris`, `scopes`, 
 `client_settings`, `token_settings`
 ) VALUES (
"algafood-web","algafood-web","2023-06-04 11:18:04","$2a$10$ak.wHLUhZHHRHBI4/X9KR.3PWSn.6h6ebcmhUf02n4QzFpC9bUbBy",NULL,
"algafood-web","client_secret_basic","client_credentials","",
"","read,write",

"{\"@class\":\"java.util.Collections$UnmodifiableMap\",
  \"settings.client.require-proof-key\":false,
  \"settings.client.require-authorization-consent\":false}",

"{\"@class\":\"java.util.Collections$UnmodifiableMap\",
  \"settings.token.reuse-refresh-tokens\":true,
  \"settings.token.id-token-signature-algorithm\":[\"org.springframework.security.oauth2.jose.jws.SignatureAlgorithm\",\"RS256\"],
  \"settings.token.access-token-time-to-live\":[\"java.time.Duration\",1800.000000000],
  \"settings.token.access-token-format\":{\"@class\":\"org.springframework.security.oauth2.server.authorization.settings.OAuth2TokenFormat\",\"value\":\"reference\"},
  \"settings.token.refresh-token-time-to-live\":[\"java.time.Duration\",3600.000000000],
  \"settings.token.authorization-code-time-to-live\":[\"java.time.Duration\",300.000000000],
  \"settings.token.device-code-time-to-live\":[\"java.time.Duration\",300.000000000]}"
);

INSERT INTO `oauth2_registered_client` 
(`id`, `client_id`, `client_id_issued_at`, `client_secret`, `client_secret_expires_at`, 
 `client_name`, `client_authentication_methods`, `authorization_grant_types`, `redirect_uris`, 
 `post_logout_redirect_uris`, `scopes`, 
 `client_settings`, `token_settings`
) VALUES (
 "device1","device1","2023-06-04 11:18:04","$2a$10$OsvixDzroXaXtGKsTtww5O3AWnCaeRWQRRFDB/oaL3KECUaG1uhAy",NULL,
 "device1","client_secret_basic","refresh_token,urn:ietf:params:oauth:grant-type:device_code","",
 "","read,openid,write",
 
 "{\"@class\":\"java.util.Collections$UnmodifiableMap\",
   \"settings.client.require-proof-key\":false,
   \"settings.client.require-authorization-consent\":true}",
   
 "{\"@class\":\"java.util.Collections$UnmodifiableMap\",
   \"settings.token.reuse-refresh-tokens\":false,
   \"settings.token.id-token-signature-algorithm\":[\"org.springframework.security.oauth2.jose.jws.SignatureAlgorithm\",\"RS256\"],
   \"settings.token.access-token-time-to-live\":[\"java.time.Duration\",1800.000000000],
   \"settings.token.access-token-format\":{\"@class\":\"org.springframework.security.oauth2.server.authorization.settings.OAuth2TokenFormat\",\"value\":\"self-contained\"},
   \"settings.token.refresh-token-time-to-live\":[\"java.time.Duration\",43200.000000000],
   \"settings.token.authorization-code-time-to-live\":[\"java.time.Duration\",300.000000000],
   \"settings.token.device-code-time-to-live\":[\"java.time.Duration\",300.000000000]}"
);

INSERT INTO `oauth2_registered_client` 
(`id`, `client_id`, `client_id_issued_at`, `client_secret`, `client_secret_expires_at`, 
 `client_name`, `client_authentication_methods`, `authorization_grant_types`, `redirect_uris`, 
 `post_logout_redirect_uris`, `scopes`, 
 `client_settings`, `token_settings`
) VALUES (
"javascript1","javascript1","2023-06-04 11:18:04","$2a$10$UbGEG9PKi1jXoVGSGVBJ0Oq/q/GuzSc4Gd2lKKWJ5IcL/McEjk8z6",NULL,
"javascript1","client_secret_basic","refresh_token,client_credentials,authorization_code","http://127.0.0.1:5555/index.html",
"","read,openid,write",

"{\"@class\":\"java.util.Collections$UnmodifiableMap\",
  \"settings.client.require-proof-key\":false,
  \"settings.client.require-authorization-consent\":true}",
  
"{\"@class\":\"java.util.Collections$UnmodifiableMap\",
  \"settings.token.reuse-refresh-tokens\":false,
  \"settings.token.id-token-signature-algorithm\":[\"org.springframework.security.oauth2.jose.jws.SignatureAlgorithm\",\"RS256\"],
  \"settings.token.access-token-time-to-live\":[\"java.time.Duration\",1800.000000000],
  \"settings.token.access-token-format\":{\"@class\":\"org.springframework.security.oauth2.server.authorization.settings.OAuth2TokenFormat\",\"value\":\"self-contained\"},
  \"settings.token.refresh-token-time-to-live\":[\"java.time.Duration\",43200.000000000],
  \"settings.token.authorization-code-time-to-live\":[\"java.time.Duration\",300.000000000],
  \"settings.token.device-code-time-to-live\":[\"java.time.Duration\",300.000000000]}"
);

INSERT INTO `oauth2_registered_client` 
(`id`, `client_id`, `client_id_issued_at`, `client_secret`, `client_secret_expires_at`, 
 `client_name`, `client_authentication_methods`, `authorization_grant_types`, `redirect_uris`, 
 `post_logout_redirect_uris`, `scopes`, `client_settings`, `token_settings`
) VALUES (
"postman1","postman1","2023-06-04 11:18:04","$2a$10$Df..NHtXDTlXuvYFf4QldeW6kmyxU39lpD.aUy9oMLXkA0skDFmFS",NULL,
"postman1","client_secret_basic","refresh_token,client_credentials,authorization_code","http://localhost:8080/swagger-ui/oauth2-redirect.html,https://oidcdebugger.com/debug,http://127.0.0.1:8080/authorize,https://oauth.pstmn.io/v1/callback",
"http://127.0.0.1:8080/","read,write",

"{\"@class\":\"java.util.Collections$UnmodifiableMap\",
  \"settings.client.require-proof-key\":false,
  \"settings.client.require-authorization-consent\":true}",
  
"{\"@class\":\"java.util.Collections$UnmodifiableMap\",
  \"settings.token.reuse-refresh-tokens\":false,
  \"settings.token.id-token-signature-algorithm\":[\"org.springframework.security.oauth2.jose.jws.SignatureAlgorithm\",\"RS256\"],
  \"settings.token.access-token-time-to-live\":[\"java.time.Duration\",10800.000000000],
  \"settings.token.access-token-format\":{\"@class\":\"org.springframework.security.oauth2.server.authorization.settings.OAuth2TokenFormat\",\"value\":\"self-contained\"},
  \"settings.token.refresh-token-time-to-live\":[\"java.time.Duration\",43200.000000000],
  \"settings.token.authorization-code-time-to-live\":[\"java.time.Duration\",300.000000000],
  \"settings.token.device-code-time-to-live\":[\"java.time.Duration\",300.000000000]}"
);

INSERT INTO `oauth2_registered_client` 
(`id`, `client_id`, `client_id_issued_at`, `client_secret`, `client_secret_expires_at`, 
`client_name`, `client_authentication_methods`, `authorization_grant_types`, `redirect_uris`, 
`post_logout_redirect_uris`, `scopes`, `client_settings`, `token_settings`
) VALUES (
"resource-server","resource-server","2023-06-04 11:18:04","$2a$10$RhZHrEkm.PMlN3Pkov6ECuDONSTsBcO9UwO9tJXXaWhdn/DSTcd6q",NULL,
"resource-server","client_secret_basic","client_credentials","",
"","",

"{\"@class\":\"java.util.Collections$UnmodifiableMap\",
  \"settings.client.require-proof-key\":false,
  \"settings.client.require-authorization-consent\":false}",
  
"{\"@class\":\"java.util.Collections$UnmodifiableMap\",
  \"settings.token.reuse-refresh-tokens\":true,
  \"settings.token.id-token-signature-algorithm\":[\"org.springframework.security.oauth2.jose.jws.SignatureAlgorithm\",\"RS256\"],
  \"settings.token.access-token-time-to-live\":[\"java.time.Duration\",300.000000000],
  \"settings.token.access-token-format\":{\"@class\":\"org.springframework.security.oauth2.server.authorization.settings.OAuth2TokenFormat\",\"value\":\"self-contained\"},
  \"settings.token.refresh-token-time-to-live\":[\"java.time.Duration\",3600.000000000],
  \"settings.token.authorization-code-time-to-live\":[\"java.time.Duration\",300.000000000],
  \"settings.token.device-code-time-to-live\":[\"java.time.Duration\",300.000000000]}"
);






