package org.greatfree.cry.multicast.root;

import org.greatfree.concurrency.reactive.NotificationQueue;
import org.greatfree.cry.exceptions.NonPublicMachineException;
import org.greatfree.cry.server.ServiceProvider;
import org.greatfree.data.ServerConfig;
import org.greatfree.message.multicast.MulticastResponse;
import org.greatfree.util.ServerStatus;

/**
 * 
 * @author libing
 * 
 * 04/29/2022
 *
 */
final class MulticastResponseThread extends NotificationQueue<MulticastResponse>
{

	public MulticastResponseThread(int taskSize)
	{
		super(taskSize);
	}

	@Override
	public void run()
	{
		MulticastResponse notification;
		while (!super.isShutdown())
		{
			while (!super.isEmpty())
			{
				try
				{
					notification = super.dequeue();
					ServiceProvider.CRY().processMulticastResponse(super.getServerKey(), notification);
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
