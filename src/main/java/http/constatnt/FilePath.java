package http.constatnt;

public enum FilePath {
    WebappPath("C:/Users/rkddu/Documents/4학년 1학기/KUIT 서버/KUIT3_Backend-Java-Tomcat/webapp"),
    HomePagePath("/index.html"),
    LoginFailPath("/user/login_failed.html"),
    LoginPath("/user/login.html"),
    UserListPath("/user/list.html");

    private String path;

    FilePath(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}
