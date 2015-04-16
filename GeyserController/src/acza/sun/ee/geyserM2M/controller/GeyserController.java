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

import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.ParseException;
import org.json.simple.parser.JSONParser;

public class GeyserController {
	
	private static final int CONTROL_PERIOD = 30; //In seconds


	public static void main(String [] args){
		
		
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
		 * Register and subscribe to M2M
		 * Confirm registration.
		 * 
		 * Main control loop:
		 	* Something similar to below
		 * 
		 * ------------------------------- LISTENERS ----------------------------
		 * @Override doGet()
		 	* Since this is the VERBOSE version, doGet() wont be implemented
		 	* A temporary one will be implemented just to debug the server.
		 * 
		 * @Override doPost()
		 	* Schedule configuration settings will be posted here.
		 	* The controller must subscribe to a configuration container which will echo changes here
		 	* 
		 * 
		 * **************************************************************************************** 
		 */
		
		
		System.out.println("Geyser Controller started.");
		
		//TODO: Pseudo code
		
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
			
			
			try {
				Thread.sleep(CONTROL_PERIOD*1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		
		// -----------------------------------------------------------------------------
		
	}
}


/*
 * ---------------------------------------------------------------------------------------------------------
 * NOTES:
 * ---------------------------------------------------------------------------------------------------------
 */