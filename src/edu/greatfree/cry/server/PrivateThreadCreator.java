package edu.greatfree.cry.server;

import org.greatfree.concurrency.reactive.RequestQueueCreator;

import edu.greatfree.cry.messege.PrivateRequest;
import edu.greatfree.cry.messege.PrivateResponse;
import edu.greatfree.cry.messege.PrivateStream;

/**
 * 
 * @author libing
 * 
 * 04/20/2022
 *
 */
class PrivateThreadCreator implements RequestQueueCreator<PrivateRequest, PrivateStream, PrivateResponse, PrivateThread>
{

	@Override
	public PrivateThread createInstance(int taskSize)
	{
		return new PrivateThread(taskSize);
	}

}
