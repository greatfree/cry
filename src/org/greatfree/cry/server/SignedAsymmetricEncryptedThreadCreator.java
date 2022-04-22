package org.greatfree.cry.server;

import org.greatfree.concurrency.reactive.RequestQueueCreator;
import org.greatfree.cry.messege.SignedAsymmetricEncryptedRequest;
import org.greatfree.cry.messege.SignedAsymmetricEncryptedResponse;
import org.greatfree.cry.messege.SignedAsymmetricEncryptedStream;

/**
 * 
 * @author libing
 * 
 * 04/19/2022
 *
 */
class SignedAsymmetricEncryptedThreadCreator implements RequestQueueCreator<SignedAsymmetricEncryptedRequest, SignedAsymmetricEncryptedStream, SignedAsymmetricEncryptedResponse, SignedAsymmetricEncryptedThread>
{

	@Override
	public SignedAsymmetricEncryptedThread createInstance(int taskSize)
	{
		return new SignedAsymmetricEncryptedThread(taskSize);
	}

}
