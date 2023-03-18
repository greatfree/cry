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
public class HelloBroadcastRequest extends ClusterRequest
{
	private static final long serialVersionUID = -7155821737111083525L;
	
	private String message;

	public HelloBroadcastRequest(String message)
	{
		super(MulticastMessageType.BROADCAST_REQUEST, ClusterAppID.HELLO_BROADCAST_REQUEST);
		this.message = message;
	}

	public String getMessage()
	{
		return this.message;
	}
}
