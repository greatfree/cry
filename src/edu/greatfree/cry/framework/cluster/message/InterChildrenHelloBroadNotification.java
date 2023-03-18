package edu.greatfree.cry.framework.cluster.message;

import org.greatfree.message.multicast.container.IntercastNotification;

import edu.greatfree.cry.messege.multicast.InterChildrenNotification;

/**
 * 
 * @author libing
 * 
 * 06/20/2022
 *
 */
public class InterChildrenHelloBroadNotification extends InterChildrenNotification
{
	private static final long serialVersionUID = 407286469823950458L;

	private String additionalMessage;

	public InterChildrenHelloBroadNotification(IntercastNotification in, String additionalMessage)
	{
		super(in);
		this.additionalMessage = additionalMessage;
	}

	public String getAdditionalMessage()
	{
		return this.additionalMessage;
	}
}
