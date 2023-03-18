package edu.greatfree.cry.framework.cluster.message;

import java.util.List;

import edu.greatfree.cry.messege.multicast.PrimitiveMulticastResponse;

/**
 * 
 * @author libing
 * 
 * 06/21/2022
 *
 */
public class HelloInterAnycastResponse extends PrimitiveMulticastResponse
{
	private static final long serialVersionUID = -5729957357878045972L;

	private List<String> messages;

	public HelloInterAnycastResponse(List<String> messages, String collaboratorKey)
	{
		super(ClusterAppID.HELLO_INTER_ANYCAST_RESPONSE, collaboratorKey);
		this.messages = messages;
	}

	public List<String> getMessages()
	{
		return this.messages;
	}
}
