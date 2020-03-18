package eu.arrowhead.client.skeleton.provider.controller;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.StatusCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import eu.arrowhead.client.skeleton.provider.OPC_UA.*;

import java.util.concurrent.CompletableFuture;

@RestController
public class ProviderController {
	//=================================================================================================
	// members
	@Value("${opc.ua.connection_address}")
	private String opcuaServerAddress;

	@Value("${opc.ua.root_node_namespace}")
	private int rootNodeNamespaceIndex;

	@Value("${opc.ua.root_node_identifier}")
	private String rootNodeIdentifier;

	//TODO: add your variables here
	//opcuaServerAddress = opcuaServerAddress.replaceAll("opc.tcp://", "");

	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------
	@RequestMapping(path = "/echo")
	@ResponseBody
	public String echoService() {
		return "Got it!";
	}
	
	//-------------------------------------------------------------------------------------------------
	//TODO: implement here your provider related REST end points
	// FIXME Double-check that the token security prevents tampering with variables in the OPC-UA it is not supposed to access (I.e. only allows access to the variables in the Service Registry)
	//-------------------------------------------------------------------------------------------------
	@RequestMapping(path = "/read/variable")
	@ResponseBody
	public String readVariableNode(@RequestParam(name = "DeviceType") final String DeviceType, @RequestParam(name = "Instance") final String Instance, @RequestParam(name = "Device") final String Device, @RequestParam(name = "Location") final String Location, @RequestParam(name = "nodeIdentifier") final String nodeIdentifier) {
		System.out.println("Got a read variable request:" + Device + "/" + DeviceType + "/" + Location+ "/" + Instance);
		NodeId nodeId = new NodeId(rootNodeNamespaceIndex, nodeIdentifier);
		opcuaServerAddress = opcuaServerAddress.replaceAll("opc.tcp://", "");
		OPCUAConnection connection = new OPCUAConnection(opcuaServerAddress);
		String body = "";
		try {
			body = OPCUAInteractions.readNode(connection.getConnectedClient(), nodeId);
			connection.dispose();
			return body;
		} catch (Exception ex) {
			connection.dispose();
			return "There was an error reading the OPC-UA node.";
		}
	}

	@RequestMapping(path = "/write/variable")
	@ResponseBody
	public String writeVariableNode(@RequestParam(name = "opcuaServerAddress") final String opcuaServerAddress, @RequestParam(name = "opcuaNamespace") final int namespaceIndex, @RequestParam(name = "opcuaNodeId") final String identifier, @RequestParam(name = "value") final String value) {
		System.out.println("Got a write variable request:" + opcuaServerAddress + "/" + namespaceIndex + "/" + identifier + " value: " + value);
		NodeId nodeId = new NodeId(namespaceIndex, identifier);
		boolean bolValue= false;
		if(value== "true")
			bolValue=true;
		else bolValue=false;
		String opcuaServerAddress1 = opcuaServerAddress.replaceAll("opc.tcp://", "");
		OPCUAConnection connection = new OPCUAConnection(opcuaServerAddress1);
		String body = "Wrote value: " + value;

		try {
			String status = OPCUAInteractions.writeNode(connection.getConnectedClient(), nodeId, value);
			//StatusCode status2 = OPCUAInteractions.writeNode2(connection.getConnectedClient(), nodeId, value).get();
			System.out.println("Status Code: " + status);
			connection.dispose();
			return body;
		} catch (Exception ex) {
			connection.dispose();
			return "There was an error writing to the OPC-UA node.";
		}
	}
	@RequestMapping("*")
	@ResponseBody
	public String fallbackMethod(){
		return "fallback method";
	}
}
