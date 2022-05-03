package org.greatfree.cry.multicast;

import org.greatfree.message.multicast.MulticastNotification;
import org.greatfree.message.multicast.MulticastRequest;
import org.greatfree.message.multicast.MulticastResponse;
import org.greatfree.server.container.ServerTask;

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
	public abstract void processNotification(MulticastResponse notification);
	public abstract void processNotification(MulticastNotification notification);
	public abstract void processRequest(MulticastRequest request);

	/*
	 * The method is not used often. It is only useful for internal multicasting when implementing new APIs. In the current cases, it is designed for cryptography-based multicasting and clustering. 04/28/2022, Bing Li
	 */
//	public abstract ServerMessage processRequest(ServerMessage request);
}
