package edu.greatfree.cry.messege.cluster;

import org.greatfree.message.multicast.MulticastMessageType;

import edu.greatfree.cry.messege.CryAppID;
import edu.greatfree.cry.messege.multicast.ClusterNotification;

/**
 * 
 * @author libing
 * 
 * 05/14/2022
 *
 */
public class ClusterCryptoOptionNotification extends ClusterNotification
{
	private static final long serialVersionUID = -2608933751788146583L;
	
	private int cryptoOption;

	public ClusterCryptoOptionNotification(int cryptoOption)
	{
		super(MulticastMessageType.BROADCAST_NOTIFICATION, CryAppID.CLUSTER_CRYPTO_OPTION_NOTIFICATION);
		this.cryptoOption = cryptoOption;
	}

	public int getCryptoOption()
	{
		return this.cryptoOption;
	}
}
