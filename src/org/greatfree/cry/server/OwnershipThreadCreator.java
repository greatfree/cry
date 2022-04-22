package org.greatfree.cry.server;

import org.greatfree.concurrency.reactive.RequestQueueCreator;
import org.greatfree.cry.messege.OwnershipRequest;
import org.greatfree.cry.messege.OwnershipResponse;
import org.greatfree.cry.messege.OwnershipStream;

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
