package org.greatfree.cry.framework.multisigned.message;

import org.greatfree.message.container.Notification;

/**
 * 
 * @author libing
 * 
 * 03/24/2022
 *
 */
public class StopServerNotification extends Notification
{
	private static final long serialVersionUID = -816620627198608315L;
	
	public StopServerNotification()
	{
		super(MSAppID.STOP_SERVER_NOTIFICATION);
	}

}
