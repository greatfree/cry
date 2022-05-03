package org.greatfree.cry.multicast.child;

import org.greatfree.concurrency.reactive.NotificationQueue;
import org.greatfree.cry.exceptions.NonPublicMachineException;
import org.greatfree.cry.server.ServiceProvider;
import org.greatfree.data.ServerConfig;
import org.greatfree.message.multicast.MulticastRequest;
import org.greatfree.util.ServerStatus;

/**
 * 
 * @author libing
 * 
 * 04/30/2022
 *
 */
final class MulticastRequestThread extends NotificationQueue<MulticastRequest>
{

	public MulticastRequestThread(int taskSize)
	{
		super(taskSize);
	}

	@Override
	public void run()
	{
		MulticastRequest notification;
		while (!super.isShutdown())
		{
			while (!super.isEmpty())
			{
				try
				{
					notification = super.dequeue();
					ServiceProvider.CRY().processMulticastRequest(super.getServerKey(), notification);
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
