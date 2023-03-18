package edu.greatfree.cry.multicast.root;

import org.greatfree.concurrency.reactive.NotificationQueueCreator;

import edu.greatfree.cry.messege.multicast.PrimitiveMulticastResponse;

/**
 * 
 * @author libing
 * 
 * 04/29/2022
 *
 */
// final class MulticastResponseThreadCreator implements NotificationQueueCreator<MulticastResponse, MulticastResponseThread>
final class MulticastResponseThreadCreator implements NotificationQueueCreator<PrimitiveMulticastResponse, MulticastResponseThread>
{

	@Override
	public MulticastResponseThread createInstance(int taskSize)
	{
		return new MulticastResponseThread(taskSize);
	}

}
