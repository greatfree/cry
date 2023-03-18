package edu.greatfree.cry.server;

import org.greatfree.concurrency.reactive.RequestQueueCreator;

import edu.greatfree.cry.messege.AbandonOwnershipRequest;
import edu.greatfree.cry.messege.AbandonOwnershipResponse;
import edu.greatfree.cry.messege.AbandonOwnershipStream;

/**
 * 
 * @author libing
 * 
 * 05/17/2022
 *
 */
final class AbandonOwnershipThreadCreator implements RequestQueueCreator<AbandonOwnershipRequest, AbandonOwnershipStream, AbandonOwnershipResponse, AbandonOwnershipThread>
{

	@Override
	public AbandonOwnershipThread createInstance(int taskSize)
	{
		return new AbandonOwnershipThread(taskSize);
	}

}
