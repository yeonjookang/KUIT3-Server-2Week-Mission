package http.request;

import http.util.HttpRequestUtils;

import java.util.HashMap;
import java.util.Map;

public class HttpRequestStartLine {
    private static final int START_LINE_MIN_LENGTH = 3;
    private static final String DISCRIMINATOR = " ";
    private static final String PARAM_DISCRIMINATOR = "\\?";
    private final String method;
    private final String path;
    private final Map<String,String> query;
    private final String version;

    public HttpRequestStartLine(String method, String path, Map<String,String> query, String version) {
        this.method = method;
        this.path = path;
        this.query = query;
        this.version = version;
    }

    private HttpRequestStartLine from(String startLine){
        String[] startLines = startLine.split(DISCRIMINATOR);
        validationStartLineLength(startLines);

        String method = startLines[0];
        String url = startLines[1];
        String[] parsePaths = parsePath(url);
        String path = parsePaths[0];

        Map<String,String> parseQuery = new HashMap<>();
        if(parsePaths.length>1)
            parseQuery = HttpRequestUtils.parseQueryParameter(parsePaths[1])''

        String version = startLines[2];

        return new HttpRequestStartLine(method,path,parseQuery,version);
    }

    private String[] parsePath(String url) {
        return url.split(PARAM_DISCRIMINATOR);
    }

    private void validationStartLineLength(String[] startLines) {
        if(startLines.length<START_LINE_MIN_LENGTH){
            throw new IllegalArgumentException("요청 StartLine 정보가 잘못되었습니다.");
        }
    }

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public String getQueryParameter(String key) {
        return query.get(key);
    }

    public String getVersion() {
        return version;
    }
}
