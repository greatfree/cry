package org.greatfree.cry.framework.bitcoin.message;

import org.greatfree.message.container.Notification;

/**
 * 
 * @author libing
 * 
 * 02/28/2022
 *
 */
public class FinalizeTransactionMiningNotification extends Notification
{
	private static final long serialVersionUID = -2875097329320017039L;
	
	private String sessionKey;

	public FinalizeTransactionMiningNotification(String sessionKey)
	{
		super(CoinAppID.FINALIZE_TRANSACTION_MINING_NOTIFICATION);
		this.sessionKey = sessionKey;
	}

	public String getSessionKey()
	{
		return this.sessionKey;
	}
}
