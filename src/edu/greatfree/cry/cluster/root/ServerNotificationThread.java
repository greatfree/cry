package edu.greatfree.cry.cluster.root;

import java.util.logging.Logger;

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
 * 05/04/2022
 *
 */
final class ServerNotificationThread extends NotificationQueue<ServerMessage>
{
	private final static Logger log = Logger.getLogger("org.greatfree.cry.cluster.root");

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
					ServiceProvider.CRY().processClusterRootNotification(super.getServerKey(), notification);
					super.disposeMessage(notification);
				}
				catch (NonPublicMachineException e)
				{
					log.info("The cluster is not public!");
				}
				catch (InterruptedException e)
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
