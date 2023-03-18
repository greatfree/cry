package edu.greatfree.cry.multicast;

/**
 * 
 * @author libing
 * 
 * 04/06/2022
 *
 */
public class MulticastConfig
{
	public final static int PLAIN = 0;
	public final static int SYM = 1;
	public final static int ASYM = 2;
	public final static int SIGNED = 3;
	public final static int PRIVATE = 4;
	
	public static String cryptoOption(int option)
	{
		switch (option)
		{
			case PLAIN:
				return "PLAIN";
				
			case SYM:
				return "SYM";
				
			case ASYM:
				return "ASYM";
				
			case SIGNED:
				return "SIGNED";
				
			default:
				return "PRIVATE";
		}
	}
}
