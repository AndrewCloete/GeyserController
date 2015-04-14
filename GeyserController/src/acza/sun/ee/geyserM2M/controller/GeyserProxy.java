/* --------------------------------------------------------------------------------------------------------
 * DATE:	14 Apr 2015
 * AUTHOR:	Cloete A.H
 * PROJECT:	M-Eng, Inteligent geyser M2M system.	
 * ---------------------------------------------------------------------------------------------------------
 * DESCRIPTION: This object is used to communicate with geyser Firmware. In this specific case, this proxy
 * uses a TCP client as the underlying means of communication. However a user of this object is unaware of this.
 * ---------------------------------------------------------------------------------------------------------
 * PURPOSE: 
 * ---------------------------------------------------------------------------------------------------------
 */

//TODO: Change this class to an abstract class and then extend a TCPGeyserProxy.

package acza.sun.ee.geyserM2M.controller;

import java.util.LinkedList;

public class GeyserProxy{
	
	//Queues as the broker between the API and the communication pipe. (Look familiar?)
	private LinkedList<String> commandQueue = new LinkedList<String>();
	private LinkedList<String> replyQueue = new LinkedList<String>();
	
	
	public GeyserProxy(){
		TCPClient client = new TCPClient(3000, commandQueue, replyQueue);
		boolean is_connected = client.connect();
		Thread tcp_client_thread = new Thread(client);
		//TODO: Design a strategy because if is_connected == false, this will give NullPointerException. 
		tcp_client_thread.start(); 
	}
	
	
	
	//---------------  COMMAND METHODS -----------------
	//API commands used to control geyser
	
	public String openLoop(){
		return this.promptServer("open");
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

