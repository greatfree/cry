package org.greatfree.cry.framework.tncs.message;

import org.greatfree.message.container.Notification;

/**
 * 
 * @author libing
 * 
 * 01/07/2022, Bing Li
 *
 */
public class ShutdownNotification extends Notification
{
	private static final long serialVersionUID = 2292857280367360808L;

	public ShutdownNotification()
	{
		super(CSAppID.SHUTDOWN_NOTIFICATION);
	}

}
