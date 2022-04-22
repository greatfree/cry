package org.greatfree.cry.framework.bitcoin.message;

import org.greatfree.message.ServerMessage;

/**
 * 
 * @author libing
 * 
 * 02/25/2022
 *
 */
public class JoinChainSucceedinglyResponse extends ServerMessage
{
	private static final long serialVersionUID = -45616290025150993L;
	
	private boolean isDone;

	public JoinChainSucceedinglyResponse(boolean isDone)
	{
		super(CoinAppID.JOIN_CHAIN_SUCCEEDINGLY_RESPONSE);
		this.isDone = isDone;
	}

	public boolean isDone()
	{
		return this.isDone;
	}
}
