package org.greatfree.cry.framework.blockchain.message;

import org.greatfree.cry.framework.blockchain.BlockInfo;
import org.greatfree.message.ServerMessage;

/**
 * 
 * @author libing
 * 
 * 01/26/2022, Bing Li
 *
 */
public class JoinChainResponse extends ServerMessage
{
	private static final long serialVersionUID = 1074809130529970002L;

	private int sequenceNO;
	private String headPeerName;
	private BlockInfo precedingBI;
//	private String previousFingerPrint;

//	public JoinChainResponse(String previousFingerPrint)
	public JoinChainResponse(int sequenceNO, String headPeerName, BlockInfo bi)
	{
		super(ChainAppID.JOIN_CHAIN_RESPONSE);
		this.sequenceNO = sequenceNO;
		this.headPeerName = headPeerName;
		this.precedingBI = bi;
//		this.previousFingerPrint = previousFingerPrint;
	}
	
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

	/*
	public String getPreviousFingerPrint()
	{
		return this.previousFingerPrint;
	}
	*/
}
