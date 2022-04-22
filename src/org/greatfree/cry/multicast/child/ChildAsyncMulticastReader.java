package org.greatfree.cry.multicast.child;

import org.greatfree.concurrency.AsyncPool;
import org.greatfree.concurrency.ThreadPool;
import org.greatfree.data.ClientConfig;
import org.greatfree.message.multicast.MulticastRequest;

/**
 * 
 * @author libing
 * 
 * 04/10/2022
 *
 */
final class ChildAsyncMulticastReader
{
	private AsyncPool<MulticastRequest> actor;
	private ThreadPool pool;

	public ChildAsyncMulticastReader(ChildSyncMulticastor multicastor, ThreadPool pool)
	{
		this.actor = new AsyncPool.ActorPoolBuilder<MulticastRequest>()
				.messageQueueSize(ClientConfig.ASYNC_EVENT_QUEUE_SIZE)
				.actorSize(ClientConfig.ASYNC_EVENTER_SIZE)
				.poolingWaitTime(ClientConfig.ASYNC_EVENTING_WAIT_TIME)
				.actorWaitTime(ClientConfig.ASYNC_EVENTER_WAIT_TIME)
				.waitRound(ClientConfig.ASYNC_EVENTER_WAIT_ROUND)
				.idleCheckDelay(ClientConfig.ASYNC_EVENT_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ClientConfig.ASYNC_EVENT_IDLE_CHECK_PERIOD)
				.schedulerPoolSize(ClientConfig.SCHEDULER_POOL_SIZE)
				.schedulerKeepAliveTime(ClientConfig.SCHEDULER_KEEP_ALIVE_TIME)
				.actor(new ChildReadActor(multicastor))
				.build();
		
		this.pool = pool;
	}

	public void dispose() throws InterruptedException
	{
		this.actor.dispose();
	}

	public void read(MulticastRequest request)
	{
		if (!this.actor.isReady())
		{
			this.pool.execute(this.actor);
		}
		this.actor.perform(request);
	}
}