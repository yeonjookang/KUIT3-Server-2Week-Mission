package controller;

import db.MemoryUserRepository;
import http.request.HttpRequest;
import http.response.HttpResponse;
import model.User;

import java.io.IOException;
import java.util.Map;

import static http.constatnt.FilePath.HomePagePath;
import static http.constatnt.FilePath.LoginFailPath;

public class LoginController implements Controller{
    private final MemoryUserRepository memoryUserRepository = MemoryUserRepository.getInstance();
    @Override
    public void execute(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException {
        Map<String, String> queryParameter = httpRequest.getQueryParametersFromBody();
        User findUser = memoryUserRepository.findUserById(queryParameter.get("userId"));

        if(findUser!=null && findUser.getPassword().equals(queryParameter.get("password"))){
            String cookie = "logined=true";
            httpResponse.putHeader("Cookie",cookie);

            httpResponse.redirect(HomePagePath.getPath());
        }
        httpResponse.redirect(LoginFailPath.getPath());
    }
}
