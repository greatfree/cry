package edu.greatfree.cry.messege;

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
public class AsymmetricEncryptedStream extends MessageStream<AsymmetricEncryptedRequest>
{

	public AsymmetricEncryptedStream(ObjectOutputStream out, Lock lock, AsymmetricEncryptedRequest message)
	{
		super(out, lock, message);
	}

}
