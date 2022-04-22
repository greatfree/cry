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
public class PublicCryptoSessionStream extends MessageStream<PublicCryptoSessionRequest>
{

	public PublicCryptoSessionStream(ObjectOutputStream out, Lock lock, PublicCryptoSessionRequest message)
	{
		super(out, lock, message);
	}

}
