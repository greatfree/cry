package edu.greatfree.cry.multicast.child;

import java.util.logging.Logger;

import org.greatfree.concurrency.reactive.NotificationQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.util.ServerStatus;

import edu.greatfree.cry.exceptions.NonPublicMachineException;
import edu.greatfree.cry.messege.multicast.PrimitiveMulticastNotification;
import edu.greatfree.cry.server.ServiceProvider;

/**
 * 
 * The thread is shared with the one of multicasting. It is not correct. The bug should be fixed. 05/09/2022, Bing Li
 * 
 * @author libing
 * 
 * 04/30/2022
 *
 */
// final class MulticastNotificationThread extends NotificationQueue<MulticastNotification>
final class MulticastNotificationThread extends NotificationQueue<PrimitiveMulticastNotification>
{
	private final static Logger log = Logger.getLogger("org.greatfree.cry.multicast.child");

	public MulticastNotificationThread(int taskSize)
	{
		super(taskSize);
	}

	@Override
	public void run()
	{
//		MulticastNotification notification;
		PrimitiveMulticastNotification notification;
		while (!super.isShutdown())
		{
			while (!super.isEmpty())
			{
				try
				{
					notification = super.dequeue();
					log.info("super.getServerKey() for task = " + super.getServerKey());
					ServiceProvider.CRY().processMulticastNotification(super.getServerKey(), notification);
//					ServiceProvider.CRY().processNotification(super.getServerKey(), notification);
//					ServiceProvider.CRY().processClusterNotification(super.getServerKey(), notification);
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
