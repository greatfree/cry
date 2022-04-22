package org.greatfree.cry.framework.chainless.fullledger.hot.message;

import org.greatfree.message.multicast.MulticastResponse;

/**
 * 
 * @author libing
 * 
 * 04/06/2022
 *
 */
public class DepositResponse extends MulticastResponse
{
	private static final long serialVersionUID = -8543584975877897140L;
	
	private boolean isSucceeded;

	public DepositResponse(String collaboratorKey, boolean isSucceeded)
	{
		super(HotAppID.DEPOSIT_RESPONSE, collaboratorKey);
		this.isSucceeded = isSucceeded;
	}

	public boolean isSucceeded()
	{
		return this.isSucceeded;
	}
}
