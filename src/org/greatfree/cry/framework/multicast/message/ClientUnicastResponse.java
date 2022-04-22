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
public class ClientUnicastResponse extends ServerMessage
{
	private static final long serialVersionUID = 4069678254995057869L;

	private List<HelloWorldUnicastResponse> responses;

	public ClientUnicastResponse(List<HelloWorldUnicastResponse> responses)
	{
		super(MultiAppID.CLIENT_UNICAST_RESPONSE);
		this.responses = responses;
	}

	public List<HelloWorldUnicastResponse> getResponses()
	{
		return this.responses;
	}
}
