/* --------------------------------------------------------------------------------------------------------
 * DATE:	14 Apr 2015
 * AUTHOR:	Cloete A.H
 * PROJECT:	M-Eng, Inteligent geyser M2M system.	
 * ---------------------------------------------------------------------------------------------------------
 * DESCRIPTION: Abstract class that defines how to interface with geyser firmware, i.e. provides a 
 * 				software handle for the fimrware API. It does not specify a communication protocol
 * ---------------------------------------------------------------------------------------------------------
 * PURPOSE: 
 * ---------------------------------------------------------------------------------------------------------
 */

//TODO: Change this class to an abstract class and then extend a TCPGeyserProxy.

package acza.sun.ee.geyserM2M.controller;

import java.util.LinkedList;

public abstract class GeyserProxy{
	
	//Queues as the broker between the API and the communication pipe. (Look familiar?)
	protected LinkedList<String> commandQueue = new LinkedList<String>();
	protected LinkedList<String> replyQueue = new LinkedList<String>();
	
	
	public GeyserProxy(){ 
		/*
		 * TODO: It there a way of handing a reference of commandQueue and replyQueue 
		 * to child classes using this constructor and the super method.
		 * Or is protected fine?
		 */
	}
	
	//---------------  COMMAND METHODS -----------------
	//API commands used to control geyser
	
	public String openLoop(){
		return this.promptServer("open");
	}
	
	public String setElement(boolean state){
		if(state == true)
			return this.promptServer("elementon");
		else
			return this.promptServer("elementoff");
	}
	
	public String getStatus(){
		return this.promptServer("get");
	}
	
	
	// ---------------  PROTOCOL METHOD -----------------
	private String promptServer(String msg){
		commandQueue.push(msg);
		//Wait for communication thread to reply
		
		while(this.replyQueue.isEmpty()){ 
			try {
				Thread.sleep(1);	//Is there a more graceful way to listen?
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		return replyQueue.pop();
	}
	
	
}

