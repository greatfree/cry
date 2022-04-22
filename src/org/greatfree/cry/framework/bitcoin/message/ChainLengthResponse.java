package org.greatfree.cry.framework.bitcoin.message;

import org.greatfree.message.ServerMessage;

/**
 * 
 * @author libing
 * 
 * 02/26/2022
 *
 */
public class ChainLengthResponse extends ServerMessage
{
	private static final long serialVersionUID = -3446038676346502484L;
	
	private int chainLength;

	public ChainLengthResponse(int chainLength)
	{
		super(CoinAppID.CHAIN_LENGTH_RESPONSE);
		this.chainLength = chainLength;
	}

	public int getChainLength()
	{
		return this.chainLength;
	}
}
