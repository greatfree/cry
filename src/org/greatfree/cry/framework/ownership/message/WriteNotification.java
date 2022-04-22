package org.greatfree.cry.framework.ownership.message;

import org.greatfree.message.container.Notification;

/**
 * 
 * @author libing
 * 
 * 03/22/2022
 *
 */
public class WriteNotification extends Notification
{
	private static final long serialVersionUID = -5842163383043090709L;
	
	private String notification;

	public WriteNotification(String notification)
	{
		super(OwnerAppID.WRITE_NOTIFICATION);
		this.notification = notification;
	}

	public String getNotification()
	{
		return this.notification;
	}
}
