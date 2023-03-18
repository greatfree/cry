package edu.greatfree.cry.messege.multicast;

import org.greatfree.message.multicast.MulticastMessageType;
import org.greatfree.message.multicast.MulticastNotification;

/**
 * 
 * 
 * The motivation to design the message is to add the application ID to the multicasting messages as well as keep the constant, MULTICAST_NOTIFICATION, in the notification. The one, MulticastNotification, does not allow to do that. 05/11/2022, Bing Li
 *  
 * 
 * @author libing
 * 
 * 05/11/2022
 *
 */
public abstract class PrimitiveMulticastNotification extends MulticastNotification
{
	private static final long serialVersionUID = -3462002947906760130L;
	
	private int multiAppID;

	public PrimitiveMulticastNotification(int multiAppID)
	{
		super(MulticastMessageType.MULTICAST_NOTIFICATION);
		this.multiAppID = multiAppID;
	}

	public int getMultiAppID()
	{
		return this.multiAppID;
	}
}
