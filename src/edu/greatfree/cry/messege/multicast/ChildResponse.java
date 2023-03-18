package edu.greatfree.cry.messege.multicast;

import org.greatfree.cluster.message.ClusterMessageType;
import org.greatfree.message.ServerMessage;

/**
 * 
 * @author libing
 * 
 * 05/11/2022
 *
 */
public class ChildResponse extends ServerMessage
{
	private static final long serialVersionUID = -347864636262431205L;

	private PrimitiveMulticastResponse response;

	/*
	 * The constructor is used when the child of the cluster is normal in terms of the pressure of workload. 09/11/2020, Bing Li
	 */
	public ChildResponse(PrimitiveMulticastResponse response)
	{
		super(ClusterMessageType.CHILD_RESPONSE);
		this.response = response;
	}

	public PrimitiveMulticastResponse getResponse()
	{
		return this.response;
	}
}
