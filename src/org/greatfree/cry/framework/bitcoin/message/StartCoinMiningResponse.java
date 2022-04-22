package org.greatfree.cry.framework.bitcoin.message;

import org.greatfree.message.ServerMessage;

/**
 * 
 * @author Bing Li
 * 
 * 02/06/2022
 *
 */
public class StartCoinMiningResponse extends ServerMessage
{
	private static final long serialVersionUID = 2166866087998506587L;

	private boolean isStarted;
	
	public StartCoinMiningResponse(boolean isStarted)
	{
		super(CoinAppID.START_COIN_MINING_RESPONSE);
		this.isStarted = isStarted;
	}

	public boolean isStarted()
	{
		return this.isStarted;
	}
}
