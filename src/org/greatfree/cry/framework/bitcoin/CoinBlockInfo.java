package org.greatfree.cry.framework.bitcoin;

import org.greatfree.cry.framework.blockchain.BlockInfo;

/**
 * 
 * @author Bing Li
 * 
 * 02/15/2022
 *
 */
public final class CoinBlockInfo extends BlockInfo
{
	private static final long serialVersionUID = 309881647958321713L;
	
	private String sessionKey;
//	private Date timeStamp;

//	public CoinBlockInfo(String sessionKey, String nodeName, String fingerPrint, Date timeStamp)
	public CoinBlockInfo(String sessionKey, String nodeName, String fingerPrint)
	{
		super(nodeName, fingerPrint);
		this.sessionKey = sessionKey;
//		this.timeStamp = timeStamp;
	}

	public String getSessionKey()
	{
		return this.sessionKey;
	}

	/*
	public Date getTimeStamp()
	{
		return this.timeStamp;
	}
	*/
}
