package org.greatfree.cry.framework.blockchain.message;

import org.greatfree.message.container.Request;

/**
 * 
 * @author libing
 * 
 * 01/28/2022, Bing Li
 *
 */
public class PrecedingFingerPrintRequest extends Request
{
	private static final long serialVersionUID = -4522585535360129776L;
	
	private String sessionKey;

	public PrecedingFingerPrintRequest()
	{
		super(ChainAppID.PRECEDING_FINGER_PRINT_REQUEST);
	}

	public PrecedingFingerPrintRequest(String sessionKey)
	{
		super(ChainAppID.PRECEDING_FINGER_PRINT_REQUEST);
		this.sessionKey = sessionKey;
	}

	public String getSessionKey()
	{
		return this.sessionKey;
	}
}
