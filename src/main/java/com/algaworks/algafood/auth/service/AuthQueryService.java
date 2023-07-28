package com.algaworks.algafood.auth.service;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;

import java.util.List;

public interface AuthQueryService {

    List<RegisteredClient> listClientsWithConsent(String principalName);
    List<OAuth2Authorization> listAuthorizations(String principalName, String clientId);

}