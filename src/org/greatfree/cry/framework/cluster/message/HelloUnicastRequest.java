package org.greatfree.cry.framework.cluster.message;

import org.greatfree.message.multicast.container.ClusterRequest;

/**
 * 
 * @author libing
 * 
 * 04/27/2022
 *
 */
public class HelloUnicastRequest extends ClusterRequest
{
	private static final long serialVersionUID = 9108221138005781944L;
	
	private String message;

	public HelloUnicastRequest(String message)
	{
		super(ClusterAppID.HELLO_UNICAST_REQUEST);
		this.message = message;
	}

	public String getMessage()
	{
		return this.message;
	}
}
