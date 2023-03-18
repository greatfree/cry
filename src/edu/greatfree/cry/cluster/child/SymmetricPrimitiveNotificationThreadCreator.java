package edu.greatfree.cry.cluster.child;

import org.greatfree.concurrency.reactive.NotificationQueueCreator;

import edu.greatfree.cry.messege.SymmetricPrimitiveNotification;

/**
 * 
 * @author libing
 * 
 * 05/12/2022
 *
 */
final class SymmetricPrimitiveNotificationThreadCreator implements NotificationQueueCreator<SymmetricPrimitiveNotification, SymmetricPrimitiveNotificationThread>
{

	@Override
	public SymmetricPrimitiveNotificationThread createInstance(int taskSize)
	{
		return new SymmetricPrimitiveNotificationThread(taskSize);
	}

}
