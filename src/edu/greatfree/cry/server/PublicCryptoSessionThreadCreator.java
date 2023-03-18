package edu.greatfree.cry.server;

import org.greatfree.concurrency.reactive.RequestQueueCreator;

import edu.greatfree.cry.messege.PublicCryptoSessionRequest;
import edu.greatfree.cry.messege.PublicCryptoSessionResponse;
import edu.greatfree.cry.messege.PublicCryptoSessionStream;

/**
 * 
 * @author libing
 * 
 * 04/19/2022
 *
 */
class PublicCryptoSessionThreadCreator implements RequestQueueCreator<PublicCryptoSessionRequest, PublicCryptoSessionStream, PublicCryptoSessionResponse, PublicCryptoSessionThread>
{

	@Override
	public PublicCryptoSessionThread createInstance(int taskSize)
	{
		return new PublicCryptoSessionThread(taskSize);
	}

}
