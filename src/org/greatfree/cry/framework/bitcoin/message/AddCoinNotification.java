package org.greatfree.cry.framework.bitcoin.message;

import org.greatfree.cry.framework.bitcoin.Coin;
import org.greatfree.cry.framework.bitcoin.Transaction;
import org.greatfree.message.container.Notification;

/**
 * 
 * @author Bing Li
 * 
 * 02/15/2022
 *
 */
public class AddCoinNotification extends Notification
{
	private static final long serialVersionUID = -6227122468463380089L;
	
	private Coin coin;
	private Transaction t;

	public AddCoinNotification(Coin coin, Transaction t)
	{
		super(CoinAppID.ADD_COIN_NOTIFICATION);
		this.coin = coin;
		this.t = t;
	}

	public Coin getCoin()
	{
		return this.coin;
	}
	
	public Transaction getTransaction()
	{
		return this.t;
	}
}
