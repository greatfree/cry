package edu.greatfree.cry.multicast.root;

import java.util.concurrent.atomic.AtomicInteger;

import org.greatfree.concurrency.Notifier;

import edu.greatfree.cry.server.CryptoCSDispatcher;

/**
 * 
 * @author libing
 * 
 * 04/08/2022
 *
 */
// abstract class AsyncMulticastor<Message, Dispatcher extends CryptoCSDispatcher> extends Async<Message>
abstract class AsyncMulticastor<Message, Dispatcher extends CryptoCSDispatcher> implements Notifier<Message>
{
	private RootSyncMulticastor<Dispatcher> multicastor;
	private AtomicInteger cryptoOption;

//	public AsyncMulticastor(RootSyncMulticastor multicastor)
	public AsyncMulticastor(RootSyncMulticastor<Dispatcher> multicastor, int cryptoOption)
	{
		this.multicastor = multicastor;
		this.cryptoOption = new AtomicInteger(cryptoOption);
	}

	public RootSyncMulticastor<Dispatcher> getMulticastor()
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
