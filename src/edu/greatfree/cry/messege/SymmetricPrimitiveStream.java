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
public class SymmetricPrimitiveStream extends MessageStream<SymmetricPrimitiveRequest>
{

	public SymmetricPrimitiveStream(ObjectOutputStream out, Lock lock, SymmetricPrimitiveRequest message)
	{
		super(out, lock, message);
	}

}
