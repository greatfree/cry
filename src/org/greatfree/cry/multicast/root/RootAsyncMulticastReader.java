package org.greatfree.cry.multicast.root;

import org.greatfree.concurrency.AsyncPool;
import org.greatfree.concurrency.ThreadPool;
import org.greatfree.cry.server.CryptoCSDispatcher;
import org.greatfree.data.ClientConfig;
import org.greatfree.message.multicast.MulticastRequest;
import org.greatfree.multicast.root.ChildKeyMulticastRequest;
import org.greatfree.multicast.root.ChildrenMulticastRequest;
import org.greatfree.multicast.root.ChildrenSizeMulticastRequest;
import org.greatfree.multicast.root.NearestKeyMulticastRequest;
import org.greatfree.multicast.root.NearestKeysMulticastRequest;
import org.greatfree.multicast.root.RandomChildrenMulticastRequest;
import org.greatfree.multicast.root.SizeMulticastRequest;

/**
 * 
 * @author libing
 * 
 * 04/08/2022
 *
 */
final class RootAsyncMulticastReader<Dispatcher extends CryptoCSDispatcher>
{
	private AsyncPool<MulticastRequest> actor;
	private AsyncPool<SizeMulticastRequest> sizeActor;
	private AsyncPool<ChildrenMulticastRequest> childrenActor;
	private AsyncPool<ChildrenSizeMulticastRequest> childrenSizeActor;
	
	private AsyncPool<NearestKeysMulticastRequest> nearestKeysActor;
	private AsyncPool<NearestKeyMulticastRequest> nearestKeyActor;
	private AsyncPool<MulticastRequest> randomActor;
	private AsyncPool<ChildKeyMulticastRequest> childKeyActor;
	
	private AsyncPool<RandomChildrenMulticastRequest> randomChildrenActor;
	
	private ThreadPool pool;
	
//	public RootAsyncMulticastReader(RootSyncMulticastor multicastor, int cryptoOption, ThreadPool pool)
	public RootAsyncMulticastReader(RootSyncMulticastor<Dispatcher> multicastor, ThreadPool pool)
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
				.actor(new RootReadActor<Dispatcher>(multicastor, multicastor.getCryptoOption()))
//				.actor(new RootReadActor(multicastor))
				.build();

		this.sizeActor = new AsyncPool.ActorPoolBuilder<SizeMulticastRequest>()
				.messageQueueSize(ClientConfig.ASYNC_EVENT_QUEUE_SIZE)
				.actorSize(ClientConfig.ASYNC_EVENTER_SIZE)
				.poolingWaitTime(ClientConfig.ASYNC_EVENTING_WAIT_TIME)
				.actorWaitTime(ClientConfig.ASYNC_EVENTER_WAIT_TIME)
				.waitRound(ClientConfig.ASYNC_EVENTER_WAIT_ROUND)
				.idleCheckDelay(ClientConfig.ASYNC_EVENT_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ClientConfig.ASYNC_EVENT_IDLE_CHECK_PERIOD)
				.schedulerPoolSize(ClientConfig.SCHEDULER_POOL_SIZE)
				.schedulerKeepAliveTime(ClientConfig.SCHEDULER_KEEP_ALIVE_TIME)
				.actor(new SizeRootReadActor<Dispatcher>(multicastor, multicastor.getCryptoOption()))
//				.actor(new SizeRootReadActor(multicastor))
				.build();

		this.childrenActor = new AsyncPool.ActorPoolBuilder<ChildrenMulticastRequest>()
				.messageQueueSize(ClientConfig.ASYNC_EVENT_QUEUE_SIZE)
				.actorSize(ClientConfig.ASYNC_EVENTER_SIZE)
				.poolingWaitTime(ClientConfig.ASYNC_EVENTING_WAIT_TIME)
				.actorWaitTime(ClientConfig.ASYNC_EVENTER_WAIT_TIME)
				.waitRound(ClientConfig.ASYNC_EVENTER_WAIT_ROUND)
				.idleCheckDelay(ClientConfig.ASYNC_EVENT_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ClientConfig.ASYNC_EVENT_IDLE_CHECK_PERIOD)
				.schedulerPoolSize(ClientConfig.SCHEDULER_POOL_SIZE)
				.schedulerKeepAliveTime(ClientConfig.SCHEDULER_KEEP_ALIVE_TIME)
				.actor(new ChildrenRootReadActor<Dispatcher>(multicastor, multicastor.getCryptoOption()))
//				.actor(new ChildrenRootReadActor(multicastor))
				.build();

		this.childrenSizeActor = new AsyncPool.ActorPoolBuilder<ChildrenSizeMulticastRequest>()
				.messageQueueSize(ClientConfig.ASYNC_EVENT_QUEUE_SIZE)
				.actorSize(ClientConfig.ASYNC_EVENTER_SIZE)
				.poolingWaitTime(ClientConfig.ASYNC_EVENTING_WAIT_TIME)
				.actorWaitTime(ClientConfig.ASYNC_EVENTER_WAIT_TIME)
				.waitRound(ClientConfig.ASYNC_EVENTER_WAIT_ROUND)
				.idleCheckDelay(ClientConfig.ASYNC_EVENT_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ClientConfig.ASYNC_EVENT_IDLE_CHECK_PERIOD)
				.schedulerPoolSize(ClientConfig.SCHEDULER_POOL_SIZE)
				.schedulerKeepAliveTime(ClientConfig.SCHEDULER_KEEP_ALIVE_TIME)
				.actor(new ChildrenSizeRootReadActor<Dispatcher>(multicastor, multicastor.getCryptoOption()))
//				.actor(new ChildrenSizeRootReadActor(multicastor))
				.build();

		this.nearestKeysActor = new AsyncPool.ActorPoolBuilder<NearestKeysMulticastRequest>()
				.messageQueueSize(ClientConfig.ASYNC_EVENT_QUEUE_SIZE)
				.actorSize(ClientConfig.ASYNC_EVENTER_SIZE)
				.poolingWaitTime(ClientConfig.ASYNC_EVENTING_WAIT_TIME)
				.actorWaitTime(ClientConfig.ASYNC_EVENTER_WAIT_TIME)
				.waitRound(ClientConfig.ASYNC_EVENTER_WAIT_ROUND)
				.idleCheckDelay(ClientConfig.ASYNC_EVENT_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ClientConfig.ASYNC_EVENT_IDLE_CHECK_PERIOD)
				.schedulerPoolSize(ClientConfig.SCHEDULER_POOL_SIZE)
				.schedulerKeepAliveTime(ClientConfig.SCHEDULER_KEEP_ALIVE_TIME)
				.actor(new NearestKeysRootReadActor<Dispatcher>(multicastor, multicastor.getCryptoOption()))
//				.actor(new NearestKeysRootReadActor(multicastor))
				.build();

		this.nearestKeyActor = new AsyncPool.ActorPoolBuilder<NearestKeyMulticastRequest>()
				.messageQueueSize(ClientConfig.ASYNC_EVENT_QUEUE_SIZE)
				.actorSize(ClientConfig.ASYNC_EVENTER_SIZE)
				.poolingWaitTime(ClientConfig.ASYNC_EVENTING_WAIT_TIME)
				.actorWaitTime(ClientConfig.ASYNC_EVENTER_WAIT_TIME)
				.waitRound(ClientConfig.ASYNC_EVENTER_WAIT_ROUND)
				.idleCheckDelay(ClientConfig.ASYNC_EVENT_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ClientConfig.ASYNC_EVENT_IDLE_CHECK_PERIOD)
				.schedulerPoolSize(ClientConfig.SCHEDULER_POOL_SIZE)
				.schedulerKeepAliveTime(ClientConfig.SCHEDULER_KEEP_ALIVE_TIME)
				.actor(new NearestKeyRootReadActor<Dispatcher>(multicastor, multicastor.getCryptoOption()))
//				.actor(new NearestKeyRootReadActor(multicastor))
				.build();

		this.randomActor = new AsyncPool.ActorPoolBuilder<MulticastRequest>()
				.messageQueueSize(ClientConfig.ASYNC_EVENT_QUEUE_SIZE)
				.actorSize(ClientConfig.ASYNC_EVENTER_SIZE)
				.poolingWaitTime(ClientConfig.ASYNC_EVENTING_WAIT_TIME)
				.actorWaitTime(ClientConfig.ASYNC_EVENTER_WAIT_TIME)
				.waitRound(ClientConfig.ASYNC_EVENTER_WAIT_ROUND)
				.idleCheckDelay(ClientConfig.ASYNC_EVENT_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ClientConfig.ASYNC_EVENT_IDLE_CHECK_PERIOD)
				.schedulerPoolSize(ClientConfig.SCHEDULER_POOL_SIZE)
				.schedulerKeepAliveTime(ClientConfig.SCHEDULER_KEEP_ALIVE_TIME)
				.actor(new RandomRootReadActor<Dispatcher>(multicastor, multicastor.getCryptoOption()))
//				.actor(new RandomRootReadActor(multicastor))
				.build();

		this.childKeyActor = new AsyncPool.ActorPoolBuilder<ChildKeyMulticastRequest>()
				.messageQueueSize(ClientConfig.ASYNC_EVENT_QUEUE_SIZE)
				.actorSize(ClientConfig.ASYNC_EVENTER_SIZE)
				.poolingWaitTime(ClientConfig.ASYNC_EVENTING_WAIT_TIME)
				.actorWaitTime(ClientConfig.ASYNC_EVENTER_WAIT_TIME)
				.waitRound(ClientConfig.ASYNC_EVENTER_WAIT_ROUND)
				.idleCheckDelay(ClientConfig.ASYNC_EVENT_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ClientConfig.ASYNC_EVENT_IDLE_CHECK_PERIOD)
				.schedulerPoolSize(ClientConfig.SCHEDULER_POOL_SIZE)
				.schedulerKeepAliveTime(ClientConfig.SCHEDULER_KEEP_ALIVE_TIME)
				.actor(new ChildKeyRootReadActor<Dispatcher>(multicastor, multicastor.getCryptoOption()))
//				.actor(new ChildKeyRootReadActor(multicastor))
				.build();

		this.randomChildrenActor = new AsyncPool.ActorPoolBuilder<RandomChildrenMulticastRequest>()
				.messageQueueSize(ClientConfig.ASYNC_EVENT_QUEUE_SIZE)
				.actorSize(ClientConfig.ASYNC_EVENTER_SIZE)
				.poolingWaitTime(ClientConfig.ASYNC_EVENTING_WAIT_TIME)
				.actorWaitTime(ClientConfig.ASYNC_EVENTER_WAIT_TIME)
				.waitRound(ClientConfig.ASYNC_EVENTER_WAIT_ROUND)
				.idleCheckDelay(ClientConfig.ASYNC_EVENT_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ClientConfig.ASYNC_EVENT_IDLE_CHECK_PERIOD)
				.schedulerPoolSize(ClientConfig.SCHEDULER_POOL_SIZE)
				.schedulerKeepAliveTime(ClientConfig.SCHEDULER_KEEP_ALIVE_TIME)
				.actor(new RandomChildrenRootReadActor<Dispatcher>(multicastor, multicastor.getCryptoOption()))
//				.actor(new RandomChildrenRootReadActor(multicastor))
				.build();

		this.pool = pool;
	}
	
	public void dispose() throws InterruptedException
	{
		this.actor.dispose();
		this.sizeActor.dispose();
		this.childrenActor.dispose();
		this.childrenSizeActor.dispose();
		this.nearestKeysActor.dispose();
		this.nearestKeyActor.dispose();
		this.randomActor.dispose();
		this.childKeyActor.dispose();
		this.randomChildrenActor.dispose();
	}

	public void asyncRead(MulticastRequest request)
	{
		if (!this.actor.isReady())
		{
			this.pool.execute(this.actor);
		}
		this.actor.perform(request);
	}
	
	public void asyncRead(SizeMulticastRequest request)
	{
		if (!this.sizeActor.isReady())
		{
			this.pool.execute(this.sizeActor);
		}
		this.sizeActor.perform(request);
	}
	
	public void asyncRead(ChildrenMulticastRequest request)
	{
		if (!this.childrenActor.isReady())
		{
			this.pool.execute(this.childrenActor);
		}
		this.childrenActor.perform(request);
	}

	public void asyncRead(ChildrenSizeMulticastRequest request)
	{
		if (!this.childrenSizeActor.isReady())
		{
			this.pool.execute(this.childrenSizeActor);
		}
		this.childrenSizeActor.perform(request);
	}
	
	public void asyncRead(NearestKeysMulticastRequest request)
	{
		if (!this.nearestKeysActor.isReady())
		{
			this.pool.execute(this.nearestKeysActor);
		}
		this.nearestKeysActor.perform(request);
	}
	
	public void asyncRead(NearestKeyMulticastRequest request)
	{
		if (!this.nearestKeyActor.isReady())
		{
			this.pool.execute(this.nearestKeyActor);
		}
		this.nearestKeyActor.perform(request);
	}
	
	public void asyncRandomRead(MulticastRequest request)
	{
		if (!this.randomActor.isReady())
		{
			this.pool.execute(this.randomActor);
		}
		this.randomActor.perform(request);
	}
	
	public void asyncRead(ChildKeyMulticastRequest request)
	{
		if (!this.childKeyActor.isReady())
		{
			this.pool.execute(this.childKeyActor);
		}
		this.childKeyActor.perform(request);
	}
	
	public void asynRead(RandomChildrenMulticastRequest request)
	{
		if (!this.randomChildrenActor.isReady())
		{
			this.pool.execute(this.randomChildrenActor);
		}
		this.randomChildrenActor.perform(request);
	}

}
