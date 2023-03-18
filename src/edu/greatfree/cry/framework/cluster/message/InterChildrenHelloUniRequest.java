package edu.greatfree.cry.framework.cluster.message;

import org.greatfree.message.multicast.container.IntercastRequest;

import edu.greatfree.cry.messege.multicast.InterChildrenRequest;

/**
 * 
 * @author libing
 * 
 * 06/20/2022
 *
 */
public class InterChildrenHelloUniRequest extends InterChildrenRequest
{
	private static final long serialVersionUID = -2577277308372480132L;
	
	private String additionalMessage;

	public InterChildrenHelloUniRequest(IntercastRequest ir, String additionalMessage)
	{
		super(ir);
		this.additionalMessage = additionalMessage;
	}

	public String getAdditionalMessage()
	{
		return this.additionalMessage;
	}
}
