package org.greatfree.cry.framework.bitcoin.message;

import org.greatfree.message.container.Request;

/**
 * 
 * @author libing
 * 
 * 02/25/2022
 *
 */
public class JoinChainSucceedinglyRequest extends Request
{
	private static final long serialVersionUID = 7018592957151764587L;
	
	private String sessionKey;

	public JoinChainSucceedinglyRequest(String sessionKey)
	{
		super(CoinAppID.JOIN_CHAIN_SUCCEEDINGLY_REQUEST);
		this.sessionKey = sessionKey;
	}

	public String getSessionKey()
	{
		return this.sessionKey;
	}
}
