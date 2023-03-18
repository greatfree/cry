package edu.greatfree.cry.framework.multicast.client;

/**
 * 
 * @author libing
 * 
 * 04/12/2022
 *
 */
final class MultiOptions
{
	public final static int NO_OPTION = -1;

	public final static int BROADCAST_NOTIFICATION = 1;
	public final static int ANYCAST_NOTIFICATION = 2;
	public final static int UNICAST_NOTIFICATION = 3;
	public final static int BROADCAST_REQUEST = 4;
	public final static int ANYCAST_REQUEST = 5;
	public final static int UNICAST_REQUEST = 6;

	public final static int SYM_BROADCAST_NOTIFICATION = 7;
	public final static int SYM_ANYCAST_NOTIFICATION = 8;
	public final static int SYM_UNICAST_NOTIFICATION = 9;
	public final static int SYM_BROADCAST_REQUEST = 10;
	public final static int SYM_ANYCAST_REQUEST = 11;
	public final static int SYM_UNICAST_REQUEST = 12;

	public final static int ASYM_BROADCAST_NOTIFICATION = 13;
	public final static int ASYM_ANYCAST_NOTIFICATION = 14;
	public final static int ASYM_UNICAST_NOTIFICATION = 15;
	public final static int ASYM_BROADCAST_REQUEST = 16;
	public final static int ASYM_ANYCAST_REQUEST = 17;
	public final static int ASYM_UNICAST_REQUEST = 18;

	public final static int SIGNED_BROADCAST_NOTIFICATION = 19;
	public final static int SIGNED_ANYCAST_NOTIFICATION = 20;
	public final static int SIGNED_UNICAST_NOTIFICATION = 21;
	public final static int SIGNED_BROADCAST_REQUEST = 22;
	public final static int SIGNED_ANYCAST_REQUEST = 23;
	public final static int SIGNED_UNICAST_REQUEST = 24;
	
	public final static int CLAIM_OWNERSHIP = 25;
	public final static int ABANDON_OWNERSHIP = 26;

	public final static int PRIVATE_BROADCAST_NOTIFICATION = 27;
	public final static int PRIVATE_ANYCAST_NOTIFICATION = 28;
	public final static int PRIVATE_UNICAST_NOTIFICATION = 29;
	public final static int PRIVATE_BROADCAST_REQUEST = 30;
	public final static int PRIVATE_ANYCAST_REQUEST = 31;
	public final static int PRIVATE_UNICAST_REQUEST = 32;

	public final static int INTER_BROADCAST_NOTIFICATION = 33;
	public final static int INTER_ANYCAST_NOTIFICATION = 34;
	public final static int INTER_UNICAST_NOTIFICATION = 35;
	public final static int INTER_BROADCAST_REQUEST = 36;
	public final static int INTER_ANYCAST_REQUEST = 37;
	public final static int INTER_UNICAST_REQUEST = 38;

	public final static int QUIT = 0;
}
