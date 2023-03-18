package edu.greatfree.cry.cluster.child;

import org.greatfree.concurrency.reactive.RequestQueueCreator;

import edu.greatfree.cry.messege.PrivatePrimitiveRequest;
import edu.greatfree.cry.messege.PrivatePrimitiveStream;
import edu.greatfree.cry.messege.PrivateResponse;

/**
 * 
 * @author libing
 * 
 * 05/12/2022
 *
 */
final class PrivatePrimitiveRequestThreadCreator implements RequestQueueCreator<PrivatePrimitiveRequest, PrivatePrimitiveStream, PrivateResponse, PrivatePrimitiveRequestThread>
{

	@Override
	public PrivatePrimitiveRequestThread createInstance(int taskSize)
	{
		return new PrivatePrimitiveRequestThread(taskSize);
	}

}
