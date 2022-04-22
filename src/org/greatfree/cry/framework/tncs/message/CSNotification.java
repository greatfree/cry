package org.greatfree.cry.framework.tncs.message;

import org.greatfree.message.container.Notification;

/**
 * 
 * @author libing
 * 
 * 01/07/2022, Bing Li
 *
 */
public class CSNotification extends Notification
{
	private static final long serialVersionUID = -1676698435411916803L;
	
	private String message;

	public CSNotification(String message)
	{
		super(CSAppID.CS_NOTIFICATION);
		this.message = message;
	}

	public String getMessage()
	{
		return this.message;
	}
}
