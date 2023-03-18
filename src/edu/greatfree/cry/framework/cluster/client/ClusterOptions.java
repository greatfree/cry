package edu.greatfree.cry.framework.cluster.client;

/**
 * 
 * @author libing
 * 
 * 02/25/2023
 *
 */
final class ClusterOptions
{
	public final static int NO_OPTION = -1;

	public final static int BROADCAST_NOTIFICATION = 1;
	public final static int ANYCAST_NOTIFICATION = 2;
	public final static int UNICAST_NOTIFICATION = 3;
	public final static int BROADCAST_REQUEST = 4;
	public final static int ANYCAST_REQUEST = 5;
	public final static int UNICAST_REQUEST = 6;

	public final static int INTER_BROADCAST_NOTIFICATION = 7;
	public final static int INTER_ANYCAST_NOTIFICATION = 8;
	public final static int INTER_UNICAST_NOTIFICATION = 9;
	public final static int INTER_BROADCAST_REQUEST = 10;
	public final static int INTER_ANYCAST_REQUEST = 11;
	public final static int INTER_UNICAST_REQUEST = 12;

	public final static int CLAIM_OWNERSHIP = 13;
	public final static int ABANDON_OWNERSHIP = 14;

	public final static int QUIT = 0;
}
