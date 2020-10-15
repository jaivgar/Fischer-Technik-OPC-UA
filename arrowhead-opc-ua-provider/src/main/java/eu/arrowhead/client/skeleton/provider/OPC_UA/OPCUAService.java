package eu.arrowhead.client.skeleton.provider.OPC_UA;

import eu.arrowhead.client.skeleton.provider.JSONReader;
import eu.arrowhead.client.skeleton.provider.Provider_Constants;
import se.jaime.properties.TypeSafeProperties;

import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.jose4j.json.internal.json_simple.parser.ParseException;

import java.io.IOException;

public class OPCUAService {
    
    private static final TypeSafeProperties props = TypeSafeProperties.getProp();
    
    // members
    String opcuaServerAddress = props.getProperty(Provider_Constants.ADDRESS_PLC_OPC_UA_SERVER, "192.168.1.1:4840");
    
    // Methods
    public String read(String ComponentId) throws IOException, ParseException {
        String returnval="";
        JSONReader reader= new JSONReader(ComponentId);
        String definition= reader.getDefinition();
        String nodeIdentifier= "\""+definition+"\"";
        NodeId nodeId = new NodeId(3, nodeIdentifier);
        //opcuaServerAddress = opcuaServerAddress.replaceAll("opc.tcp://", "");
        OPCUAConnection connection = new OPCUAConnection(opcuaServerAddress);
        try {
            returnval = OPCUAInteractions.readNode(connection.getConnectedClient(), nodeId);
            connection.dispose();
            return returnval;
        } catch (Exception ex) {
            connection.dispose();
            return "There was an error reading the OPC-UA node.";
        }
    }

    public String write(String ComponentId, String value) throws IOException, ParseException {
        String status="";

        boolean bolValue= false;
        if(value.equalsIgnoreCase ("true"))
            bolValue=true;
        else bolValue=false;
        JSONReader reader= new JSONReader(ComponentId);
        String definition= reader.getDefinition();
        String nodeIdentifier= "\""+definition+"\"";
        NodeId nodeId = new NodeId(3, nodeIdentifier);
        //opcuaServerAddress = opcuaServerAddress.replaceAll("opc.tcp://", "");
        OPCUAConnection connection = new OPCUAConnection(opcuaServerAddress);
        try {
            status = OPCUAInteractions.writeNode(connection.getConnectedClient(), nodeId, bolValue);
            connection.dispose();
            return status;
        } catch (Exception ex) {
            connection.dispose();
            return "There was an error reading the OPC-UA node.";
        }
    }
}
