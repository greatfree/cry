package edu.greatfree.cry.framework.cluster.message;

import org.greatfree.message.multicast.container.IntercastRequest;

import edu.greatfree.cry.messege.multicast.InterChildrenRequest;

/**
 * 
 * @author libing
 * 
 * 06/21/2022
 *
 */
public class InterChildrenHelloBroadRequest extends InterChildrenRequest
{
	private static final long serialVersionUID = -3583784804885619820L;

	private String additionalMessage;

	public InterChildrenHelloBroadRequest(IntercastRequest ir, String additionalMessage)
	{
		super(ir);
		this.additionalMessage = additionalMessage;
	}

	public String getAdditionalMessage()
	{
		return this.additionalMessage;
	}
}
