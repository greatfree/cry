package org.greatfree.cry.server;

import org.greatfree.concurrency.reactive.NotificationQueueCreator;
import org.greatfree.cry.messege.EncryptedNotification;

/**
 * 
 * @author libing
 * 
 * 04/28/2022
 *
 */
final class SymmetricEncryptedNotificationThreadCreator implements NotificationQueueCreator<EncryptedNotification, SymmetricEncryptedNotificationThread>
{

	@Override
	public SymmetricEncryptedNotificationThread createInstance(int taskSize)
	{
		return new SymmetricEncryptedNotificationThread(taskSize);
	}

}
