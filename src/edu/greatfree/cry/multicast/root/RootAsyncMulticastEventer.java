package edu.greatfree.cry.multicast.root;

import java.util.Set;

import org.greatfree.concurrency.NotifierPool;
import org.greatfree.concurrency.ThreadPool;
import org.greatfree.data.ClientConfig;
import org.greatfree.multicast.root.ChildMulticastNotification;
import org.greatfree.multicast.root.ChildrenMulticastNotification;
import org.greatfree.multicast.root.NearestMulticastNotification;
import org.greatfree.multicast.root.RandomChildrenMulticastNotification;

import edu.greatfree.cry.messege.multicast.PrimitiveMulticastNotification;
import edu.greatfree.cry.server.CryptoCSDispatcher;

/**
 * 
 * @author libing
 * 
 * 04/08/2022
 *
 */
final class RootAsyncMulticastEventer<Dispatcher extends CryptoCSDispatcher>
{
//	private AsyncPool<PrimitiveMulticastNotification> actor;
	private NotifierPool<PrimitiveMulticastNotification> actor;
	private NotifierPool<ChildrenMulticastNotification> childrenActor;
	private NotifierPool<ChildMulticastNotification> childActor;
	private NotifierPool<NearestMulticastNotification> nearestActor;
	private NotifierPool<PrimitiveMulticastNotification> randomActor;
	private NotifierPool<RandomChildrenMulticastNotification> randomChildrenActor;
	private ThreadPool pool;

//	public RootAsyncMulticastEventer(RootSyncMulticastor multicastor, int cryptoOption, ThreadPool pool)
	public RootAsyncMulticastEventer(RootSyncMulticastor<Dispatcher> multicastor, ThreadPool pool)
	{
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
				.notifier(new RootEventActor<Dispatcher>(multicastor, multicastor.getCryptoOption()))
//				.actor(new RootEventActor(multicastor))
				.build();

		this.childrenActor = new NotifierPool.NotifierPoolBuilder<ChildrenMulticastNotification>()
				.queueSize(ClientConfig.ASYNC_EVENT_QUEUE_SIZE)
				.notifierSize(ClientConfig.ASYNC_EVENTER_SIZE)
				.poolingWaitTime(ClientConfig.ASYNC_EVENTING_WAIT_TIME)
				.notifierWaitTime(ClientConfig.ASYNC_EVENTER_WAIT_TIME)
//				.waitRound(ClientConfig.ASYNC_EVENTER_WAIT_ROUND)
				.idleCheckDelay(ClientConfig.ASYNC_EVENT_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ClientConfig.ASYNC_EVENT_IDLE_CHECK_PERIOD)
				.schedulerPoolSize(ClientConfig.SCHEDULER_POOL_SIZE)
				.schedulerKeepAliveTime(ClientConfig.SCHEDULER_KEEP_ALIVE_TIME)
				.notifier(new ChildrenRootEventActor<Dispatcher>(multicastor, multicastor.getCryptoOption()))
//				.actor(new ChildrenRootEventActor(multicastor))
				.build();
		
		this.childActor = new NotifierPool.NotifierPoolBuilder<ChildMulticastNotification>()
				.queueSize(ClientConfig.ASYNC_EVENT_QUEUE_SIZE)
				.notifierSize(ClientConfig.ASYNC_EVENTER_SIZE)
				.poolingWaitTime(ClientConfig.ASYNC_EVENTING_WAIT_TIME)
				.notifierWaitTime(ClientConfig.ASYNC_EVENTER_WAIT_TIME)
//				.waitRound(ClientConfig.ASYNC_EVENTER_WAIT_ROUND)
				.idleCheckDelay(ClientConfig.ASYNC_EVENT_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ClientConfig.ASYNC_EVENT_IDLE_CHECK_PERIOD)
				.schedulerPoolSize(ClientConfig.SCHEDULER_POOL_SIZE)
				.schedulerKeepAliveTime(ClientConfig.SCHEDULER_KEEP_ALIVE_TIME)
				.notifier(new ChildRootEventActor<Dispatcher>(multicastor, multicastor.getCryptoOption()))
//				.actor(new ChildRootEventActor(multicastor))
				.build();
		
		this.nearestActor = new NotifierPool.NotifierPoolBuilder<NearestMulticastNotification>()
				.queueSize(ClientConfig.ASYNC_EVENT_QUEUE_SIZE)
				.notifierSize(ClientConfig.ASYNC_EVENTER_SIZE)
				.poolingWaitTime(ClientConfig.ASYNC_EVENTING_WAIT_TIME)
				.notifierWaitTime(ClientConfig.ASYNC_EVENTER_WAIT_TIME)
//				.waitRound(ClientConfig.ASYNC_EVENTER_WAIT_ROUND)
				.idleCheckDelay(ClientConfig.ASYNC_EVENT_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ClientConfig.ASYNC_EVENT_IDLE_CHECK_PERIOD)
				.schedulerPoolSize(ClientConfig.SCHEDULER_POOL_SIZE)
				.schedulerKeepAliveTime(ClientConfig.SCHEDULER_KEEP_ALIVE_TIME)
				.notifier(new NearestRootEventActor<Dispatcher>(multicastor, multicastor.getCryptoOption()))
//				.actor(new NearestRootEventActor(multicastor))
				.build();
		
		this.randomActor = new NotifierPool.NotifierPoolBuilder<PrimitiveMulticastNotification>()
				.queueSize(ClientConfig.ASYNC_EVENT_QUEUE_SIZE)
				.notifierSize(ClientConfig.ASYNC_EVENTER_SIZE)
				.poolingWaitTime(ClientConfig.ASYNC_EVENTING_WAIT_TIME)
				.notifierWaitTime(ClientConfig.ASYNC_EVENTER_WAIT_TIME)
//				.waitRound(ClientConfig.ASYNC_EVENTER_WAIT_ROUND)
				.idleCheckDelay(ClientConfig.ASYNC_EVENT_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ClientConfig.ASYNC_EVENT_IDLE_CHECK_PERIOD)
				.schedulerPoolSize(ClientConfig.SCHEDULER_POOL_SIZE)
				.schedulerKeepAliveTime(ClientConfig.SCHEDULER_KEEP_ALIVE_TIME)
				.notifier(new RandomRootEventActor<Dispatcher>(multicastor, multicastor.getCryptoOption()))
//				.actor(new RandomRootEventActor(multicastor))
				.build();

		this.randomChildrenActor = new NotifierPool.NotifierPoolBuilder<RandomChildrenMulticastNotification>()
				.queueSize(ClientConfig.ASYNC_EVENT_QUEUE_SIZE)
				.notifierSize(ClientConfig.ASYNC_EVENTER_SIZE)
				.poolingWaitTime(ClientConfig.ASYNC_EVENTING_WAIT_TIME)
				.notifierWaitTime(ClientConfig.ASYNC_EVENTER_WAIT_TIME)
//				.waitRound(ClientConfig.ASYNC_EVENTER_WAIT_ROUND)
				.idleCheckDelay(ClientConfig.ASYNC_EVENT_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ClientConfig.ASYNC_EVENT_IDLE_CHECK_PERIOD)
				.schedulerPoolSize(ClientConfig.SCHEDULER_POOL_SIZE)
				.schedulerKeepAliveTime(ClientConfig.SCHEDULER_KEEP_ALIVE_TIME)
				.notifier(new RandomChildrenRootEventActor<Dispatcher>(multicastor, multicastor.getCryptoOption()))
//				.actor(new RandomChildrenRootEventActor(multicastor))
				.build();
		
		this.pool = pool;
	}
	
	public void dispose() throws InterruptedException
	{
		this.actor.dispose();
		this.childrenActor.dispose();
		this.childActor.dispose();
		this.nearestActor.dispose();
		this.randomActor.dispose();
		this.randomChildrenActor.dispose();
	}
	
	public void asyncNotify(PrimitiveMulticastNotification notification)
	{
		if (!this.actor.isReady())
		{
			this.pool.execute(this.actor);
		}
		this.actor.notify(notification);
	}
	
	public void asyncNotify(PrimitiveMulticastNotification notification, Set<String> childrenKeys)
	{
		if (!this.childrenActor.isReady())
		{
			this.pool.execute(this.childrenActor);
		}
		this.childrenActor.notify(new ChildrenMulticastNotification(notification, childrenKeys));
	}
	
	public void asyncNotify(PrimitiveMulticastNotification msg, String childKey)
	{
		if (!this.childActor.isReady())
		{
			this.pool.execute(this.childActor);
		}
		this.childActor.notify(new ChildMulticastNotification(msg, childKey));
	}
	
	public void asyncNearestNotify(String key, PrimitiveMulticastNotification msg)
	{
		if (!this.nearestActor.isReady())
		{
			this.pool.execute(this.nearestActor);
		}
		this.nearestActor.notify(new NearestMulticastNotification(key, msg));
	}

	public void asyncRandomNotify(PrimitiveMulticastNotification msg)
	{
		if (!this.randomActor.isReady())
		{
			this.pool.execute(this.randomActor);
		}
		this.randomActor.notify(msg);
	}
	
	public void asyncNotifyWithinNChildren(PrimitiveMulticastNotification notification, int childrenSize)
	{
		if (!this.randomChildrenActor.isReady())
		{
			this.pool.execute(this.randomChildrenActor);
		}
		this.randomChildrenActor.notify(new RandomChildrenMulticastNotification(childrenSize, notification));
	}
}
