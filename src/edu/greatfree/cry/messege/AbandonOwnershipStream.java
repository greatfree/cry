package edu.greatfree.cry.messege;

import java.io.ObjectOutputStream;
import java.util.concurrent.locks.Lock;

import org.greatfree.server.MessageStream;

/**
 * 
 * @author libing
 * 
 * 05/16/2022
 *
 */
public class AbandonOwnershipStream extends MessageStream<AbandonOwnershipRequest>
{

	public AbandonOwnershipStream(ObjectOutputStream out, Lock lock, AbandonOwnershipRequest message)
	{
		super(out, lock, message);
	}

}
