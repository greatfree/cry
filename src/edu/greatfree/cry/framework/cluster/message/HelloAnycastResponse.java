package edu.greatfree.cry.framework.cluster.message;

import edu.greatfree.cry.messege.multicast.PrimitiveMulticastResponse;

/**
 * 
 * @author libing
 * 
 * 04/27/2022
 *
 */
// public class HelloAnycastResponse extends MulticastResponse
public class HelloAnycastResponse extends PrimitiveMulticastResponse
{
	private static final long serialVersionUID = -7385159571349250036L;
	
	private String message;

	public HelloAnycastResponse(String message, String collaboratorKey)
	{
		super(ClusterAppID.HELLO_ANYCAST_RESPONSE, collaboratorKey);
		this.message = message;
	}

	public String getMessage()
	{
		return this.message;
	}
}
