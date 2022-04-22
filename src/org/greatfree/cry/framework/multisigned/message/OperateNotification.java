package org.greatfree.cry.framework.multisigned.message;

import org.greatfree.message.container.Notification;

/**
 * 
 * @author libing
 * 
 * 03/24/2022
 *
 */
public class OperateNotification extends Notification
{
	private static final long serialVersionUID = 7930373825719059380L;
	
	private String operation;
	private String description;

	public OperateNotification(String operation, String description)
	{
		super(MSAppID.OPERATE_NOTIFICATION);
		this.operation = operation;
		this.description = description;
	}

	public String getOperation()
	{
		return this.operation;
	}
	
	public String getDescription()
	{
		return this.description;
	}
}
