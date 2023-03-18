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
public class InterChildrenHelloAnyNotification extends InterChildrenNotification
{
	private static final long serialVersionUID = 1500221462323130055L;

	private String additionalMessage;

	public InterChildrenHelloAnyNotification(IntercastNotification in, String additionalMessage)
	{
		super(in);
		this.additionalMessage = additionalMessage;
	}

	public String getAdditionalMessage()
	{
		return this.additionalMessage;
	}
}
