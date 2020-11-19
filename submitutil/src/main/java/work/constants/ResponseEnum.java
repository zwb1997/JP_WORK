package work.constants;

public enum ResponseEnum {

    SUCCESS("RECEIVE SUCCESS", 1), ERROR("FAILURE EXECUTION", 0);

    private String message;
    private int code;

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    private ResponseEnum(String message, int code) {
        this.message = message;
        this.code = code;
    }
}
