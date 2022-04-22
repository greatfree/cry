package org.greatfree.cry.server;

import org.greatfree.concurrency.reactive.RequestQueueCreator;
import org.greatfree.cry.messege.PublicCryptoSessionRequest;
import org.greatfree.cry.messege.PublicCryptoSessionResponse;
import org.greatfree.cry.messege.PublicCryptoSessionStream;

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
