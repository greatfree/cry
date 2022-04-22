package org.greatfree.cry.framework.bitcoin.message;

import org.greatfree.message.container.Request;

/**
 * 
 * @author Bing Li
 * 
 * 02/15/2022
 *
 */
public class JoinCoinSystemRequest extends Request
{
	private static final long serialVersionUID = -5243922925242569493L;

	private String peerName;
	
	public JoinCoinSystemRequest(String peerName)
	{
		super(CoinAppID.JOIN_COIN_SYSTEM_REQUEST);
		this.peerName = peerName;
	}

	public String getPeerName()
	{
		return this.peerName;
	}
}
