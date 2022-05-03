package org.greatfree.cry.multicast.child;

import java.util.logging.Logger;

import org.greatfree.concurrency.reactive.NotificationQueue;
import org.greatfree.cry.exceptions.NonPublicMachineException;
import org.greatfree.cry.server.ServiceProvider;
import org.greatfree.data.ServerConfig;
import org.greatfree.message.multicast.MulticastNotification;
import org.greatfree.util.ServerStatus;

/**
 * 
 * @author libing
 * 
 * 04/30/2022
 *
 */
final class MulticastNotificationThread extends NotificationQueue<MulticastNotification>
{
	private final static Logger log = Logger.getLogger("org.greatfree.cry.multicast.child");

	public MulticastNotificationThread(int taskSize)
	{
		super(taskSize);
	}

	@Override
	public void run()
	{
		MulticastNotification notification;
		while (!super.isShutdown())
		{
			while (!super.isEmpty())
			{
				try
				{
					notification = super.dequeue();
					log.info("super.getServerKey() for task = " + super.getServerKey());
					ServiceProvider.CRY().processMulticastNotification(super.getServerKey(), notification);
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
