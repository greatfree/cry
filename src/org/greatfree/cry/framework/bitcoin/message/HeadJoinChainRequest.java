package org.greatfree.cry.framework.bitcoin.message;

import org.greatfree.message.container.Request;

/**
 * 
 * The request is sent from the head of the networking to the peer to request them to join the block chain. It is different from the one, JoinCoinChainRequest, which is sent from one peer to the coordinator. 02/25/2022, Bing Li
 * 
 * @author libing
 * 
 * 02/25/2022
 *
 */
public class HeadJoinChainRequest extends Request
{
	private static final long serialVersionUID = 7510743874778128388L;
	
	private String sessionKey;

	public HeadJoinChainRequest(String sessionKey)
	{
		super(CoinAppID.HEAD_JOIN_CHAIN_REQUEST);
		this.sessionKey = sessionKey;
	}

	public String getSessionKey()
	{
		return this.sessionKey;
	}
}
