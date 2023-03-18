package edu.greatfree.cry.multicast;

import org.greatfree.message.ServerMessage;
import org.greatfree.server.container.ServerTask;

import edu.greatfree.cry.messege.multicast.PrimitiveMulticastNotification;
import edu.greatfree.cry.messege.multicast.PrimitiveMulticastRequest;
import edu.greatfree.cry.messege.multicast.PrimitiveMulticastResponse;

/**
 * 
 * @author libing
 * 
 * 04/29/2022
 * 
 * Since it is necessary to process messages inherited from SeverMessage in multicasting, the interface of ServerTask needs to be extended. 04/29/2022
 *
 */
public abstract class MulticastTask implements ServerTask
{
	/*
	 * The method is not used often. It is only useful for internal multicasting when implementing new APIs. In the current cases, it is designed for cryptography-based multicasting and clustering. 04/28/2022, Bing Li
	 */
//	public abstract void processNotification(MulticastResponse notification);
//	public abstract void processNotification(Notification notification);
//	public abstract ServerMessage processRequest(Request request);
	
	public abstract void processNotification(PrimitiveMulticastResponse notification);
//	public abstract void processNotification(MulticastNotification notification);
	public abstract void processNotification(PrimitiveMulticastNotification notification);
//	public abstract ServerMessage processRequest(MulticastRequest request);
	public abstract ServerMessage processRequest(PrimitiveMulticastRequest request);

	/*
	 * The method is not used often. It is only useful for internal multicasting when implementing new APIs. In the current cases, it is designed for cryptography-based multicasting and clustering. 04/28/2022, Bing Li
	 */
//	public abstract ServerMessage processRequest(ServerMessage request);
}
