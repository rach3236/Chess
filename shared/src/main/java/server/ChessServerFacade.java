package server;

import com.google.gson.Gson;
import datamodel.*;
import datamodel.Error;
import exception.ResponseException;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ChessServerFacade {

    private final HttpClient client = HttpClient.newHttpClient();
    private final String serverUrl;

    public ChessServerFacade(String url) {
        serverUrl = url;
    }

    public RegisterResponse register(UserData user) throws ResponseException {
        var request = buildRequest("POST", "/user", user, "");
        var response = sendRequest(request);
        return handleResponse(response, RegisterResponse.class);
    }

    public RegisterResponse login(UserData user) throws ResponseException {
        var request = buildRequest("POST", "/session", user, "");
        var response = sendRequest(request);
        return handleResponse(response, RegisterResponse.class);
    }

    //TO DO: probably a bool, some response to handle not getting an object back
    public Error logout(String auth) throws ResponseException {
        var request = buildRequest("DELETE", "/session", null, auth);
        var response = sendRequest(request);
        return handleResponse(response, Error.class);
    }

    public Games listGames(String auth) throws ResponseException {
        var request = buildRequest("GET", "/game", null, auth);
        var response = sendRequest(request);
        return handleResponse(response, Games.class);
    }

    public Games createGame(GameData game, String auth) throws ResponseException {
        var request = buildRequest("POST", "/game", game, auth);
        var response = sendRequest(request);
        return handleResponse(response, Games.class);
    }

    public Error joinPlayer(PlayerInfo player, String auth) throws ResponseException {
        var request = buildRequest("PUT", "/game", player, auth);
        var response = sendRequest(request);
        return handleResponse(response, Error.class);
    }

    public Error clear() throws ResponseException {
        var request = buildRequest("DELETE", "/db", null, null);
        var response = sendRequest(request);
        return handleResponse(response, Error.class);
    }

    private HttpRequest buildRequest(String method, String path, Object body, String authKey) {
        var request = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + path))
                .method(method, makeRequestBody(body));
        if (body != null) {
            request.setHeader("Content-Type", "application/json");
        }
        if (authKey != null && !authKey.isBlank()) {
            request.setHeader("authorization", authKey);
        }
        return request.build();
    }

    private HttpRequest.BodyPublisher makeRequestBody(Object request) {
        if (request != null) {
            return HttpRequest.BodyPublishers.ofString(new Gson().toJson(request));
        } else {
            return HttpRequest.BodyPublishers.noBody();
        }
    }

    private HttpResponse<String> sendRequest(HttpRequest request) throws ResponseException {
        try {
            return client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception ex) {
            throw new ResponseException(ResponseException.Code.ServerError, ex.getMessage());
        }
    }

    // Maybe handle not "response T" w/ a method

    private <T> T handleResponse(HttpResponse<String> response, Class<T> responseClass) throws ResponseException {
        var status = response.statusCode();
        if (!isSuccessful(status)) {
            var body = response.body();
            if (body != null) {
                throw ResponseException.fromJson(body);
            }

            throw new ResponseException(ResponseException.fromHttpStatusCode(status), "other failure: " + status);
        }

        if (responseClass != null) {
            return new Gson().fromJson(response.body(), responseClass);
        }

        return null;
    }

    private boolean isSuccessful(int status) {
        return status / 100 == 2;
    }
}
