package org.greatfree.cry.framework.bitcoin.message;

import org.greatfree.cry.framework.bitcoin.CoinBlockInfo;
import org.greatfree.cry.framework.blockchain.BlockInfo;
import org.greatfree.message.ServerMessage;

/**
 * 
 * @author Bing Li
 * 
 * 02/15/2022
 *
 */
public class JoinCoinChainResponse extends ServerMessage
{
	private static final long serialVersionUID = -1012567376483727357L;
	
//	private boolean isHead;

	private int sequenceNO;
	private String headPeerName;
	private CoinBlockInfo precedingBI;

//	public JoinCoinSystemResponse(boolean isHead)
	public JoinCoinChainResponse(int sequenceNO, String headPeerName, CoinBlockInfo bi)
	{
		super(CoinAppID.JOIN_COIN_CHAIN_RESPONSE);
//		this.isHead = isHead;
		this.sequenceNO = sequenceNO;
		this.headPeerName = headPeerName;
		this.precedingBI = bi;
	}

	/*
	public boolean isHead()
	{
		return this.isHead;
	}
	*/
	
	public int getSequenceNO()
	{
		return this.sequenceNO;
	}
	
	public String getHeadPeerName()
	{
		return this.headPeerName;
	}

	public BlockInfo getPrecedingBlockInfo()
	{
		return this.precedingBI;
	}
}
