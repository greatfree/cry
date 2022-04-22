package org.greatfree.cry.framework.bitcoin.peer;

import java.util.concurrent.ScheduledFuture;

/**
 * 
 * @author Bing Li
 * 
 * 02/03/2022, Bing Li
 * 
 * Each peer is owned by a client who expects to join the block chain to mine coins and get transaction fees. The peer needs to be owned by a client before join the chain. That is critical to ensure the peer is owned by the correct client.
 * 
 * Each peer accepts only one invitation, which is originated from its owner only, before the peer joins the chain. 02/03/2022, Bing Li
 *
 */
class MiningTaskManager
{
//	private String owner;
//	private AtomicBoolean isCoinMiningStarted;
	private boolean isCoinMiningStarted;
	private ScheduledFuture<?> coinMiningTask;
	private ScheduledFuture<?> transactionTask;
	
	private MiningTaskManager()
	{
		this.isCoinMiningStarted = false;
//		this.owner = UtilConfig.EMPTY_STRING;
		this.coinMiningTask = null;
		this.transactionTask = null;
	}
	
	private static MiningTaskManager instance = new MiningTaskManager();
	
	public static MiningTaskManager COIN()
	{
		if (instance == null)
		{
			instance = new MiningTaskManager();
			return instance;
		}
		else
		{
			return instance;
		}
	}

	/*
	public synchronized boolean setOwner(String owner)
	{
		if (!this.owner.equals(UtilConfig.EMPTY_STRING))
		{
			this.owner = owner;
			return true;
		}
		return false;
	}

	public synchronized String getOwner()
	{
		return this.owner;
	}
	
	public synchronized boolean isOwnerSet()
	{
		return this.owner.equals(UtilConfig.EMPTY_STRING) ? false : true;
	}
	*/
	
	public synchronized void setCoinMining(boolean isStarted)
	{
//		this.isCoinMiningStarted.set(isStarted);
		this.isCoinMiningStarted = isStarted;
	}
	
	public synchronized boolean isCoinMiningStarted()
	{
		if (this.coinMiningTask != null)
		{
//			return this.isCoinMiningStarted.get();
			return this.isCoinMiningStarted;
		}
		return false;
	}
	
	public synchronized void setCoinMiningTask(ScheduledFuture<?> task)
	{
		if (this.coinMiningTask == null)
		{
			this.coinMiningTask = task;
		}
	}
	
	public void setTransactionTask(ScheduledFuture<?> task)
	{
		this.transactionTask = task;
	}
	
	public synchronized void stopCoinMiningTask()
	{
		this.coinMiningTask.cancel(true);
		this.coinMiningTask = null;
	}
	
	public void stopTransactionTask()
	{
		this.transactionTask.cancel(true);
	}
}
