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
public final class AsymmetricPrimitiveStream extends MessageStream<AsymmetricPrimitiveRequest>
{

	public AsymmetricPrimitiveStream(ObjectOutputStream out, Lock lock, AsymmetricPrimitiveRequest message)
	{
		super(out, lock, message);
	}

}
