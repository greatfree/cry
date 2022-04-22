package org.greatfree.cry.server;

import org.greatfree.concurrency.reactive.RequestQueueCreator;
import org.greatfree.cry.messege.SymmetricCryptoSessionRequest;
import org.greatfree.cry.messege.SymmetricCryptoSessionResponse;
import org.greatfree.cry.messege.SymmetricCryptoSessionStream;

/**
 * 
 * @author libing
 * 
 * 04/19/2022
 *
 */
class SymmetricCryptoSessionThreadCreator implements RequestQueueCreator<SymmetricCryptoSessionRequest, SymmetricCryptoSessionStream, SymmetricCryptoSessionResponse, SymmetricCryptoSessionThread>
{

	@Override
	public SymmetricCryptoSessionThread createInstance(int taskSize)
	{
		return new SymmetricCryptoSessionThread(taskSize);
	}

}
