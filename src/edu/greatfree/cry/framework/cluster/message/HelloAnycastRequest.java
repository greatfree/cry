package edu.greatfree.cry.framework.cluster.message;

import org.greatfree.message.multicast.MulticastMessageType;

import edu.greatfree.cry.messege.multicast.ClusterRequest;

/**
 * 
 * @author libing
 * 
 * 04/27/2022
 *
 */
public class HelloAnycastRequest extends ClusterRequest
{
	private static final long serialVersionUID = 4953270166926466522L;
	
	private String message;

	public HelloAnycastRequest(String message)
	{
		super(MulticastMessageType.ANYCAST_REQUEST, ClusterAppID.HELLO_ANYCAST_REQUEST);
		this.message = message;
	}

	public String getMessage()
	{
		return this.message;
	}
}
