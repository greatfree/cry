package org.greatfree.cry.multicast.child;

import org.greatfree.concurrency.reactive.NotificationQueueCreator;
import org.greatfree.message.multicast.MulticastRequest;

/**
 * 
 * @author libing
 * 
 * 04/30/2022
 *
 */
final class MulticastRequestThreadCreator implements NotificationQueueCreator<MulticastRequest, MulticastRequestThread>
{

	@Override
	public MulticastRequestThread createInstance(int taskSize)
	{
		return new MulticastRequestThread(taskSize);
	}

}
