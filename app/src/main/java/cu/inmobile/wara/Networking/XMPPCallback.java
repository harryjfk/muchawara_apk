package cu.inmobile.wara.Networking;

import org.jivesoftware.smack.tcp.XMPPTCPConnection;

/**
 * Created by harry on 28/12/18.
 */

public interface XMPPCallback<T> {
    public void execute(T t, XMPPTCPConnection connection);
}