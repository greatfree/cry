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
public class PrivatePrimitiveStream extends MessageStream<PrivatePrimitiveRequest>
{

	public PrivatePrimitiveStream(ObjectOutputStream out, Lock lock, PrivatePrimitiveRequest message)
	{
		super(out, lock, message);
	}

}
