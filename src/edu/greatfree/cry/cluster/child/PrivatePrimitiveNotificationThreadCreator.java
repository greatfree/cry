package edu.greatfree.cry.cluster.child;

import org.greatfree.concurrency.reactive.NotificationQueueCreator;

import edu.greatfree.cry.messege.PrivatePrimitiveNotification;

/**
 * 
 * @author libing
 * 
 * 05/12/2022
 *
 */
final class PrivatePrimitiveNotificationThreadCreator implements NotificationQueueCreator<PrivatePrimitiveNotification, PrivatePrimitiveNotificationThread>
{

	@Override
	public PrivatePrimitiveNotificationThread createInstance(int taskSize)
	{
		return new PrivatePrimitiveNotificationThread(taskSize);
	}

}
