package webserver;

import db.MemoryUserRepository;
import http.request.HttpRequest;
import http.response.HttpResponse;
import model.User;

import java.io.*;
import java.net.Socket;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import static http.constatnt.FilePath.*;
import static http.constatnt.HttpMethod.GET;
import static http.constatnt.HttpMethod.POST;
import static http.constatnt.Url.*;

public class RequestHandler implements Runnable{
    Socket connection;
    private final MemoryUserRepository memoryUserRepository = MemoryUserRepository.getInstance();
    private static final Logger log = Logger.getLogger(RequestHandler.class.getName());

    public RequestHandler(Socket connection) {

        this.connection = connection;
    }

    @Override
    public void run() {
        log.log(Level.INFO, "New Client Connect! Connected IP : " + connection.getInetAddress() + ", Port : " + connection.getPort());
        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()){
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            DataOutputStream dos = new DataOutputStream(out);

            HttpRequest httpRequest = HttpRequest.from(br);
            HttpResponse httpResponse = new HttpResponse(dos);

            /**
             * 요구사항 1번 - index.html 반환하기
             * localhost/ 혹은 localhost/index.html으로 요청이 들어오면 webapp/index.html 반환
             */
            if(httpRequest.getMethod().equals(GET.getMethod())
                    && httpRequest.getUrl().endsWith(".html")) {
                httpResponse.forward(httpRequest.getUrl());
            }

            /**
             * 요구사항 2,4번 - GET 방식으로 회원가입하기 + 302 status code 적용
             * queryString으로 들어온 정보를 이용해 User 정보를 저장하고 index.html 반환
             */
            if(httpRequest.getMethod().equals(GET.getMethod())
                    && httpRequest.getUrl().equals(SIGNUP.getUrl())){
                User user = parseUserFromQueryString(httpRequest);
                memoryUserRepository.addUser(user);

                httpResponse.redirect(HomePagePath.getPath());
            }

            /**
             * 요구사항 3,4번 - POST 방식으로 회원가입하기 + 302 status code 적용
             * request body에 들어있는 queryString 정보를 이용하여 2번과 동일하게 수행
             */
            if(httpRequest.getMethod().equals(POST.getMethod())
                    && httpRequest.getUrl().equals(SIGNUP.getUrl())){
                User user = parseUserFromBody(httpRequest);
                memoryUserRepository.addUser(user);

                httpResponse.redirect(HomePagePath.getPath());
            }

            /**
             * 요구사항 5번 - 로그인 하기
             * repository에서 유저 정보를 비교 후 성공 여부 판단
             * 성공 => Cookie: logined=true를 추가 + index.html 화면으로 redirect
             * 실패 => login_failed.html로 redirect
             */
            if(httpRequest.getMethod().equals(POST.getMethod())
                    &&httpRequest.getUrl().equals(LOGIN.getUrl())){
                User user = parseUserFromBody(httpRequest);
                User findUser = memoryUserRepository.findUserById(user.getUserId());

                if(findUser!=null && findUser.getPassword().equals(user.getPassword())){
                    String cookie = "logined=true";
                    httpResponse.putHeader("Cookie",cookie);

                    httpResponse.redirect(HomePagePath.getPath());
                }
                httpResponse.redirect(LoginFailPath.getPath());
            }

            /**
             * 요구사항 6번 - 사용자 목록 출력
             * 요청 Cookie 헤더를 통해 로그인된 사용자인지 확인하자.
             * 로그인한 사용자 - userlist 버튼 클릭 시 user list 출력
             * 로그인 안된 사용자 - login.html 화면으로 redirect
             */
            if(httpRequest.getMethod().equals(GET.getMethod())
                    &&httpRequest.getUrl().equals(USERLIST.getUrl())){
                String cookie= httpRequest.getHeader("Cookie");
                String location;
                if (cookie.isEmpty())
                    location = LoginPath.getPath();
                else location = UserListPath.getPath();

                httpResponse.redirect(location);
                return;
            }

            /**
             * 요구사항 7번 - CSS 출력
             * 현재 styles.css가 적용되어있지 않다.
             * => url이 css 확장자로 끝난다면 Content-type을 text/css로 설정하고 반환
             */
            if(httpRequest.getUrl().endsWith(".css")){
                httpResponse.redirect(httpRequest.getUrl());
            }

        } catch (IOException e) {
            log.log(Level.SEVERE,e.getMessage());
        }
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