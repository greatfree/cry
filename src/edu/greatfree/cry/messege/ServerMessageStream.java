package edu.greatfree.cry.messege;

import java.io.ObjectOutputStream;
import java.util.concurrent.locks.Lock;

import org.greatfree.message.ServerMessage;
import org.greatfree.server.MessageStream;

/**
 * 
 * @author libing
 * 
 * 05/04/2022
 *
 */
public class ServerMessageStream extends MessageStream<ServerMessage>
{

	public ServerMessageStream(ObjectOutputStream out, Lock lock, ServerMessage message)
	{
		super(out, lock, message);
	}

}
