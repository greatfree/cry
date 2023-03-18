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
public class HelloAnycastNotification extends ClusterNotification
{
	private static final long serialVersionUID = 2388394242870897722L;
	
	private String message;

	public HelloAnycastNotification(String message)
	{
		super(MulticastMessageType.ANYCAST_NOTIFICATION, ClusterAppID.HELLO_ANYCAST_NOTIFICATION);
		this.message = message;
	}

	public String getMessage()
	{
		return this.message;
	}
}
