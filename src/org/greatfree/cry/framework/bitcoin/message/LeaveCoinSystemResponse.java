package org.greatfree.cry.framework.bitcoin.message;

import org.greatfree.message.ServerMessage;

/**
 * 
 * @author Bing Li
 * 
 * 02/15/2022
 *
 */
public class LeaveCoinSystemResponse extends ServerMessage
{
	private static final long serialVersionUID = -1263071988986092197L;
	
	private boolean isDone;

	public LeaveCoinSystemResponse(boolean isDone)
	{
		super(CoinAppID.LEAVE_COIN_SYSTEM_RESPONSE);
		this.isDone = isDone;
	}

	public boolean isDone()
	{
		return this.isDone;
	}
}
