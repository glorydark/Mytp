package glorydark.utils;

public class Request {
    public long millis;

    public String sender; //发送者

    public String receiver; //接受者

    public RequestType requestType; //类型

    public Request(String sender, String receiver, RequestType requestType) {
        this.sender = sender;
        this.receiver = receiver;
        this.requestType = requestType;
        this.millis = System.currentTimeMillis();
    }

    public long getMillis() {
        return millis;
    }

    public String getReceiver() {
        return receiver;
    }

    public String getSender() {
        return sender;
    }

    public boolean isExpired() {
        return System.currentTimeMillis() - millis > 60000; //1分钟时长
    }
}
