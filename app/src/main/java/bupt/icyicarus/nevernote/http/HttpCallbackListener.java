package bupt.icyicarus.nevernote.http;

/**
 * Created by Icarus on 07/10/2016.
 */

public interface HttpCallbackListener {
    void onFinish(String response);

    void onError(Exception e);
}
