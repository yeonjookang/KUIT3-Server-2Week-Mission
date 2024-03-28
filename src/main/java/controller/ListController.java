package controller;

import http.constatnt.FilePath;
import http.request.HttpRequest;
import http.response.HttpResponse;

import java.io.IOException;

import static http.constatnt.FilePath.LoginPath;
import static http.constatnt.FilePath.UserListPath;

public class ListController implements Controller{
    @Override
    public void execute(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException {
        String cookie= httpRequest.getHeader("Cookie");
        String location;
        if (cookie.isEmpty()){
            location = LoginPath.getPath();
            //최초 요청한 url 정보가 아닌 새로 지정된 location 정보도 대체
            httpResponse.redirect(location);
        }
        else
            //최초 요청한 url 정보가 유효
            httpResponse.forward(UserListPath.getPath());
    }
}
