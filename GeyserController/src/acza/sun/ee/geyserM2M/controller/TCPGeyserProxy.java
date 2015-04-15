/* --------------------------------------------------------------------------------------------------------
 * DATE:	14 Apr 2015
 * AUTHOR:	Cloete A.H
 * PROJECT:	M-Eng, Inteligent geyser M2M system.	
 * ---------------------------------------------------------------------------------------------------------
 * DESCRIPTION: Extends GeyserProxy and provides a TCP specific means of communication
 * ---------------------------------------------------------------------------------------------------------
 * PURPOSE: 
 * ---------------------------------------------------------------------------------------------------------
 */

package acza.sun.ee.geyserM2M.controller;

public class TCPGeyserProxy extends GeyserProxy{
	
	public TCPGeyserProxy(int port){
		TCPClient client = new TCPClient(port, this.commandQueue, this.replyQueue);
		boolean is_connected = client.connect();
		Thread tcp_client_thread = new Thread(client);
		//TODO: Design a strategy because if is_connected == false, this will give NullPointerException. 
		tcp_client_thread.start();
	}
}
