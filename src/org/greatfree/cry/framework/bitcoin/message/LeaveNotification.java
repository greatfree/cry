package org.greatfree.cry.framework.bitcoin.message;

import org.greatfree.message.container.Notification;

/**
 * 
 * @author Bing Li
 * 
 * 02/16/2022
 *
 */
public class LeaveNotification extends Notification
{
	private static final long serialVersionUID = -4569332417362479414L;
	
	private String newSucceedingPN;

	public LeaveNotification(String newSucceedingPN)
	{
		super(CoinAppID.LEAVE_NOTIFICATION);
		this.newSucceedingPN = newSucceedingPN;
	}

	public String getNewSucceedingPN()
	{
		return this.newSucceedingPN;
	}
}
