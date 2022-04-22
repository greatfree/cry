package org.greatfree.cry.messege;

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
public class PrivateStream extends MessageStream<PrivateRequest>
{

	public PrivateStream(ObjectOutputStream out, Lock lock, PrivateRequest message)
	{
		super(out, lock, message);
	}

}
