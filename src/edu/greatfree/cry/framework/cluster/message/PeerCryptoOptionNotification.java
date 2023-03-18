package edu.greatfree.cry.framework.cluster.message;

import org.greatfree.message.container.Notification;

import edu.greatfree.cry.messege.CryAppID;

/**
 * 
 * @author libing
 * 
 * 02/25/2023
 *
 */
public class PeerCryptoOptionNotification extends Notification
{
	private static final long serialVersionUID = -6861393741628640408L;

	private int cryptoOption;

	public PeerCryptoOptionNotification(int cryptoOption)
	{
		super(CryAppID.PEER_CRYPTO_OPTION_NOTIFICATION);
		this.cryptoOption = cryptoOption;
	}

	public int getCryptoOption()
	{
		return this.cryptoOption;
	}
}
