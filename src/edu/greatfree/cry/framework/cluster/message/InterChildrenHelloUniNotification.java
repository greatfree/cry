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
public class InterChildrenHelloUniNotification extends InterChildrenNotification
{
	private static final long serialVersionUID = -5980441548318629620L;
	
	private String additionalMessage;

	public InterChildrenHelloUniNotification(IntercastNotification in, String additionalMessage)
	{
		super(in);
		this.additionalMessage = additionalMessage;
	}

	public String getAdditionalMessage()
	{
		return this.additionalMessage;
	}
}
