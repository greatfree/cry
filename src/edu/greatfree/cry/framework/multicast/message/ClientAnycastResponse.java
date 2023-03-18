package edu.greatfree.cry.framework.multicast.message;

import java.util.List;

import org.greatfree.message.ServerMessage;

/**
 * 
 * @author libing
 * 
 * 04/12/2022
 *
 */
public class ClientAnycastResponse extends ServerMessage
{
	private static final long serialVersionUID = -7964689874472409192L;

	private List<HelloWorldAnycastResponse> responses;

	public ClientAnycastResponse(List<HelloWorldAnycastResponse> responses)
	{
		super(MultiAppID.CLIENT_ANYCAST_RESPONSE);
		this.responses = responses;
	}

	public List<HelloWorldAnycastResponse> getResponses()
	{
		return this.responses;
	}
}
