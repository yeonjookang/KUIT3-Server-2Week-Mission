package controller;

import db.MemoryUserRepository;
import http.constatnt.HttpMethod;
import http.request.HttpRequest;
import http.response.HttpResponse;
import model.User;

import java.io.IOException;
import java.util.Map;

import static http.constatnt.FilePath.HomePagePath;

public class SignUpController implements Controller{
    private final MemoryUserRepository memoryUserRepository = MemoryUserRepository.getInstance();
    @Override
    public void execute(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException {
        User user;
        if(httpRequest.getMethod().equals(HttpMethod.GET.getMethod()))
            user = parseUserFromQueryString(httpRequest);
        else
            user = parseUserFromBody(httpRequest);

        memoryUserRepository.addUser(user);
        httpResponse.redirect(HomePagePath.getPath());
    }

    private User parseUserFromQueryString(HttpRequest httpRequest) {
        String userId = httpRequest.getQueryParameter("userId");
        String password = httpRequest.getQueryParameter("password");
        String name = httpRequest.getQueryParameter("name");
        String email = httpRequest.getQueryParameter("email");

        return new User(userId, password, name, email);
    }

    private User parseUserFromBody(HttpRequest httpRequest){
        Map<String, String> queryParametersFromBody = httpRequest.getQueryParametersFromBody();
        String userId = queryParametersFromBody.get("userId");
        String password = queryParametersFromBody.get("password");
        String name = queryParametersFromBody.get("name");
        String email = queryParametersFromBody.get("email");

        return new User(userId, password, name, email);
    }
}
