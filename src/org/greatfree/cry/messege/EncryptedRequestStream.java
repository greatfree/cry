package org.greatfree.cry.messege;

import java.io.ObjectOutputStream;
import java.util.concurrent.locks.Lock;

import org.greatfree.message.container.Request;
import org.greatfree.server.MessageStream;

/**
 * 
 * @author libing
 * 
 * 01/06/2022, Bing Li
 *
 */
// public class EncryptedRequestStream extends MessageStream<EncryptedRequest>
public class EncryptedRequestStream extends MessageStream<Request>
{

//	public EncryptedRequestStream(ObjectOutputStream out, Lock lock, EncryptedRequest message)
	public EncryptedRequestStream(ObjectOutputStream out, Lock lock, Request message)
	{
		super(out, lock, message);
	}

}
