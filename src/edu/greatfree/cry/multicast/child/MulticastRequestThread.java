package edu.greatfree.cry.multicast.child;

import org.greatfree.concurrency.reactive.NotificationQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.util.ServerStatus;

import edu.greatfree.cry.exceptions.NonPublicMachineException;
import edu.greatfree.cry.messege.multicast.PrimitiveMulticastRequest;
import edu.greatfree.cry.server.ServiceProvider;

/**
 * 
 * @author libing
 * 
 * 04/30/2022
 *
 */
// final class MulticastRequestThread extends NotificationQueue<MulticastRequest>
final class MulticastRequestThread extends NotificationQueue<PrimitiveMulticastRequest>
{

	public MulticastRequestThread(int taskSize)
	{
		super(taskSize);
	}

	@Override
	public void run()
	{
		PrimitiveMulticastRequest notification;
		while (!super.isShutdown())
		{
			while (!super.isEmpty())
			{
				try
				{
					notification = super.dequeue();
					ServiceProvider.CRY().processMulticastRequest(super.getServerKey(), notification);
//					ServiceProvider.CRY().processRequest(super.getServerKey(), notification);
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
