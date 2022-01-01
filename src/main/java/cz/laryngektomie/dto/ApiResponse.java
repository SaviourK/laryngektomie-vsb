package cz.laryngektomie.dto;

public class ApiResponse {

    private final boolean success;
    private final String text;

    public ApiResponse(boolean success, String text) {
        this.success = success;
        this.text = text;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getText() {
        return text;
    }
}
