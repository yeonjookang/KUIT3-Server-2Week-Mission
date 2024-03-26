package webserver;

import db.MemoryUserRepository;
import http.util.HttpRequestUtils;
import model.User;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RequestHandler implements Runnable{
    Socket connection;
    private final MemoryUserRepository memoryUserRepository = MemoryUserRepository.getInstance();
    private static final Logger log = Logger.getLogger(RequestHandler.class.getName());
    private static final String WebappPath = "C:/Users/rkddu/Documents/4학년 1학기/KUIT 서버/KUIT3_Backend-Java-Tomcat/webapp";

    public RequestHandler(Socket connection) {

        this.connection = connection;
    }

    @Override
    public void run() {
        log.log(Level.INFO, "New Client Connect! Connected IP : " + connection.getInetAddress() + ", Port : " + connection.getPort());
        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()){
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            DataOutputStream dos = new DataOutputStream(out);

            //요청 프로토콜의 startLine 분석
            String startLine = br.readLine();
            String[] startLines = startLine.split(" ");
            String method = startLines[0];
            String url = startLines[1];
            byte[] body= "Hello World".getBytes();;
            System.out.println(url);

            /**
             * 요구사항 1번 - index.html 반환하기
             * localhost/ 혹은 localhost/index.html으로 요청이 들어오면 webapp/index.html 반환
             */
            if(url.equals("/"))
                url="/index.html";
            if(method.equals("GET") && url.endsWith(".html"))
                body = Files.readAllBytes(Paths.get(WebappPath + url));

            /**
             * 요구사항 2번 - GET 방식으로 회원가입하기
             * queryString으로 들어온 정보를 이용해 User 정보를 저장하고 index.html 반환
             */
            if(method.equals("GET") && url.contains("?")){
                User user = extractUserFromUrl(url);
                memoryUserRepository.addUser(user);

                String location= "/index.html";
                response302Header(dos,location);
                responseBody(dos, body);
                return;
            }

            response200Header(dos, body.length);
            responseBody(dos, body);

        } catch (IOException e) {
            log.log(Level.SEVERE,e.getMessage());
        }
    }

    private void response302Header(DataOutputStream dos, String location) {
        try{
            dos.writeBytes("HTTP/1.1 302 Found \r\n");
            dos.writeBytes("Location: " + location);
            dos.writeBytes("\r\n");
        } catch (IOException e){
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    private void response200Header(DataOutputStream dos, int lengthOfBodyContent) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    private void responseBody(DataOutputStream dos, byte[] body) {
        try {
            dos.write(body, 0, body.length);
            dos.flush();
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    private User extractUserFromUrl(String url) {
        String[] urlSplit = url.split("\\?");
        String queryString = urlSplit[1];
        Map<String, String> queryStringMap = HttpRequestUtils.parseQueryParameter(queryString);

        String userId = queryStringMap.get("userId");
        String password = queryStringMap.get("password");
        String name = queryStringMap.get("name");
        String email = queryStringMap.get("email");

        return new User(userId, password, name, email);
    }
}