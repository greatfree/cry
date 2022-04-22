package org.greatfree.cry.server;

import org.greatfree.concurrency.reactive.NotificationQueueCreator;
import org.greatfree.cry.messege.PrivateNotification;

/**
 * 
 * @author libing
 * 
 * 04/20/2022
 *
 */
class PrivateNotificationThreadCreator implements NotificationQueueCreator<PrivateNotification, PrivateNotificationThread>
{

	@Override
	public PrivateNotificationThread createInstance(int taskSize)
	{
		return new PrivateNotificationThread(taskSize);
	}

}
