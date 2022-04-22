package org.greatfree.cry.framework.bitcoin.message;

import org.greatfree.cry.framework.bitcoin.Coin;
import org.greatfree.message.container.Notification;

/**
 * 
 * @author Bing Li
 * 
 * 02/03/2022, Bing Li
 *
 */
public class CoinGeneratedNotification extends Notification
{
	private static final long serialVersionUID = -5826240369702754288L;

	private Coin coin;

	public CoinGeneratedNotification(Coin coin)
	{
		super(CoinAppID.COIN_GENERATED_NOTIFICATION);
		this.coin = coin;
	}

	public Coin getCoin()
	{
		return this.coin;
	}
}
