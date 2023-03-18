package edu.greatfree.cry.messege.multicast;

import org.greatfree.message.multicast.MulticastMessageType;
import org.greatfree.message.multicast.MulticastResponse;

/**
 * 
 *  * The motivation to design the message is to add the application ID to the multicasting messages as well as keep the constant, MULTICAST_RESPONSE, in the response. The one, MulticastResponse, does not allow to do that. 05/11/2022, Bing Li

 * @author libing
 * 
 * 05/11/2022, Bing Li
 *
 */
public abstract class PrimitiveMulticastResponse extends MulticastResponse
{
	private static final long serialVersionUID = 7705262725126189516L;
	
	private int applicationID;

	public PrimitiveMulticastResponse(int applicationID, String collaboratorKey)
	{
		super(MulticastMessageType.MULTICAST_RESPONSE, collaboratorKey);
		this.applicationID = applicationID;
	}

	public int getApplicationID()
	{
		return this.applicationID;
	}
}
