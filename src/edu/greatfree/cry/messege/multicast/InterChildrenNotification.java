package edu.greatfree.cry.messege.multicast;

import org.greatfree.message.multicast.MulticastMessageType;
import org.greatfree.message.multicast.container.IntercastNotification;

/**
 * 
 * @author libing
 * 
 * 05/11/2022
 *
 */
public abstract class InterChildrenNotification extends ClusterNotification
{
	private static final long serialVersionUID = -6742448652932691832L;

	private IntercastNotification in;

	public InterChildrenNotification(IntercastNotification in)
	{
		super(MulticastMessageType.INTER_CHILDEN_NOTIFICATION, in.getApplicationID());
		this.in = in;
	}
	
	public IntercastNotification getIntercastNotification()
	{
		return this.in;
	}
}
