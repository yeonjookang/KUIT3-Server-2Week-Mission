package http.request;

import http.constatnt.FilePath;
import http.util.HttpRequestUtils;
import http.util.IOUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Map;

import static http.constatnt.FilePath.HomePagePath;

public class HttpRequest {
    private final HttpRequestStartLine httpRequestStartLine;
    private final HttpHeaders headers;
    private final String body;

    public HttpRequest(HttpRequestStartLine httpRequestStartLine, HttpHeaders headers, String body) {
        this.httpRequestStartLine = httpRequestStartLine;
        this.headers = headers;
        this.body = body;
    }

    public static HttpRequest from(BufferedReader br) throws IOException {
        String startLine = br.readLine();
        if(startLine==null){
            throw new IllegalArgumentException("HTTP 요청이 비어있습니다.");
        }
        HttpRequestStartLine httpRequestStartLine = HttpRequestStartLine.from(startLine);
        HttpHeaders httpHeaders = HttpHeaders.from(br);
        String body = readBody(br,httpHeaders);

        return new HttpRequest(httpRequestStartLine,httpHeaders,body);
    }

    private static String readBody(BufferedReader br, HttpHeaders httpHeaders) throws IOException {
        int contentLength = Integer.parseInt(httpHeaders.getHeader("Content-Length"));
        return IOUtils.readData(br,contentLength);
    }

    public String getQueryParameter(String key){
        return httpRequestStartLine.getQueryParameter(key);
    }

    public Map<String,String> getQueryParametersFromBody(){
        return HttpRequestUtils.parseQueryParameter(body);
    }

    public String getUrl(){
        if(httpRequestStartLine.getPath().equals("/"))
            return HomePagePath.getPath();
        return httpRequestStartLine.getPath();
    }

    public String getMethod(){
        return httpRequestStartLine.getMethod();
    }

    public String getHeader(String headerKey){
        return headers.getHeader(headerKey);
    }
}
