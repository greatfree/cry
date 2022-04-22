package org.greatfree.cry.framework.bitcoin.message;

import org.greatfree.cry.framework.bitcoin.CoinBlockInfo;
import org.greatfree.message.container.Request;

/**
 * 
 * @author Bing Li
 * 
 * 02/15/2022
 *
 */
public class JoinCoinChainRequest extends Request
{
	private static final long serialVersionUID = -5243922925242569493L;
	
	private CoinBlockInfo bi;

	public JoinCoinChainRequest(CoinBlockInfo bi)
	{
		super(CoinAppID.JOIN_COIN_CHAIN_REQUEST);
		this.bi = bi;
	}

	public CoinBlockInfo getBlockInfo()
	{
		return this.bi;
	}
}
