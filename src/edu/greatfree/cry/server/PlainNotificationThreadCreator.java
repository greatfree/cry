package edu.greatfree.cry.server;

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
class PlainNotificationThreadCreator implements NotificationQueueCreator<Notification, PlainNotificationThread>
{

	@Override
	public PlainNotificationThread createInstance(int taskSize)
	{
		return new PlainNotificationThread(taskSize);
	}

}
