package cu.inmobile.wara.Networking;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.packet.ExtensionElement;
import org.jivesoftware.smack.provider.EmbeddedExtensionProvider;
import org.jivesoftware.smack.util.PacketParserUtils;

/**
 * Created by harry on 2/01/19.
 */
public class ReadReceipt implements ExtensionElement
{

    public static final String NAMESPACE = "urn:xmpp:read";
    public static final String ELEMENT = "read";

    private String id; /// original ID of the delivered message

    public ReadReceipt(String id)
    {
        this.id = id;
    }

    public String getId()
    {
        return id;
    }

    @Override
    public String getElementName()
    {
        return ELEMENT;
    }

    @Override
    public String getNamespace()
    {
        return NAMESPACE;
    }

    @Override
    public String toXML()
    {
        return "<read xmlns='" + NAMESPACE + "' id='" + id + "'/>";
    }

    public static class Provider extends EmbeddedExtensionProvider<ExtensionElement>
    {
        @Override
        protected ExtensionElement createReturnExtension(String currentElement, String currentNamespace,
                                                         Map<String, String> attributeMap, List<? extends ExtensionElement> content)
        {
            return new ReadReceipt(attributeMap.get("id"));
        }
    }
}