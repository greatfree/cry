package org.greatfree.cry.framework.bitcoin.message;

import org.greatfree.message.container.Request;

/**
 * 
 * @author Bing Li
 * 
 * 02/15/2022
 *
 */
public class LeaveCoinSystemRequest extends Request
{
	private static final long serialVersionUID = 9095471512671465372L;
	
	private String peerName;

	public LeaveCoinSystemRequest(String peerName)
	{
		super(CoinAppID.LEAVE_COIN_SYSTEM_REQUEST);
		this.peerName = peerName;
	}

	public String getPeerName()
	{
		return this.peerName;
	}
}
