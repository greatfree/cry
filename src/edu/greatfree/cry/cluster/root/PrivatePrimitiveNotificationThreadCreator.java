package edu.greatfree.cry.cluster.root;

import org.greatfree.concurrency.reactive.NotificationQueueCreator;

import edu.greatfree.cry.messege.PrivatePrimitiveNotification;

/**
 * 
 * @author libing
 * 
 * 05/11/2022
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
