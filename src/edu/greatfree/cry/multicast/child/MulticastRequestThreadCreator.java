package edu.greatfree.cry.multicast.child;

import org.greatfree.concurrency.reactive.NotificationQueueCreator;

import edu.greatfree.cry.messege.multicast.PrimitiveMulticastRequest;

/**
 * 
 * @author libing
 * 
 * 04/30/2022
 *
 */
// final class MulticastRequestThreadCreator implements NotificationQueueCreator<MulticastRequest, MulticastRequestThread>
final class MulticastRequestThreadCreator implements NotificationQueueCreator<PrimitiveMulticastRequest, MulticastRequestThread>
{

	@Override
	public MulticastRequestThread createInstance(int taskSize)
	{
		return new MulticastRequestThread(taskSize);
	}

}
