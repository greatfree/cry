package edu.greatfree.cry.server;

import org.greatfree.concurrency.reactive.RequestQueueCreator;

import edu.greatfree.cry.messege.OwnershipRequest;
import edu.greatfree.cry.messege.OwnershipResponse;
import edu.greatfree.cry.messege.OwnershipStream;

/**
 * 
 * @author libing
 * 
 * 04/20/2022
 *
 */
class OwnershipThreadCreator implements RequestQueueCreator<OwnershipRequest, OwnershipStream, OwnershipResponse, OwnershipThread>
{

	@Override
	public OwnershipThread createInstance(int taskSize)
	{
		return new OwnershipThread(taskSize);
	}

}
