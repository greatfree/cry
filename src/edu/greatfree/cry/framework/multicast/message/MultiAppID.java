package edu.greatfree.cry.framework.multicast.message;

/**
 * 
 * @author libing
 * 
 * 04/10/2022
 *
 */
public final class MultiAppID
{
	public final static int HELLO_WORLD_BROADCAST_NOTIFICATION = 80000;
	public final static int HELLO_WORLD_ANYCAST_NOTIFICATION = 80001;
	public final static int HELLO_WORLD_UNICAST_NOTIFICATION = 80002;
	
	public final static int SHUTDOWN_ROOT_NOTIFICATION = 80003;
	public final static int SHUTDOWN_CHILDREN_BROADCAST_NOTIFICATION = 80004;
	
	public final static int HELLO_WORLD_BROADCAST_REQUEST = 80005;
	public final static int HELLO_WORLD_BROADCAST_RESPONSE = 80006;
	
	public final static int HELLO_WORLD_ANYCAST_REQUEST = 80007;
	public final static int HELLO_WORLD_ANYCAST_RESPONSE = 80008;
	
	public final static int HELLO_WORLD_UNICAST_REQUEST = 80009;
	public final static int HELLO_WORLD_UNICAST_RESPONSE = 80010;

	public final static int CLIENT_BROADCAST_NOTIFICATION = 80011;
	public final static int CLIENT_ANYCAST_NOTIFICATION = 80012;
	public final static int CLIENT_UNICAST_NOTIFICATION = 80013;
	
	public final static int CLIENT_BROADCAST_REQUEST = 80014;
	public final static int CLIENT_BROADCAST_RESPONSE = 80015;
	
	public final static int CLIENT_ANYCAST_REQUEST = 80016;
	public final static int CLIENT_ANYCAST_RESPONSE = 80017;
	
	public final static int CLIENT_UNICAST_REQUEST = 80018;
	public final static int CLIENT_UNICAST_RESPONSE = 80019;
	
	public final static int ADMIN_STOP_CHILDREN_NOTIFICATION = 80020;
	public final static int ADMIN_STOP_ROOT_NOTIFICATION = 80021;
	
	public final static int STOP_CHILDREN_NOTIFICATION = 80020;
	public final static int STOP_ROOT_NOTIFICATION = 80021;
}
