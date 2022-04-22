package org.greatfree.cry.framework.bitcoin.message;

import org.greatfree.message.ServerMessage;

/**
 * 
 * @author libing
 * 
 * 02/25/2022
 *
 */
public class PropagateSucceedinglyResponse extends ServerMessage
{
	private static final long serialVersionUID = -5272665297732099398L;
	
	private boolean isDone;

	public PropagateSucceedinglyResponse(boolean isDone)
	{
		super(CoinAppID.PROPAGATE_SUCCEEDINGLY_RESPONSE);
		this.isDone = isDone;
	}

	public boolean isDone()
	{
		return this.isDone;
	}
}
