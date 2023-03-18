package edu.greatfree.cry.messege;

import java.io.ObjectOutputStream;
import java.util.concurrent.locks.Lock;

import org.greatfree.server.MessageStream;

/**
 * 
 * @author libing
 * 
 * 05/04/2022
 *
 */
public class SignedPrimitiveStream extends MessageStream<SignedPrimitiveRequest>
{

	public SignedPrimitiveStream(ObjectOutputStream out, Lock lock, SignedPrimitiveRequest message)
	{
		super(out, lock, message);
	}

}
