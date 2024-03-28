package webserver;

import controller.*;
import http.constatnt.Url;
import http.request.HttpRequest;
import http.response.HttpResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class RequestMapper {
    private final HttpRequest httpRequest;
    private final HttpResponse httpResponse;
    private static final Map<String, Controller>  controllers = new HashMap<>();
    private final Controller controller;
    public RequestMapper(HttpRequest httpRequest, HttpResponse httpResponse){
        this.httpRequest=httpRequest;
        this.httpResponse=httpResponse;
        this.controller = controllers.get(httpRequest.getUrl());
    }
    static {
        controllers.put(Url.SIGNUP.getUrl(),new SignUpController());
        controllers.put(Url.LOGIN.getUrl(), new LoginController());
        controllers.put(Url.USERLIST.getUrl(), new ListController());
        controllers.put(Url.HOME.getUrl(),new ForwardController());
    }
    public void proceed() throws IOException, IOException {
        if (controller != null) {
            controller.execute(httpRequest, httpResponse);
            return;
        }
        httpResponse.forward(httpRequest.getUrl());
    }
}
