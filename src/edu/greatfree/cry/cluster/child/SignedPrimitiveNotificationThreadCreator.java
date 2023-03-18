package edu.greatfree.cry.cluster.child;

import org.greatfree.concurrency.reactive.NotificationQueueCreator;

import edu.greatfree.cry.messege.SignedPrimitiveNotification;

/**
 * 
 * @author libing
 * 
 * 05/12/2022
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
