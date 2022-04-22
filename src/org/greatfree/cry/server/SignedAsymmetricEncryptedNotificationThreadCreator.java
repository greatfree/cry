package org.greatfree.cry.server;

import org.greatfree.concurrency.reactive.NotificationQueueCreator;
import org.greatfree.cry.messege.SignedAsymmetricEncryptedNotification;

/**
 * 
 * @author libing
 * 
 * 04/20/2022
 *
 */
class SignedAsymmetricEncryptedNotificationThreadCreator implements NotificationQueueCreator<SignedAsymmetricEncryptedNotification, SignedAsymmetricEncryptedNotificationThread>
{

	@Override
	public SignedAsymmetricEncryptedNotificationThread createInstance(int taskSize)
	{
		return new SignedAsymmetricEncryptedNotificationThread(taskSize);
	}

}
