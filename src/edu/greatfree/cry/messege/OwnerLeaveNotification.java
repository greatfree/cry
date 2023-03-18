package edu.greatfree.cry.messege;

import org.greatfree.message.container.Notification;

/**
 * 
 * @author libing
 * 
 * 03/24/2022
 *
 */
public class OwnerLeaveNotification extends Notification
{
	private static final long serialVersionUID = -6202660389802764544L;
	
	private String ownerName;

	public OwnerLeaveNotification(String ownerName)
	{
		super(CryAppID.OWNER_LEAVE_NOTIFICATION);
		this.ownerName = ownerName;
	}

	public String getOwnerName()
	{
		return this.ownerName;
	}
}
