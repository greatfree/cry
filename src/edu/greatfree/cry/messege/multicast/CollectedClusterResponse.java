package edu.greatfree.cry.messege.multicast;

import java.util.List;

import org.greatfree.message.ServerMessage;

/**
 * 
 * @author libing
 * 
 * 05/11/2022
 *
 */
public class CollectedClusterResponse extends ServerMessage
{
	private static final long serialVersionUID = 7060450504124455025L;

	private List<PrimitiveMulticastResponse> responses;
	private PrimitiveMulticastResponse response;

	public CollectedClusterResponse(int type, List<PrimitiveMulticastResponse> responses)
	{
		super(type);
		this.responses = responses;
		this.response = null;
	}

	/*
	 * The constructor is used for generating the response for the partition based queries. Only a single response is enough for replication. 09/08/2020, Bing Li
	 */
	public CollectedClusterResponse(int type, PrimitiveMulticastResponse response)
	{
		super(type);
		this.responses = null;
		this.response = response;
	}

	public List<PrimitiveMulticastResponse> getResponses()
	{
		return this.responses;
	}
	
	public PrimitiveMulticastResponse getResponse()
	{
		return this.response;
	}
}
