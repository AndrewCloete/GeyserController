/* --------------------------------------------------------------------------------------------------------
 * DATE:	14 Apr 2015
 * AUTHOR:	Cloete A.H
 * PROJECT:	M-Eng, Inteligent geyser M2M system.	
 * ---------------------------------------------------------------------------------------------------------
 * DESCRIPTION: Abstracts TCP socket communications to message queues. Client side implementation.
 * ---------------------------------------------------------------------------------------------------------
 * PURPOSE: (A good name for this might be "LegacyTCPClient")
 * ---------------------------------------------------------------------------------------------------------
 */

package acza.sun.ee.geyserM2M.controller;

import java.io.*;
import java.net.Socket;
import java.util.LinkedList;


public class TCPClient implements Runnable {

	private int port;
	private Socket firmware_server;
	private BufferedReader bin;
	private PrintWriter pout;
	
	private LinkedList<String> outbound_queue;
	private LinkedList<String> inbound_queue;
	
	public TCPClient(int port, LinkedList<String> outbound_q, LinkedList<String> inbound_q){
		this.port = port;
		this.outbound_queue = outbound_q;
		this.inbound_queue = inbound_q;
	}
	
	
	 /*
	  * TODO: Design connection life cycle
	  	* i.e  what happens if server goes down
	  	* or connection breaks etc.
	  	* consider adding connect(), reconnect() and close() methods.
	  */

	public boolean connect(){
		//Connect to server.
		try{
			this.firmware_server = new Socket("localhost", this.port);
			this.pout = new PrintWriter(this.firmware_server.getOutputStream(), true);
			this.bin = new BufferedReader(new InputStreamReader(this.firmware_server.getInputStream()));
			return true;

		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Error trying to connect client TCP socket to server.");
			return false;
		}
	}
	
	
	//Thread: Connects message queues with communication pipe
	@Override
	public void run() {
		/* Connect to server:
		 * Conversation loop: 
			 * Wait for new command in commandQueue
			 * Pop command off commandQueue and send to geyser
			 * Wait for geyser reply.
			 * Push reply onto replyQueue
		 */
		
		//Conversation loop
		while(true){

			//Wait for command in commandQueue;
			while(this.outbound_queue.isEmpty()){ 
				try {
					Thread.sleep(1);	//Is there a more graceful way to listen?
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
			//Pop command off commandQueue and send to geyser
			this.pout.println(outbound_queue.pop());
			
			String response;
			try {
				response = bin.readLine(); //Wait for geyser reply.
			} catch (IOException e) {
				response = "Firmware server could not be read.";
				System.err.println("Client conversation error.");
			}
			
			//Push reply onto replyQueue
			this.inbound_queue.push(response);

		}
	}

}
