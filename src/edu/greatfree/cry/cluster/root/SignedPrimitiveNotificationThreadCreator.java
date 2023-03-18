package edu.greatfree.cry.cluster.root;

import org.greatfree.concurrency.reactive.NotificationQueueCreator;

import edu.greatfree.cry.messege.SignedPrimitiveNotification;

/**
 * 
 * @author libing
 * 
 * 05/11/2022
 *
 */
final class SignedPrimitiveNotificationThreadCreator implements NotificationQueueCreator<SignedPrimitiveNotification, SignedPrimitiveNotificationThread>
{

	@Override
	public SignedPrimitiveNotificationThread createInstance(int taskSize)
	{
		return new SignedPrimitiveNotificationThread(taskSize);
	}

}
