package edu.greatfree.cry.multicast;

import org.greatfree.concurrency.reactive.NotificationQueueCreator;

import edu.greatfree.cry.messege.SymmetricPrimitiveNotification;

/**
 * 
 * @author libing
 * 
 * 04/30/2022
 *
 */
public final class SymmetricPrimitiveNotificationThreadCreator implements NotificationQueueCreator<SymmetricPrimitiveNotification, SymmetricPrimitiveNotificationThread>
{

	@Override
	public SymmetricPrimitiveNotificationThread createInstance(int taskSize)
	{
		return new SymmetricPrimitiveNotificationThread(taskSize);
	}

}
