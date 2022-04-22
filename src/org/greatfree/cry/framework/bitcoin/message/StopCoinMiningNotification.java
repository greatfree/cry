package org.greatfree.cry.framework.bitcoin.message;

import org.greatfree.message.container.Notification;

/**
 * 
 * @author Bing Li
 * 
 * 02/03/2022, Bing Li
 *
 */
public class StopCoinMiningNotification extends Notification
{
	private static final long serialVersionUID = -5446461336522294347L;

	public StopCoinMiningNotification()
	{
		super(CoinAppID.STOP_COIN_MINING_NOTIFICATION);
	}

}
