const config = {
  scope: "read",
  clientId: "javascript1",
  clientSecret: "123",
  authorizeUrl: "http://auth-server:8081/oauth2/authorize",
  tokenUrl: "http://auth-server:8081/oauth2/token",
  callbackUrl: "http://127.0.0.1:5555/index.html",
  cozinhasUrl: "http://localhost:8080/v1/cozinhas"
};

let accessToken = "";

function consultar() {
  alert("Consultando recurso com access token " + accessToken);

  $.ajax({
    url: config.cozinhasUrl,
    type: "get",

    beforeSend: function(request) {
      request.setRequestHeader("Authorization", "Bearer " + accessToken);
    },

    success: function(response) {
      var json = JSON.stringify(response);
      $("#resultado").text(json);
    },

    error: function(error) {
      alert("Erro ao consultar recurso");
    }
  });
}


function login() {
  // https://auth0.com/docs/protocols/oauth2/oauth-state
  let state = btoa(Math.random());
  localStorage.setItem("clientState", state);

  window.location.href = `${config.authorizeUrl}?response_type=code&client_id=${config.clientId}&state=${state}&redirect_uri=${config.callbackUrl}&scope=${config.scope}`;
}

function gerarAccessToken(code) {
  alert("Gerando access token com code " + code);

  let clientAuth = btoa(config.clientId + ":" + config.clientSecret);
  
  let params = new URLSearchParams();
  params.append("grant_type", "authorization_code");
  params.append("code", code);
  params.append("redirect_uri", config.callbackUrl);

  $.ajax({
    url: config.tokenUrl,
    type: "post",
    data: params.toString(),
    contentType: "application/x-www-form-urlencoded",

    beforeSend: function(request) {
      request.setRequestHeader("Authorization", "Basic " + clientAuth);
    },

    success: function(response) {
      accessToken = response.access_token;

      alert("Access token gerado: " + accessToken);
    },

    error: function(error) {
      alert("Erro ao gerar access key");
    }
  });
}

$(document).ready(function() {
  let params = new URLSearchParams(window.location.search);

  let error = params.get("error");
  let error_description = params.get("error_description");

  let code = params.get("code");
  let state = params.get("state");
  let currentState = localStorage.getItem("clientState");

  if (error)
    alert(error + " - " + error_description);
  
  else if (code) {
    // window.history.replaceState(null, null, "/");

    if (currentState == state) {
      gerarAccessToken(code);
    } else {
      alert("State inválido");
    }
  }
});

$("#btn-consultar").click(consultar);
$("#btn-login").click(login);
