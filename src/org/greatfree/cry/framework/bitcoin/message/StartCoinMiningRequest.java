package org.greatfree.cry.framework.bitcoin.message;

import org.greatfree.message.container.Request;

/**
 * 
 * @author Bing Li
 * 
 * 02/03/2022, Bing Li
 *
 */
public class StartCoinMiningRequest extends Request
{
	private static final long serialVersionUID = -6505280104324829697L;
	
	public StartCoinMiningRequest()
	{
		super(CoinAppID.START_COIN_MINING_REQUEST);
	}
}
