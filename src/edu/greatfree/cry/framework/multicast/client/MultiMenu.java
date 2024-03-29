package edu.greatfree.cry.framework.multicast.client;

/**
 * 
 * @author libing
 * 
 * 04/11/2022
 *
 */
final class MultiMenu
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

	public final static String SYM_BROADCAST_NOTIFICATION = TAB + "7) Broadcast a notification symmetrically";
	public final static String SYM_ANYCAST_NOTIFICATION = TAB + "8) Anycast a notification symmetrically";
	public final static String SYM_UNICAST_NOTIFICATION = TAB + "9) Unicast a notification symmetrically";
	public final static String SYM_BROADCAST_REQUEST = TAB + "10) Broadcast a request symmetrically";
	public final static String SYM_ANYCAST_REQUEST = TAB + "11) Anycast a request symmetrically";
	public final static String SYM_UNICAST_REQUEST = TAB + "12) Unicast a request symmetrically";

	public final static String ASYM_BROADCAST_NOTIFICATION = TAB + "13) Broadcast a notification asymmetrically";
	public final static String ASYM_ANYCAST_NOTIFICATION = TAB + "14) Anycast a notification asymmetrically";
	public final static String ASYM_UNICAST_NOTIFICATION = TAB + "15) Unicast a notification asymmetrically";
	public final static String ASYM_BROADCAST_REQUEST = TAB + "16) Broadcast a request asymmetrically";
	public final static String ASYM_ANYCAST_REQUEST = TAB + "17) Anycast a request asymmetrically";
	public final static String ASYM_UNICAST_REQUEST = TAB + "18) Unicast a request asymmetrically";

	public final static String SIGNED_BROADCAST_NOTIFICATION = TAB + "19) Broadcast a notification with signature";
	public final static String SIGNED_ANYCAST_NOTIFICATION = TAB + "20) Anycast a notification with signature";
	public final static String SIGNED_UNICAST_NOTIFICATION = TAB + "21) Unicast a notification with signature";
	public final static String SIGNED_BROADCAST_REQUEST = TAB + "22) Broadcast a request with signature";
	public final static String SIGNED_ANYCAST_REQUEST = TAB + "23) Anycast a reques with signature";
	public final static String SIGNED_UNICAST_REQUEST = TAB + "24) Unicast a request with signature";

	public final static String CLAIM_OWNERSHIP = TAB + "25) Claim ownership";
	public final static String ABANDON_OWNERSHIP = TAB + "26) Abandon ownership";

	public final static String PRIVATE_BROADCAST_NOTIFICATION = TAB + "27) Broadcast a notification privately";
	public final static String PRIVATE_ANYCAST_NOTIFICATION = TAB + "28) Anycast a notification privately";
	public final static String PRIVATE_UNICAST_NOTIFICATION = TAB + "29) Unicast a notification privately";
	public final static String PRIVATE_BROADCAST_REQUEST = TAB + "30) Broadcast a request privately";
	public final static String PRIVATE_ANYCAST_REQUEST = TAB + "31) Anycast a reques privately";
	public final static String PRIVATE_UNICAST_REQUEST = TAB + "32) Unicast a request privately";

	// An application-level ID upon a child as the source sends the notification to all the multiples application-level IDs upon a number of children as the destinations. 06/19/2022, Bing Li
	public final static String INTER_BROADCAST_NOTIFICATION = TAB + "33) Inter-broadcast a notification";
	// An application-level ID upon a child as the source sends the notification to some of multiples application-level IDs upon a number of children as the destinations. 06/19/2022, Bing Li
	public final static String INTER_ANYCAST_NOTIFICATION = TAB + "34) Inter-anycast a notification";
	// An application-level ID upon a child as the source sends the notification to another application-level ID upon a single child as the destination. 06/19/2022, Bing Li
	public final static String INTER_UNICAST_NOTIFICATION = TAB + "35) Inter-unicast a notification";

	// An application-level ID upon a child as the source sends the notification to all the multiples application-level IDs upon a number of children as the destinations. 06/19/2022, Bing Li
	public final static String INTER_BROADCAST_REQUEST = TAB + "36) Inter-broadcast a request";
	// An application-level ID upon a child as the source sends the notification to some of multiples application-level IDs upon a number of children as the destinations. 06/19/2022, Bing Li
	public final static String INTER_ANYCAST_REQUEST = TAB + "37) Inter-anycast a request";
	public final static String INTER_UNICAST_REQUEST = TAB + "38) Inter-unicast a request";

	public final static String QUIT = TAB + "0) Quit";
	public final static String MENU_TAIL = "========== Menu Tail ===========\n";
	public final static String INPUT_PROMPT = "Input an option:";
	
	public final static String WRONG_OPTION = "Wrong option!";
}
