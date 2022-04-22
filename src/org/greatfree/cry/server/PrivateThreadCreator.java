package org.greatfree.cry.server;

import org.greatfree.concurrency.reactive.RequestQueueCreator;
import org.greatfree.cry.messege.PrivateRequest;
import org.greatfree.cry.messege.PrivateResponse;
import org.greatfree.cry.messege.PrivateStream;

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
