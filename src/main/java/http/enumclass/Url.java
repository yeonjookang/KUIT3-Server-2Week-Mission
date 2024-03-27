package http.enumclass;

public enum Url {
    HOME("/"),
    SIGNUP("/user/signup"),
    LOGIN("/user/login"),
    USERLIST("/user/userList")
    ;

    private String url;

    Url(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }
}
