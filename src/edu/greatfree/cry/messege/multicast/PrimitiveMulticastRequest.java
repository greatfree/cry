package edu.greatfree.cry.messege.multicast;

import org.greatfree.message.multicast.MulticastMessageType;
import org.greatfree.message.multicast.MulticastRequest;

/**
 * 
 * The motivation to design the message is to add the application ID to the multicasting messages as well as keep the constant, MULTICASE_REQUEST, in the request. The one, MulticastRequest, does not allow to do that. 05/11/2022, Bing Li
 * 
 * @author libing
 * 
 * 05/11/2022
 *
 */
public abstract class PrimitiveMulticastRequest extends MulticastRequest
{
	private static final long serialVersionUID = -3449478884042507322L;
	
	private int multiAppID;

	public PrimitiveMulticastRequest(int multiAppID)
	{
		super(MulticastMessageType.MULTICAST_REQUEST);
		this.multiAppID = multiAppID;
	}

	public int getMultiAppID()
	{
		return this.multiAppID;
	}
}
