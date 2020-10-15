package eu.arrowhead.client.skeleton.provider;

public class Provider_Constants {


    public static final String jdbcUrl = "jdbc:mysql://localhost:3306/?user=root";
    public static final String UserName="root";
    public static final String Passwd="appyjita7*";
    public static final String base_uri="/factory";
    
    public static final String ADDRESS_PLC_OPC_UA_SERVER = "opc.ua.server.address";
    public static final String $ADDRESS_PLC_OPC_UA_SERVER_WD = "${" + ADDRESS_PLC_OPC_UA_SERVER + ": 192.168.1.101:4840" + "}";
    
    public static final String PATH_TO_JSON_SR_ENTRY = "path.sr.entries";
    public static final String $PATH_TO_JSON_SR_ENTRY_WD ="${" + PATH_TO_JSON_SR_ENTRY + ": arrowhead-opc-ua-provider/src/main/resources/SR_Entry.json" + "}";
}
