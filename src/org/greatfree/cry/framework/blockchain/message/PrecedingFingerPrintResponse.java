package org.greatfree.cry.framework.blockchain.message;

import org.greatfree.message.ServerMessage;

/**
 * 
 * @author libing
 * 
 * 01/28/2022, Bing Li
 *
 */
public class PrecedingFingerPrintResponse extends ServerMessage
{
	private static final long serialVersionUID = -2075073969845050618L;
	
	private String precedingFingerPrint;

	public PrecedingFingerPrintResponse(String precedingFingerPrint)
	{
		super(ChainAppID.PRECEDING_FINGER_PRINT_RESPONSE);
		this.precedingFingerPrint = precedingFingerPrint;
	}

	public String getPrecedingFingerPrint()
	{
		return this.precedingFingerPrint;
	}
}
