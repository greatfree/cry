package org.greatfree.cry.multicast.child;

import org.greatfree.concurrency.reactive.NotificationQueueCreator;
import org.greatfree.message.multicast.MulticastNotification;

/**
 * 
 * @author libing
 * 
 * 04/30/2022
 *
 */
final class MulticastNotificationThreadCreator implements NotificationQueueCreator<MulticastNotification, MulticastNotificationThread>
{

	@Override
	public MulticastNotificationThread createInstance(int taskSize)
	{
		return new MulticastNotificationThread(taskSize);
	}

}
