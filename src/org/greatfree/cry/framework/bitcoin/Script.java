package org.greatfree.cry.framework.bitcoin;

import java.util.Stack;

/**
 * 
 * @author Bing Li
 * 
 * 02/13/2022
 *
 */
public class Script extends Stack<String>
{
	private static final long serialVersionUID = 8361223904722746182L;

	public void pushInstruction(String instruction)
	{
		super.add(instruction);
	}
	
	public String popInstruction()
	{
		return super.pop();
	}

	public boolean isEmpty()
	{
		return super.isEmpty();
	}
}
