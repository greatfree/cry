package edu.greatfree.cry.server;

import org.greatfree.concurrency.reactive.RequestQueueCreator;

import edu.greatfree.cry.messege.AsymmetricEncryptedRequest;
import edu.greatfree.cry.messege.AsymmetricEncryptedResponse;
import edu.greatfree.cry.messege.AsymmetricEncryptedStream;

/**
 * 
 * @author libing
 * 
 * 04/19/2022
 *
 */
class AsymmetricEncryptedThreadCreator implements RequestQueueCreator<AsymmetricEncryptedRequest, AsymmetricEncryptedStream, AsymmetricEncryptedResponse, AsymmetricEncryptedThread>
{

	@Override
	public AsymmetricEncryptedThread createInstance(int taskSize)
	{
		return new AsymmetricEncryptedThread(taskSize);
	}

}
