package org.greatfree.cry.server;

import org.greatfree.concurrency.reactive.NotificationQueueCreator;
import org.greatfree.message.container.Notification;

/**
 * 
 * @author libing
 * 
 * 01/06/2022, Bing Li
 *
 */
// class EncryptedNotificationThreadCreator implements NotificationQueueCreator<EncryptedNotification, EncryptedNotificationThread>
class EncryptedNotificationThreadCreator implements NotificationQueueCreator<Notification, EncryptedNotificationThread>
{

	@Override
	public EncryptedNotificationThread createInstance(int taskSize)
	{
		return new EncryptedNotificationThread(taskSize);
	}

}
