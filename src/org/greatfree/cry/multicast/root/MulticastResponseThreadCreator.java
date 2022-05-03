package org.greatfree.cry.multicast.root;

import org.greatfree.concurrency.reactive.NotificationQueueCreator;
import org.greatfree.message.multicast.MulticastResponse;

/**
 * 
 * @author libing
 * 
 * 04/29/2022
 *
 */
final class MulticastResponseThreadCreator implements NotificationQueueCreator<MulticastResponse, MulticastResponseThread>
{

	@Override
	public MulticastResponseThread createInstance(int taskSize)
	{
		return new MulticastResponseThread(taskSize);
	}

}
