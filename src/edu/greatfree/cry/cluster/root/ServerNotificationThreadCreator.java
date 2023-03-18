package edu.greatfree.cry.cluster.root;

import org.greatfree.concurrency.reactive.NotificationQueueCreator;
import org.greatfree.message.ServerMessage;

/**
 * 
 * @author libing
 * 
 * 05/04/2022
 *
 */
final class ServerNotificationThreadCreator implements NotificationQueueCreator<ServerMessage, ServerNotificationThread>
{

	@Override
	public ServerNotificationThread createInstance(int taskSize)
	{
		return new ServerNotificationThread(taskSize);
	}

}
