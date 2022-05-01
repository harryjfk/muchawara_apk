package cu.inmobile.wara.Networking;

/**
 * Created by harry on 3/01/19.
 */

import org.jivesoftware.smack.packet.ExtensionElement;
import org.jivesoftware.smack.provider.ExtensionElementProvider;
import org.jivesoftware.smack.util.XmlStringBuilder;
import org.jivesoftware.smackx.chatstates.ChatState;
import org.xmlpull.v1.XmlPullParser;
public class AdditionalChatStateExtension implements ExtensionElement {

    public static final String NAMESPACE = "http://jabber.org/protocol/chatstates";

    private final AdditionalChatState state;

    /**
     * Default constructor. The argument provided is the state that the extension will represent.
     *
     * @param state the state that the extension represents.
     */
    public AdditionalChatStateExtension(AdditionalChatState state) {
        this.state = state;
    }

    @Override
    public String getElementName() {
        return state.name();
    }

    @Override
    public String getNamespace() {
        return NAMESPACE;
    }

    public AdditionalChatState getChatState() {
        return state;
    }

    @Override
    public XmlStringBuilder toXML() {
        XmlStringBuilder xml = new XmlStringBuilder(this);
        xml.closeEmptyElement();
        return xml;
    }

    public static class Provider extends ExtensionElementProvider<AdditionalChatStateExtension> {

        @Override
        public AdditionalChatStateExtension parse(XmlPullParser parser, int initialDepth) {
            AdditionalChatState state;
            try {
                state = AdditionalChatState.valueOf(parser.getName());
            }
            catch (Exception ex) {
                state = AdditionalChatState.recieved;
            }
            return new AdditionalChatStateExtension(state);
        }
    }
}
