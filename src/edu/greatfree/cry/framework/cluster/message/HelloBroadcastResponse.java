package edu.greatfree.cry.framework.cluster.message;

import edu.greatfree.cry.messege.multicast.PrimitiveMulticastResponse;

/**
 * 
 * @author libing
 * 
 * 04/27/2022
 *
 */
// public class HelloBroadcastResponse extends MulticastResponse
public class HelloBroadcastResponse extends PrimitiveMulticastResponse
{
	private static final long serialVersionUID = -2211812953787835534L;
	
	private String message;

	public HelloBroadcastResponse(String message, String collaboratorKey)
	{
		super(ClusterAppID.HELLO_BROADCAST_RESPONSE, collaboratorKey);
		this.message = message;
	}

	public String getMessage()
	{
		return this.message;
	}
}
