package webserver;

import controller.*;
import db.MemoryUserRepository;
import http.request.HttpRequest;
import http.response.HttpResponse;
import model.User;

import java.io.*;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

import static http.constatnt.HttpMethod.GET;
import static http.constatnt.HttpMethod.POST;
import static http.constatnt.Url.*;

public class RequestHandler implements Runnable{
    Socket connection;
    private static final Logger log = Logger.getLogger(RequestHandler.class.getName());
    private Controller controller = new ForwardController();

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
             * 요구사항 1,7번 - / 혹은 .html 혹은 .css 반환하기
             * 해당 파일로 forward 시키기
             */
            if(httpRequest.getMethod().equals(GET.getMethod())
                    && (httpRequest.getUrl().endsWith(".html") || httpRequest.getUrl().endsWith(".css"))) {
                controller = new ForwardController();
            }

            /**
             * 요구사항 2,3,4번 - GET 또는 POST 방식으로 회원가입하기 + 302 status code 적용
             * queryString으로 들어온 정보를 이용해 User 정보를 저장하고 index.html 반환
             */
            if(httpRequest.getMethod().equals(GET.getMethod())
                    && httpRequest.getUrl().equals(SIGNUP.getUrl())){
                controller = new SignUpController();
            }

            /**
             * 요구사항 5번 - 로그인 하기
             * repository에서 유저 정보를 비교 후 성공 여부 판단
             * 성공 => Cookie: logined=true를 추가 + index.html 화면으로 redirect
             * 실패 => login_failed.html로 redirect
             */
            if(httpRequest.getMethod().equals(POST.getMethod())
                    &&httpRequest.getUrl().equals(LOGIN.getUrl())){
                controller = new LoginController();
            }

            /**
             * 요구사항 6번 - 사용자 목록 출력
             * 요청 Cookie 헤더를 통해 로그인된 사용자인지 확인하자.
             * 로그인한 사용자 - userlist 버튼 클릭 시 user list 출력
             * 로그인 안된 사용자 - login.html 화면으로 redirect
             */
            if(httpRequest.getMethod().equals(GET.getMethod())
                    &&httpRequest.getUrl().equals(USERLIST.getUrl())){
                controller = new ListController();
            }

            controller.execute(httpRequest,httpResponse);
        } catch (IOException e) {
            log.log(Level.SEVERE,e.getMessage());
        }
    }
}