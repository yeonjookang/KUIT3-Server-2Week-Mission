package http.response;

import http.constatnt.FilePath;
import http.request.HttpHeaders;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;

public class HttpResponse {
    private final HttpResponseStartLine startLine = new HttpResponseStartLine();
    private HttpHeaders httpHeaders;
    private byte[] body;
    private final OutputStream os;

    public HttpResponse(OutputStream os) {
        this.os = os;
        this.body = new byte[0];
        httpHeaders = new HttpHeaders(new HashMap<>());
        httpHeaders.put("Content-Type","text/html;charset-utf-8");
    }

    public void putHeader(String key, String value){
        httpHeaders.put(key,value);
    }

    private void setBody(String path) throws IOException {
        byte[] body = Files.readAllBytes(Paths.get(FilePath.WebappPath.getPath() + path));
        putHeader("Content-Length",String.valueOf(body.length));
        this.body=body;
    }

    private void write() throws IOException {
        os.write((startLine.getHttpVersion() +" "+startLine.getStatusCode()+" "+startLine.getHttpStatus()+"\r\n").getBytes());
        os.write(httpHeaders.toString().getBytes());
        os.write(body);
        os.flush();
        os.close();
    }

    public void forward(String path) throws IOException {
        setBody(path);
        if (isHtml(path)){
            write();
            return;
        }
        putHeader("Content-Type","text/css");
        write();
    }

    public void redirect(String path) throws IOException {
        startLine.setHttpStatus("Redirect");
        startLine.setStatusCode("302");
        putHeader("Location", path);
        write();
    }

    private boolean isHtml(String path) {
        String[] paths = path.split("\\.");
        return paths[paths.length-1].equals("html");
    }


}
