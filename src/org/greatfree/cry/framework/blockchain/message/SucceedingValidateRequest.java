package org.greatfree.cry.framework.blockchain.message;

import org.greatfree.message.container.Request;

/**
 * 
 * @author libing
 * 
 * 01/27/2022, Bing Li
 *
 */
public class SucceedingValidateRequest extends Request
{
	private static final long serialVersionUID = -6165002753842362016L;
	
//	private int sequenceNO;
	private String fingerPrint;

//	public SucceedingValidateRequest(int sequenceNO, String fingerPrint)
	public SucceedingValidateRequest(String fingerPrint)
	{
		super(ChainAppID.SUCCEEDING_VALIDATE_REQUEST);
//		this.sequenceNO = sequenceNO;
		this.fingerPrint = fingerPrint;
	}

	/*
	public int getSequenceNO()
	{
		return this.sequenceNO;
	}
	*/

	public String getFingerPrint()
	{
		return this.fingerPrint;
	}
}
