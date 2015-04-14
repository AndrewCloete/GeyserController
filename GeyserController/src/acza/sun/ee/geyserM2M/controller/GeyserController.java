/* --------------------------------------------------------------------------------------------------------
 * DATE:	13 Apr 2015
 * AUTHOR:	Cloete A.H
 * PROJECT:	M-Eng, Inteligent geyser M2M system.	
 * ---------------------------------------------------------------------------------------------------------
 * DESCRIPTION: A reference Java implementation of an M2M geyser controller
 * ---------------------------------------------------------------------------------------------------------
 * PURPOSE: Will be used to control a GeyserSimulator application
 * ---------------------------------------------------------------------------------------------------------
 */

package acza.sun.ee.geyserM2M.controller;

import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.ParseException;
import org.json.simple.parser.JSONParser;

public class GeyserController {

	public static void main(String [] args){
		System.out.println("Geyser Controller started.");
		
		//TODO: Pseudo code
		
		GeyserProxy geyser = new TCPGeyserProxy(3000);
		
		
		
		//Testing: Simple semi-automatic controller
		double setpointHigh = 46;
		double setpointLow = 45;
		
		double internal_temp = 0;
		
		JSONParser parser=new JSONParser();
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
				System.out.println(geyser.openLoop());
				System.out.println(geyser.elementOn());
			}
			else if(internal_temp >= setpointHigh){
				System.out.println(geyser.openLoop());
				System.out.println(geyser.elementOff());
			}
			
		
		}
		
	}
}


/*
 * ---------------------------------------------------------------------------------------------------------
 * NOTES:
 * ---------------------------------------------------------------------------------------------------------
 */