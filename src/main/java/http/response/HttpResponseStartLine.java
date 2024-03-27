package http.response;

public class HttpResponseStartLine {
    private String httpVersion = "HTTP/1.1";
    private String statusCode = "200";
    private String httpStatus = "OK";

    public HttpResponseStartLine() {
    }

    public String getHttpVersion() {
        return httpVersion;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public String getHttpStatus() {
        return httpStatus;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public void setHttpStatus(String httpStatus) {
        this.httpStatus = httpStatus;
    }
}
