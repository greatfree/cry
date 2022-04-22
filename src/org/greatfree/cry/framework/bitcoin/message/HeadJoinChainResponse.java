package org.greatfree.cry.framework.bitcoin.message;

import org.greatfree.message.ServerMessage;

/**
 * 
 * @author libing
 * 
 * 02/25/2022
 *
 */
public class HeadJoinChainResponse extends ServerMessage
{
	private static final long serialVersionUID = 3068702536806611334L;
	
	private boolean isDone;

	public HeadJoinChainResponse(boolean isDone)
	{
		super(CoinAppID.HEAD_JOIN_CHAIN_RESPONSE);
		this.isDone = isDone;
	}

	public boolean isDone()
	{
		return this.isDone;
	}
}
