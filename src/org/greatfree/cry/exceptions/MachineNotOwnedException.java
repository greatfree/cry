package org.greatfree.cry.exceptions;

/**
 * 
 * @author libing
 * 
 * 03/22/2022
 *
 */
public class MachineNotOwnedException extends Exception
{
	private static final long serialVersionUID = -1904218156644652536L;
	
	private String signature;
	
	public MachineNotOwnedException(String signature)
	{
		this.signature = signature;
	}

	public String getSignature()
	{
		return this.signature;
	}
}
