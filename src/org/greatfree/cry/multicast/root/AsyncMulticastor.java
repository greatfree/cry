package org.greatfree.cry.multicast.root;

import java.util.concurrent.atomic.AtomicInteger;

import org.greatfree.concurrency.Async;

/**
 * 
 * @author libing
 * 
 * 04/08/2022
 *
 */
abstract class AsyncMulticastor<Message> extends Async<Message>
{
	private RootSyncMulticastor multicastor;
	private AtomicInteger cryptoOption;

//	public AsyncMulticastor(RootSyncMulticastor multicastor)
	public AsyncMulticastor(RootSyncMulticastor multicastor, int cryptoOption)
	{
		this.multicastor = multicastor;
		this.cryptoOption = new AtomicInteger(cryptoOption);
	}

	public RootSyncMulticastor getMulticastor()
	{
		return this.multicastor;
	}

	public int getCryptoOption()
	{
		return this.cryptoOption.get();
	}
	
	public void setCryptoOption(int co)
	{
		this.cryptoOption.set(co);
	}
}
