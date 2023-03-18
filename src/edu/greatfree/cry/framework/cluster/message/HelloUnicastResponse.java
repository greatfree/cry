package edu.greatfree.cry.framework.cluster.message;

import edu.greatfree.cry.messege.multicast.PrimitiveMulticastResponse;

/**
 * 
 * @author libing
 * 
 * 04/27/2022
 *
 */
// public class HelloUnicastResponse extends MulticastResponse
public class HelloUnicastResponse extends PrimitiveMulticastResponse
{
	private static final long serialVersionUID = 3621664498702775273L;

	private String message;

	public HelloUnicastResponse(String message, String collaboratorKey)
	{
		super(ClusterAppID.HELLO_UNICAST_RESPONSE, collaboratorKey);
		this.message = message;
	}

	public String getMessage()
	{
		return this.message;
	}
}
