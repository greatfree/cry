package edu.greatfree.cry.cluster.root;

import org.greatfree.concurrency.reactive.RequestQueueCreator;

import edu.greatfree.cry.messege.PrivatePrimitiveRequest;
import edu.greatfree.cry.messege.PrivatePrimitiveStream;
import edu.greatfree.cry.messege.PrivateResponse;

/**
 * 
 * @author libing
 * 
 * 05/04/2022
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
