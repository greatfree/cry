package org.greatfree.cry.framework.bitcoin.message;

import org.greatfree.message.ServerMessage;

/**
 * 
 * @author libing
 * 
 * 02/25/2022
 *
 */
public class PropagateTransactionsResponse extends ServerMessage
{
	private static final long serialVersionUID = 4449708357872658766L;
	
	private boolean isDone;

	public PropagateTransactionsResponse(boolean isDone)
	{
		super(CoinAppID.PROPAGATE_TRANSACTIONS_RESPONSE);
		this.isDone = isDone;
	}

	public boolean isDone()
	{
		return this.isDone;
	}
}
