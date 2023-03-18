package edu.greatfree.cry.cluster.child;

import org.greatfree.concurrency.reactive.NotificationQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.message.ServerMessage;
import org.greatfree.util.ServerStatus;

import edu.greatfree.cry.exceptions.NonPublicMachineException;
import edu.greatfree.cry.server.ServiceProvider;

/**
 * 
 * @author libing
 * 
 * 05/09/2022
 *
 */
final class ServerNotificationThread extends NotificationQueue<ServerMessage>
{

	public ServerNotificationThread(int taskSize)
	{
		super(taskSize);
	}

	@Override
	public void run()
	{
		ServerMessage notification;
		while (!super.isShutdown())
		{
			while (!super.isEmpty())
			{
				try
				{
					notification = super.dequeue();
					ServiceProvider.CRY().processClusterChildNotification(super.getServerKey(), notification);
					super.disposeMessage(notification);
				}
				catch (InterruptedException | NonPublicMachineException e)
				{
					e.printStackTrace();
				}
			}
			try
			{
				super.holdOn(ServerConfig.NOTIFICATION_THREAD_WAIT_TIME);
			}
			catch (InterruptedException e)
			{
				ServerStatus.FREE().printException(e);
			}
		}
		
	}

}
