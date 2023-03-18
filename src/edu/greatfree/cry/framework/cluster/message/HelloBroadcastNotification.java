package edu.greatfree.cry.framework.cluster.message;

import org.greatfree.message.multicast.MulticastMessageType;

import edu.greatfree.cry.messege.multicast.ClusterNotification;

/**
 * 
 * @author libing
 * 
 * 04/27/2022
 *
 */
public class HelloBroadcastNotification extends ClusterNotification
{
	private static final long serialVersionUID = 3371921298271066369L;
	
	private String message;

	public HelloBroadcastNotification(String message)
	{
		super(MulticastMessageType.BROADCAST_NOTIFICATION, ClusterAppID.HELLO_BROADCAST_NOTIFICATION);
		this.message = message;
	}

	public String getMessage()
	{
		return this.message;
	}
}
