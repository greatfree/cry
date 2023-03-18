package edu.greatfree.cry.server;

import org.greatfree.concurrency.reactive.NotificationQueueCreator;

import edu.greatfree.cry.messege.AsymmetricEncryptedNotification;

/**
 * 
 * @author libing
 * 
 * 04/20/2022
 *
 */
class AsymmetricEncryptedNotificationThreadCreator implements NotificationQueueCreator<AsymmetricEncryptedNotification, AsymmetricEncryptedNotificationThread>
{

	@Override
	public AsymmetricEncryptedNotificationThread createInstance(int taskSize)
	{
		return new AsymmetricEncryptedNotificationThread(taskSize);
	}

}
