package edu.greatfree.cry.framework.cluster.client;

/**
 * 
 * @author libing
 * 
 * 02/25/2023
 *
 */
final class ClusterMenu
{
	public final static String TAB = "	";
	public final static String DELIMIT = "-----------------------------------------------";
	public final static String MENU_HEAD = "\n========== Menu Head ===========";
	public final static String BROADCAST_NOTIFICATION = TAB + "1) Broadcast a notification";
	public final static String ANYCAST_NOTIFICATION = TAB + "2) Anycast a notification";
	public final static String UNICAST_NOTIFICATION = TAB + "3) Unicast a notification";
	public final static String BROADCAST_REQUEST = TAB + "4) Broadcast a request";
	public final static String ANYCAST_REQUEST = TAB + "5) Anycast a request";
	public final static String UNICAST_REQUEST = TAB + "6) Unicast a request";

	// An application-level ID upon a child as the source sends the notification to all the multiples application-level IDs upon a number of children as the destinations. 06/19/2022, Bing Li
	public final static String INTER_BROADCAST_NOTIFICATION = TAB + "7) Inter-broadcast a notification";
	// An application-level ID upon a child as the source sends the notification to some of multiples application-level IDs upon a number of children as the destinations. 06/19/2022, Bing Li
	public final static String INTER_ANYCAST_NOTIFICATION = TAB + "8) Inter-anycast a notification";
	// An application-level ID upon a child as the source sends the notification to another application-level ID upon a single child as the destination. 06/19/2022, Bing Li
	public final static String INTER_UNICAST_NOTIFICATION = TAB + "9) Inter-unicast a notification";

	// An application-level ID upon a child as the source sends the notification to all the multiples application-level IDs upon a number of children as the destinations. 06/19/2022, Bing Li
	public final static String INTER_BROADCAST_REQUEST = TAB + "10) Inter-broadcast a request";
	// An application-level ID upon a child as the source sends the notification to some of multiples application-level IDs upon a number of children as the destinations. 06/19/2022, Bing Li
	public final static String INTER_ANYCAST_REQUEST = TAB + "11) Inter-anycast a request";
	public final static String INTER_UNICAST_REQUEST = TAB + "12) Inter-unicast a request";

	public final static String CLAIM_OWNERSHIP = TAB + "13) Claim ownership";
	public final static String ABANDON_OWNERSHIP = TAB + "14) Abandon ownership";

	public final static String QUIT = TAB + "0) Quit";
	public final static String MENU_TAIL = "========== Menu Tail ===========\n";
	public final static String INPUT_PROMPT = "Input an option:";
	
	public final static String WRONG_OPTION = "Wrong option!";
}
