package eu.arrowhead.client.skeleton.consumer;

import eu.arrowhead.common.dto.shared.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpMethod;

import org.jose4j.json.internal.json_simple.JSONArray;
import org.jose4j.json.internal.json_simple.JSONObject;
import org.jose4j.json.internal.json_simple.parser.JSONParser;
import org.jose4j.json.internal.json_simple.parser.ParseException;

import eu.arrowhead.client.library.ArrowheadService;
import eu.arrowhead.common.CommonConstants;
import eu.arrowhead.common.dto.shared.OrchestrationFlags.Flag;
import eu.arrowhead.common.dto.shared.OrchestrationFormRequestDTO.Builder;
import eu.arrowhead.common.exception.ArrowheadException;
import eu.arrowhead.common.Utilities;
import eu.arrowhead.common.SSLProperties;
import eu.arrowhead.client.skeleton.consumer.Consumer_Contsant;
//import eu.arrowhead.client.skeleton.provider.JSONReader;

import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.*;

@SpringBootApplication
@ComponentScan(basePackages = {CommonConstants.BASE_PACKAGE}) //TODO: add custom packages if any
public class ConsumerMain implements ApplicationRunner {

	//=================================================================================================
	// members

	@Autowired
	private ArrowheadService arrowheadService;

	@Autowired
	protected SSLProperties sslProperties;
	//=================================================================================================
	// methods

	//------------------------------------------------------------------------------------------------
	public static void main(final String[] args) {
		SpringApplication.run(ConsumerMain.class, args);
	}

	//-------------------------------------------------------------------------------------------------
	@Override
	public void run(final ApplicationArguments args) throws Exception {

		/*String [] Device=new String[29];
		String [] DeviceType=new String[29];
		String [] Location=new String[29];
		String [] Instance=new String[29];
		String [] nodeIdentifier= new String[29];

		//-------------------
		//JSONReader read= new JSONReader();
		//System.out.println(read.GetServiceDefinition("Sensor","all", "all", "all"));

		//-------------------

		String instanceinput="";
		Scanner in= new Scanner(System.in);
		System.out.println("Enter the device Name: Sensor/Actuator/All ");
		String deviceinput= in.next();
		if(deviceinput.equalsIgnoreCase("Sensor"))
			System.out.println("Enter the device Type Name: Phototransistor/Pushbutton/All ");
		else
			System.out.println("Enter the device Type Name: Motor/ConveyorBelt/Slider/All ");

		String devicetypeinput= in.next();
		if(devicetypeinput.equalsIgnoreCase("pushbutton") || devicetypeinput.equalsIgnoreCase("slider"))
			System.out.println("Enter the Location : Loading/OffLoading/All ");
		else if(devicetypeinput.equalsIgnoreCase("Motor"))
			System.out.println("Enter the Location : Milling/Drilling/All ");
		else
			System.out.println("Enter the Location : Loading/Milling/Drilling/OffLoading/All ");
		String locationinput= in.next();
		if((deviceinput.equalsIgnoreCase("Sensor")|| deviceinput.equalsIgnoreCase("all"))&& (locationinput.equalsIgnoreCase("Loading")|| locationinput.equalsIgnoreCase("OffLoading")|| locationinput.equalsIgnoreCase("all"))){
			System.out.println("Enter the instance number: One/Two/All ");
			instanceinput= in.next();
		}
		else
			instanceinput= "One";

		int i = 0;

		//Finding list of devices based on the input value
		JSONParser parser = new JSONParser();
		Object obj = parser.parse(new FileReader("client-skeleton-provider/src/main/resources/SR_Entry.json"));
		JSONObject jsonObject =  (JSONObject) obj;
		JSONArray Services = (JSONArray) jsonObject.get("Services");
		Iterator<JSONObject> iterator = Services.iterator();

		while (iterator.hasNext()) {
			JSONObject iter=iterator.next();
			String serviceDef= iter.get("ServiceDef").toString();
			JSONObject mdata=(JSONObject) iter.get("MetaData");
			String device= mdata.get("Device").toString();
			String deviceType= mdata.get("DeviceType").toString();
			String location= mdata.get("Location").toString();
			String instance= mdata.get("Instance").toString();

			if((device.equalsIgnoreCase(deviceinput)||deviceinput.equalsIgnoreCase("All"))&& (deviceType.equalsIgnoreCase(devicetypeinput)|| devicetypeinput.equalsIgnoreCase("All"))
					&& (location.equalsIgnoreCase(locationinput)|| locationinput.equalsIgnoreCase("All"))&& (instance.equalsIgnoreCase(instanceinput) || instanceinput.equalsIgnoreCase("All"))){
				Device[i]= device; DeviceType[i]= deviceType; Location[i]= location; Instance[i]= instance;
				nodeIdentifier[i]= "\"Machine Status\".\""+serviceDef+"\"";
				i++;
			}
		}
		//Finding the count of devices to be fetched
		int count=0;
		for(int k=0;k<nodeIdentifier.length; k++){

			if(nodeIdentifier[k]!= null)
				count++;
		}
		//Meta Data declaration
		Map<String, String> meta= new HashMap<String, String>();

		// --------------------- Read OPC-UA Variable using metadata---------------------------
		for(i=0;i<count; i++){
			meta.put("DeviceType",DeviceType[i]);
			meta.put("http-method","GET");
			meta.put("Instance",Instance[i]);
			meta.put("Device",Device[i]);
			meta.put("Location",Location[i]);

			//System.out.println(meta.get("DeviceType")+"/"+meta.get("Instance")+"/"+meta.get("Device")+"/"+meta.get("Location")+"/"+nodeIdentifier[i]);

			//SIMPLE EXAMPLE OF INITIATING AN ORCHESTRATION
			OrchestrationResultDTO result = orchestrate(meta);
			final HttpMethod httpMethod = HttpMethod.GET;//Http method should be specified in the description of the service.
			final String address = result.getProvider().getAddress();
			final int port = result.getProvider().getPort();
			final String serviceUri = result.getServiceUri();
			final String interfaceName = result.getInterfaces().get(0).getInterfaceName(); //Simplest way of choosing an interface.
			String token = null;
			if (result.getAuthorizationTokens() != null) {
				token = result.getAuthorizationTokens().get(interfaceName); //Can be null when the security type of the provider is 'CERTIFICATE' or nothing.
			}
			final Object payload = null; //Can be null if not specified in the description of the service.

			//System.out.println("GET " + address + "/" + serviceUri);
			final String consumedReadService = arrowheadService.consumeServiceHTTP(String.class, httpMethod, address, port, serviceUri, interfaceName, token, payload,  "DeviceType", meta.get("DeviceType"), "Instance", meta.get("Instance"), "Device", meta.get("Device"), "Location", meta.get("Location"),"nodeIdentifier", nodeIdentifier[i]);
			System.out.println("Service response: Status of "+nodeIdentifier[i].replace("\"Machine Status\".","") +" is  "+ consumedReadService);

		}

		// --------------------- Write OPC-UA Variable ---------------------------
		Scanner input1= new Scanner(System.in);
		System.out.println("Do you want to start the Factory:Yes/No ");
		String write= input1.next();

		if(write.equalsIgnoreCase("Yes")){
			String ServiceDefinition= "write_test start opc";
			String nodeId= "\"Machine Status\".\""+ServiceDefinition+"\"";
			OrchestrationResultDTO result = orchestrate(ServiceDefinition);
			Map<String, String> meta2 = result.getMetadata();
			// FIXME Why are the variables above (and therefore here) declared final? Thread safety? Performance? I've kept them final here (i.e. created new varibles instead of re-using the above), but I'd rather re-use the above variables if possible?
			final HttpMethod httpMethod2 = HttpMethod.POST;
			final String address = result.getProvider().getAddress();
			final int port = result.getProvider().getPort();
			final String serviceUri = result.getServiceUri();
			final String interfaceName = result.getInterfaces().get(0).getInterfaceName(); //Simplest way of choosing an interface.

			String token = null;
			if (result.getAuthorizationTokens() != null) {
				token = result.getAuthorizationTokens().get(interfaceName); //Can be null when the security type of the provider is 'CERTIFICATE' or nothing.
			}
			final Object payload2 = null; //Can be null if not specified in the description of the service.

			System.out.println("POST " + address + "/" + serviceUri);
			String valueAsString = "true";
			String valueAsString2 = "false";
			final String consumedWriteService = arrowheadService.consumeServiceHTTP(String.class, httpMethod2, address, port, serviceUri, interfaceName, token, payload2,  "opcuaNodeId", meta2.get("nodeId"), "value", valueAsString);
			System.out.println("Starting Factory: " + consumedWriteService);
			Thread.sleep(5000);
			final String consumedWriteService2 = arrowheadService.consumeServiceHTTP(String.class, httpMethod2, address, port, serviceUri, interfaceName, token, payload2,  "opcuaNodeId", meta2.get("nodeId"), "value", valueAsString2);
			System.out.println("Resetting Variable Input: " + consumedWriteService2);
		}
		else{
			System.out.println("Exiting code!!!");
		}*/

		readservice();
		writeservice();
	}

	public void readservice(){
		String serviceDefinition="";
		String ids="";
		Scanner in1= new Scanner(System.in);
		System.out.println("Enter the service you want to access to read value: sensorvalue or actuatorvalue");
		serviceDefinition= in1.next();
		if(serviceDefinition.equalsIgnoreCase("sensorvalue")){
			ids="I1-I9";
		}
		else ids="Q1-Q10";
		String componentId="";
		Scanner in2= new Scanner(System.in);
		System.out.println("Enter the component ID: "+ids+" /All");
		componentId= in2.next();
		if(componentId.equalsIgnoreCase("All"))
			componentId="";

		OrchestrationResultDTO result = orchestrate(serviceDefinition);
		Map<String, String> meta1 = result.getMetadata();
		final HttpMethod httpMethod = HttpMethod.GET;//Http method should be specified in the description of the service.
		final String address = result.getProvider().getAddress();
		final int port = result.getProvider().getPort();
		final String serviceUri = result.getServiceUri();
		final String interfaceName = result.getInterfaces().get(0).getInterfaceName(); //Simplest way of choosing an interface.
		String token = null;

		if (result.getAuthorizationTokens() != null) {
			token = result.getAuthorizationTokens().get(interfaceName); //Can be null when the security type of the provider is 'CERTIFICATE' or nothing.
		}
		final Object payload = null; //Can be null if not specified in the description of the service.
		System.out.println("GET " + address + serviceUri+"/"+componentId);
		final String consumedReadService = arrowheadService.consumeServiceHTTP(String.class, httpMethod, address, port, serviceUri+"/"+componentId, interfaceName, token, payload);
		System.out.println("Service response: " + consumedReadService);
	}
	public void writeservice(){
		String actuatorId="";
		Scanner in3= new Scanner(System.in);
		System.out.println("Enter the Actuator ID to write new value: Q1-Q10");
		actuatorId= in3.next();

		String actuatorvalue="";
		Scanner in4= new Scanner(System.in);
		System.out.println("Enter the Actuator value to be passed");
		actuatorvalue= in4.next();

		OrchestrationResultDTO result = orchestrate("actuatorvalue");
		Map<String, String> meta= result.getMetadata();
		final HttpMethod httpMethod = HttpMethod.PUT;//Http method should be specified in the description of the service.
		final String address = result.getProvider().getAddress();
		final int port = result.getProvider().getPort();
		final String serviceUri = result.getServiceUri();
		final String interfaceName = result.getInterfaces().get(0).getInterfaceName(); //Simplest way of choosing an interface.
		String token = null;

		if (result.getAuthorizationTokens() != null) {
			token = result.getAuthorizationTokens().get(interfaceName); //Can be null when the security type of the provider is 'CERTIFICATE' or nothing.
		}
		final Object payload = null; //Can be null if not specified in the description of the service.
		System.out.println("PUT " + address +":"+port+ serviceUri+"/"+actuatorId+"/"+actuatorvalue);
		final String consumedReadService = arrowheadService.consumeServiceHTTP(String.class, httpMethod, address, port, serviceUri+"/"+actuatorId+"/"+actuatorvalue, interfaceName, token, payload);
		System.out.println("Service response: " + consumedReadService);
	}

	/*--------------------Orchestration using just meta data-------------------------*/
	public OrchestrationResultDTO orchestrate(Map metadata) {
		final Builder orchestrationFormBuilder = arrowheadService.getOrchestrationFormBuilder();
		String sql="select service_definition from arrowhead.service_definition where id=(select service_id FROM arrowhead.service_registry where metadata="+"\""+
				"DeviceType="+metadata.get("DeviceType")+
				", http-method="+metadata.get("http-method")+
				", Instance="+metadata.get("Instance")+
				", Device="+metadata.get("Device")+
				", Location="+metadata.get("Location")+"\");";

		//System.out.println(sql);

		//get service definition from metadata
		String serviceDefinition="";
		try {
			DriverManager.registerDriver(new com.mysql.cj.jdbc.Driver());
			String url = Consumer_Contsant.jdbcUrl;
			Connection conn = DriverManager.getConnection(url,Consumer_Contsant.UserName,Consumer_Contsant.Passwd);
			Statement stmt = conn.createStatement();
			ResultSet rs;
			rs=stmt.executeQuery(sql);
			while ( rs.next() ) {
				serviceDefinition = rs.getString(1);
				//System.out.println(serviceDefinition);
			}
			conn.close();
		} catch (Exception e) {
			System.err.println("Got an exception! ");
			System.err.println(e.getMessage());
		}


		final ServiceQueryFormDTO requestedService = new ServiceQueryFormDTO();
		//requestedService.setMetadataRequirements(metadata);
		requestedService.setServiceDefinitionRequirement(serviceDefinition);

		orchestrationFormBuilder.requestedService(requestedService)
				.flag(Flag.MATCHMAKING, true) //When this flag is false or not specified, then the orchestration response cloud contain more proper provider. Otherwise only one will be chosen if there is any proper.
				.flag(Flag.OVERRIDE_STORE, true) //When this flag is false or not specified, then a Store Orchestration will be proceeded. Otherwise a Dynamic Orchestration will be proceeded.
				.flag(Flag.TRIGGER_INTER_CLOUD, false); //When this flag is false or not specified, then orchestration will not look for providers in the neighbor clouds, when there is no proper provider in the local cloud. Otherwise it will.
                //.flag(Flag.METADATA_SEARCH, true);
		//System.out.println(orchestrationFormBuilder.requestedService(requestedService));

		final OrchestrationFormRequestDTO orchestrationRequest = orchestrationFormBuilder.build();

		OrchestrationResponseDTO response = null;
		try {
			response = arrowheadService.proceedOrchestration(orchestrationRequest);
		} catch(final ArrowheadException ex) {
			//Handle the unsuccessful request as you wish!
		}

		if(response ==null||response.getResponse().isEmpty()) {
			//If no proper providers found during the orchestration process, then the response list will be empty. Handle the case as you wish!
			System.out.println("FATAL ERROR: Orchestration response came back empty. Make sure the Service you try to consume is in the Service Registry and that the Consumer has the privileges to consume this Service (e.g. check intra_cloud_authorization and intra_cloud_interface_connection).");
			System.exit(1);
		}

		final OrchestrationResultDTO result = response.getResponse().get(0); //Simplest way of choosing a provider.
		return result;
	}

	/*--------------------Orchestration using ServiceDefinition-------------------------*/
	public OrchestrationResultDTO orchestrate(String serviceDefinition) {
		final Builder orchestrationFormBuilder = arrowheadService.getOrchestrationFormBuilder();

		final ServiceQueryFormDTO requestedService = new ServiceQueryFormDTO();
		requestedService.setServiceDefinitionRequirement(serviceDefinition);

		orchestrationFormBuilder.requestedService(requestedService)
				.flag(Flag.MATCHMAKING, false) //When this flag is false or not specified, then the orchestration response cloud contain more proper provider. Otherwise only one will be chosen if there is any proper.
				.flag(Flag.OVERRIDE_STORE, true) //When this flag is false or not specified, then a Store Orchestration will be proceeded. Otherwise a Dynamic Orchestration will be proceeded.
				.flag(Flag.TRIGGER_INTER_CLOUD, false); //When this flag is false or not specified, then orchestration will not look for providers in the neighbor clouds, when there is no proper provider in the local cloud. Otherwise it will.

		final OrchestrationFormRequestDTO orchestrationRequest = orchestrationFormBuilder.build();

		OrchestrationResponseDTO response = null;
		try {
			response = arrowheadService.proceedOrchestration(orchestrationRequest);
		} catch(final ArrowheadException ex) {
			//Handle the unsuccessful request as you wish!
		}

		if(response ==null||response.getResponse().isEmpty()) {
			//If no proper providers found during the orchestration process, then the response list will be empty. Handle the case as you wish!
			System.out.println("FATAL ERROR: Orchestration response came back empty. Make sure the Service you try to consume is in the Service Registry and that the Consumer has the privileges to consume this Service (e.g. check intra_cloud_authorization and intra_cloud_interface_connection).");
			System.exit(1);
		}

		final OrchestrationResultDTO result = response.getResponse().get(0); //Simplest way of choosing a provider.
		return result;
	}
	private void printOut(final Object object) {
		System.out.println(Utilities.toPrettyJson(Utilities.toJson(object)));
	}
	private String getInterface() {
		return sslProperties.isSslEnabled() ? "HTTPS-SECURE-JSON" : "HTTP-INSECURE-JSON";
	}
}
