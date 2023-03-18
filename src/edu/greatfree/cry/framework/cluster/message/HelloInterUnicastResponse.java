package edu.greatfree.cry.framework.cluster.message;

import edu.greatfree.cry.messege.multicast.PrimitiveMulticastResponse;

/**
 * 
 * @author libing
 * 
 * 06/20/2022
 *
 */
public class HelloInterUnicastResponse extends PrimitiveMulticastResponse
{
	private static final long serialVersionUID = 5028935481432467446L;
	
	private String message;

	public HelloInterUnicastResponse(String message, String collaboratorKey)
	{
		super(ClusterAppID.HELLO_INTER_UNICAST_RESPONSE, collaboratorKey);
		this.message = message;
	}

	public String getMessage()
	{
		return this.message;
	}
}
