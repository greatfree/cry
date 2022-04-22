package org.greatfree.cry.framework.multicast.message;

import java.util.List;

import org.greatfree.message.ServerMessage;

/**
 * 
 * @author libing
 * 
 * 04/12/2022
 *
 */
public class ClientBroadcastResponse extends ServerMessage
{
	private static final long serialVersionUID = 2900868274583877402L;

	private List<HelloWorldBroadcastResponse> responses;

	public ClientBroadcastResponse(List<HelloWorldBroadcastResponse> responses)
	{
		super(MultiAppID.CLIENT_BROADCAST_RESPONSE);
		this.responses = responses;
	}

	public List<HelloWorldBroadcastResponse> getResponses()
	{
		return this.responses;
	}
}
