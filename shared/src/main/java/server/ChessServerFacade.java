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

    public ChessServerFacade(int port) {
        serverUrl = "http://localhost:" + port;
    }

    public RegisterResponse register(UserData user) throws Exception, ResponseException {
        var request = buildRequest("POST", "/user", user, "");
        var response = sendRequest(request);
        return handleResponse(response, RegisterResponse.class);
    }

    public LoginResponse login(UserData user) throws Exception {
        var request = buildRequest("POST", "/session", user, "");
        var response = sendRequest(request);
        return handleResponse(response, LoginResponse.class);
    }

    //TO DO: probably a bool, some response to handle not getting an object back
    public void logout(String auth) throws Exception {
        var request = buildRequest("DELETE", "/session", null, auth);
        var response = sendRequest(request);
        handleResponse(response, Error.class);
    }

    public Games listGames(String auth) throws Exception, ResponseException {
        var request = buildRequest("GET", "/game", null, auth);
        var response = sendRequest(request);
        return handleResponse(response, Games.class);
    }

    public GameID createGame(GameData game, String auth) throws Exception, ResponseException {
        var request = buildRequest("POST", "/game", game, auth);
        var response = sendRequest(request);
        return handleResponse(response, GameID.class);
    }

    public void joinPlayer(PlayerInfo player, String auth) throws Exception, ResponseException {
        var request = buildRequest("PUT", "/game", player, auth);
        var response = sendRequest(request);
        handleResponse(response, Error.class);
    }

    public void clear() throws Exception, ResponseException {
        var request = buildRequest("DELETE", "/db", null, null);
        var response = sendRequest(request);
        handleResponse(response, Error.class);
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

    private <T> T handleResponse(HttpResponse<String> response, Class<T> responseClass) throws Exception {
        var status = response.statusCode();
        switch (status) {
            case 400:
                throw new Exception("Oh no! Looks like one of the fields you entered is invalid! Please enter valid fields.");
            case 401:
                throw new Exception("Oh no! Looks like you're unauthorized for that action.");
            case 403:
                throw new Exception("Oh no! Looks like the information you've entered is already taken by another user:) \nPlease enter in an unique username and password");
            case 500:
                throw new Exception("Oh no! Looks like we've encountered an error!😬 Please try again later.");
            default:
                break;
        }
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
