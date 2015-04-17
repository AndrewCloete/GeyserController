/* --------------------------------------------------------------------------------------------------------
 * DATE:	13 Apr 2015
 * AUTHOR:	Cloete A.H
 * PROJECT:	M-Eng, Inteligent geyser M2M system.	
 * ---------------------------------------------------------------------------------------------------------
 * DESCRIPTION: A reference Java implementation of an M2M geyser controller. At the front-end, this component 
 * 				interfaces with an M2M RESTfull SCL using HTTP. At the back-end, the conponent interfaces
 * 				with the GeyserSimulator using TCP.
 * ---------------------------------------------------------------------------------------------------------
 * PURPOSE: Will be used to control a GeyserSimulator application
 * ---------------------------------------------------------------------------------------------------------
 */


/* ----------------------------- IMPORTANT NOTE -------------------------------------
 * Two types of this component will be developed in order to test two
 * different design ideas.
 * 
 * TYPE 1. Will implement both an HTTP server and a client.
 * 		In this case, controller requests originating from some arbitrary device on the M2M network
 * 		can be redirected asynchronously to the controller. This requires that the controller
 * 		have a public IP address, or is connected to a gateway that has.
 * 		
 * TYPE 2. Will only implement an HTTP client.
 * 		In this case, controller requests originating from some arbitrary device on the M2M network
 * 		must be buffered on the network, and the controller will then synchronously request the
 * 		data. 
 * 
 *  Type 2 will be implemented as a separate branch after type 1 is in a working condition.
 *  
 *  
 *  Furthermore. A design choice has to be made on how verbose the controller will be. The options are:
 *  	1. Verbose - The controller synchronously posts geyser data to an M2M container (every minute)
 *  	2. Quiet - The controller registers a data aPoC with M2M so that data requests originating from 
 *  				some arbitrary device on the M2M network, can be redirected asynchronously to the
 *  				controller in a RESTfull manner. (For this design requires that the M2M platform
 *  				have some efficient mechanism for multiple requests since the database NA cannot send
 *  				an individual request to each geyser every minute.)
 */

//TYPE 2 -- VERBOSE

package acza.sun.ee.geyserM2M.controller;

import org.eclipse.om2m.commons.obix.Obj;
import org.eclipse.om2m.commons.obix.Str;
import org.eclipse.om2m.commons.obix.io.ObixEncoder;

import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.ParseException;
import org.json.simple.parser.JSONParser;

public class GeyserController {
	
	private static final int CONTROL_PERIOD = 30; //In seconds


	public static void main(String [] args){
		
		System.out.println("Geyser Controller started.");
		
		/* *************************************** PSEUDO ******************************************
		 * 
		 * Constants declarations
		 	* Port number
		 	* URI strings for M2M interaction
		 * 
		 * Instantiate Jetty server
		 * Set appropriate server properties
		 * Start Jetty server
		 * 
		 * Search OM2M for previous registration
		 * Register and subscribe to M2M
		 * Confirm registration.
		 * 
		 * Main control loop:
		 	* Control geyser using open-loop mode
		 	* Post geyserdata in oBIX format to OM2M NSCL
		 * 
		 * **************************************************************************************** 
		 */
		

		//-------------- Prototype: HTTP M2M registration -------------------
		//This is just to get a feel for how well the controller plays with the OM2M platform
		final String GEYSER_ID = args[0]; //(1)
		final String APP_URI = "localhost:8181/om2m/gscl/applications";
		final String CONTAINER_URI = "localhost:8181/om2m/gscl/applications/" + GEYSER_ID + "/containers";
		final String CONTAINER_ID = "DATA";
		final String CONTENT_URI = "localhost:8181/om2m/gscl/applications/" + GEYSER_ID + "/containers/" + CONTAINER_ID + "/contentInstances";
		
		M2MHTTPClient.post(APP_URI, M2MxmlFactory.registerApplication(GEYSER_ID));	//(3)
		M2MHTTPClient.post(CONTAINER_URI, M2MxmlFactory.addContainer(CONTAINER_ID, (long)5));
		
		//----------------------------------------------------------------------
		
		
		GeyserProxy geyser = new TCPGeyserProxy(3000);
		
		//-------------- Prototype: Simple semi-automatic controller -------------------
		//This is just to get a feel for how well the controller plays with the simulator
		
		double setpointHigh = 46;
		double setpointLow = 45;
		boolean element_state = false;
		
		double internal_temp = 0;
		
		JSONParser parser=new JSONParser();
		System.out.println(geyser.openLoop());
		while(true)
		{
			
			try{
				String geyser_status = geyser.getStatus();
				Object obj = parser.parse(geyser_status);
		        JSONArray array = new JSONArray();
		        array.add(obj);
		        JSONObject jobj = (JSONObject)array.get(0);
		        internal_temp = (double) jobj.get("InternalTemp");
		        System.out.println("Mode: " + jobj.get("ControlMode") + ", Element: " + jobj.get("ElementState") + ", Temperature: " + internal_temp);
			}catch(ParseException pe){
		         System.out.println("position: " + pe.getPosition());
		         System.out.println(pe);
		      }
			
			if(internal_temp <= setpointLow){
				element_state = true;
				System.out.println(geyser.setElement(element_state));
			}
			else if(internal_temp >= setpointHigh){
				element_state = false;
				System.out.println(geyser.setElement(element_state));
			}
			else{
				System.out.println(geyser.setElement(element_state));
			}
			
			//----------------------------------------------------------------------
			
			
			
			//Prototype: Post data synchronously to GSCL in oBIX format
			Obj obj = new Obj();
        	obj.add(new Str("type","Geyser"));
        	obj.add(new Str("location","EC2"));
        	obj.add(new Str("appId","sim_geyser_1")); 
        	obj.add(new Str("ElementState",""+element_state));
        	obj.add(new Str("Internal Temperature",""+internal_temp));
        	
        	/*
        	<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
        	<obj>
        	    <str val="Geyser" name="type"/>
        	    <str val="EC2" name="location"/>
        	    <str val="sim_geyser_1" name="appId"/>
        	    <str val="false" name="ElementState"/>
        	    <str val="48.332233" name="Internal Temperature"/>
        	</obj>
        	*/
        	
        	String obix = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><obj><str val=\"Geyser\" name=\"type\"/><str val=\"EC2\" name=\"location\"/><str val=\"sim_geyser_1\" name=\"appId\"/><str val=\"" + element_state + "\" name=\"ElementState\"/><str val=\"" + internal_temp + "\" name=\"Internal Temperature\"/></obj>";
        	
        	
        	//(2)
			//M2MHTTPClient.post(CONTENT_URI, ObixEncoder.toString(obj)); 
        	M2MHTTPClient.post(CONTENT_URI, obix);
			
			try {
				Thread.sleep(CONTROL_PERIOD*1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		}
		
		// -----------------------------------------------------------------------------
		
	}
}


/*
 * ---------------------------------------------------------------------------------------------------------
 * NOTES:
 *
 * (1) 
 * To set the command line arguments:
 * 		Right click on GeyserController.java --> Properties --> Run/Debug settings --> Edit... --> Arguments
 * 
 * 
 * (2) 
 * For some reason, when you export the project as a runnable jar, then this oBIX method throws an exception.
 * It is not known why yet. So for now, I'm ignoring the "obj" variable and simply hard coded the XML oOBIX string.
 * Since OM2M is planning to deprecate oBIX, this is not too much of a worry. The only reason why I am 
 * formatting the geyser data in to oBIX in the first place, is so that I can look at the contentInctances
 * on the OM2M resource browser. In the future I simply want to use plain XML, and use JAXB to do the serialisation.
 * 
 * 
 * (3) NB!
 * When you export the project as a runnable jar, Eclipse tries to be clever and throws away certain "unnecessary" 
 * files to keep the size down. In particular, it throws away jaxb.index which ought to be in 
 * org.eclipse.om2m.commons.resources (JAXB uses this file to determine which classes can be marshaled to XML).
 * I don't know how to tell eclipse not to do this, but one solution is to simple add the file after the jar has 
 * been created. You can use Archive Manager to do this easily.
 * 
 * It might be worth investigating using the command line instead to get the export right. But a more permanent 
 * solution would be to use Maven!! Since you are staring to use external libraries, it is seriously time to 
 * bite the bullet and get Maven up and running.
 * 
 * 
 * ---------------------------------------------------------------------------------------------------------
 */