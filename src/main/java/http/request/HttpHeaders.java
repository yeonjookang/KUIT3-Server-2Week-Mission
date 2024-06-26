package http.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class HttpHeaders {
    private Map<String,String> headers;
    private static final String DISCRIMINATOR = ": ";

    public HttpHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public static HttpHeaders from(BufferedReader br) throws IOException {
        Map<String,String> headers = new HashMap<>();

        while(true){
            String line = br.readLine();
            if (line.equals("")){
                //헤더와 본문 사이에는 빈 줄이 있다. 따라서 해당 조건문에서는 무한 루프를 종료한다.
                break;
            }

            String[] header = line.split(DISCRIMINATOR);
            String headerKey = header[0].trim();
            String headerValue = header[1].trim();
            if(headerKey!=null)
                headers.put(headerKey,headerValue);
        }

        return new HttpHeaders(headers);
    }

    public String getHeader(String headerKey){
        if(headers.keySet().contains(headerKey))
            return headers.get(headerKey);
        return "";
    }

    public Set<String> getHeaderKeys(){
        return headers.keySet();
    }

    public void put(String key, String value) {
        headers.put(key,value);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        headers.forEach((key,value) -> sb.append(key).append(": ").append(value).append("\r\n"));

        return sb.append("\r\n").toString();
    }
}
