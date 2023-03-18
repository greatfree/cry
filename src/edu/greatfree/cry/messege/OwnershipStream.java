package edu.greatfree.cry.messege;

import java.io.ObjectOutputStream;
import java.util.concurrent.locks.Lock;

import org.greatfree.server.MessageStream;

/**
 * 
 * @author libing
 * 
 * 04/20/2022
 *
 */
public class OwnershipStream extends MessageStream<OwnershipRequest>
{

	public OwnershipStream(ObjectOutputStream out, Lock lock, OwnershipRequest message)
	{
		super(out, lock, message);
	}

}
