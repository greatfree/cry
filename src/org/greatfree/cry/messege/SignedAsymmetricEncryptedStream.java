package org.greatfree.cry.messege;

import java.io.ObjectOutputStream;
import java.util.concurrent.locks.Lock;

import org.greatfree.server.MessageStream;

/**
 * 
 * @author libing
 * 
 * 04/19/2022
 *
 */
public class SignedAsymmetricEncryptedStream extends MessageStream<SignedAsymmetricEncryptedRequest>
{

	public SignedAsymmetricEncryptedStream(ObjectOutputStream out, Lock lock, SignedAsymmetricEncryptedRequest message)
	{
		super(out, lock, message);
	}

}
