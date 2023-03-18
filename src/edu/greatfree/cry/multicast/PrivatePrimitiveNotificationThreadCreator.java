package edu.greatfree.cry.multicast;

import org.greatfree.concurrency.reactive.NotificationQueueCreator;

import edu.greatfree.cry.messege.PrivatePrimitiveNotification;

/**
 * 
 * @author libing
 * 
 * 05/01/2022
 *
 */
public final class PrivatePrimitiveNotificationThreadCreator implements NotificationQueueCreator<PrivatePrimitiveNotification, PrivatePrimitiveNotificationThread>
{

	@Override
	public PrivatePrimitiveNotificationThread createInstance(int taskSize)
	{
		return new PrivatePrimitiveNotificationThread(taskSize);
	}

}
