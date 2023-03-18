package edu.greatfree.cry.multicast.child;

import org.greatfree.concurrency.reactive.NotificationQueueCreator;

import edu.greatfree.cry.messege.multicast.PrimitiveMulticastNotification;

/**
 * 
 * @author libing
 * 
 * 04/30/2022
 *
 */
// final class MulticastNotificationThreadCreator implements NotificationQueueCreator<MulticastNotification, MulticastNotificationThread>
final class MulticastNotificationThreadCreator implements NotificationQueueCreator<PrimitiveMulticastNotification, MulticastNotificationThread>
{

	@Override
	public MulticastNotificationThread createInstance(int taskSize)
	{
		return new MulticastNotificationThread(taskSize);
	}

}
