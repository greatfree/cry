package edu.greatfree.cry.multicast.child;

import org.greatfree.concurrency.NotifierPool;
import org.greatfree.concurrency.ThreadPool;
import org.greatfree.data.ClientConfig;
import org.greatfree.message.ServerMessage;
import org.greatfree.server.ServerDispatcher;

import edu.greatfree.cry.messege.multicast.PrimitiveMulticastNotification;

/**
 * 
 * @author libing
 * 
 * 04/10/2022
 *
 */
// final class ChildAsyncMulticastEventer<Dispatcher extends CryptoCSDispatcher>
final class ChildAsyncMulticastEventer<Dispatcher extends ServerDispatcher<ServerMessage>>
{
//	private AsyncPool<PrimitiveMulticastNotification> actor;
	private NotifierPool<PrimitiveMulticastNotification> actor;
	private ThreadPool pool;

	public ChildAsyncMulticastEventer(ChildSyncMulticastor<Dispatcher> multicastor, ThreadPool pool)
	{
//		this.actor = new AsyncPool.ActorPoolBuilder<PrimitiveMulticastNotification>()
		this.actor = new NotifierPool.NotifierPoolBuilder<PrimitiveMulticastNotification>()
				.queueSize(ClientConfig.ASYNC_EVENT_QUEUE_SIZE)
				.notifierSize(ClientConfig.ASYNC_EVENTER_SIZE)
				.poolingWaitTime(ClientConfig.ASYNC_EVENTING_WAIT_TIME)
				.notifierWaitTime(ClientConfig.ASYNC_EVENTER_WAIT_TIME)
//				.waitRound(ClientConfig.ASYNC_EVENTER_WAIT_ROUND)
				.idleCheckDelay(ClientConfig.ASYNC_EVENT_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ClientConfig.ASYNC_EVENT_IDLE_CHECK_PERIOD)
				.schedulerPoolSize(ClientConfig.SCHEDULER_POOL_SIZE)
				.schedulerKeepAliveTime(ClientConfig.SCHEDULER_KEEP_ALIVE_TIME)
				.notifier(new ChildEventActor<Dispatcher>(multicastor))
				.build();
		
		this.pool = pool;
	}
	
	public void dispose() throws InterruptedException
	{
		this.actor.dispose();
	}
	
	public void notify(PrimitiveMulticastNotification notification)
	{
		if (!this.actor.isReady())
		{
			this.pool.execute(this.actor);
		}
//		this.actor.perform(notification);
		this.actor.notify(notification);
	}
}
