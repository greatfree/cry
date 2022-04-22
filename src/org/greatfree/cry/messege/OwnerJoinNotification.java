package org.greatfree.cry.messege;

import org.greatfree.message.container.Notification;

/**
 * 
 * The message is special since it is not sent remotely. Instead, it is an event to notify the upper level to make a callback. This is an important update in GreatFree. 03/24/2022, Bing Li
 * 
 * @author libing
 * 
 * 03/24/2022
 *
 */
public class OwnerJoinNotification extends Notification
{
	private static final long serialVersionUID = -1174745871472243681L;
	
	private String ownerName;

	public OwnerJoinNotification(String ownerName)
	{
		super(CryAppID.OWNER_JOIN_NOTIFICATION);
		this.ownerName = ownerName;
	}

	public String getOwnerName()
	{
		return this.ownerName;
	}
}
