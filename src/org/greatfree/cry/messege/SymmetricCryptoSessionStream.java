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
public class SymmetricCryptoSessionStream extends MessageStream<SymmetricCryptoSessionRequest>
{

	public SymmetricCryptoSessionStream(ObjectOutputStream out, Lock lock, SymmetricCryptoSessionRequest message)
	{
		super(out, lock, message);
	}

}
