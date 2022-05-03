package org.greatfree.cry.framework.cluster.client;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.ShortBufferException;

import org.greatfree.chat.ChatMenu;
import org.greatfree.chat.ChatOptions;
import org.greatfree.cry.exceptions.CheatingException;
import org.greatfree.cry.exceptions.CryptographyMismatchException;
import org.greatfree.cry.exceptions.OwnerCheatingException;
import org.greatfree.cry.exceptions.PublicKeyUnavailableException;
import org.greatfree.cry.exceptions.SymmetricKeyUnavailableException;
import org.greatfree.cry.framework.cluster.message.HelloAnycastNotification;
import org.greatfree.cry.framework.cluster.message.HelloAnycastRequest;
import org.greatfree.cry.framework.cluster.message.HelloAnycastResponse;
import org.greatfree.cry.framework.cluster.message.HelloBroadcastNotification;
import org.greatfree.cry.framework.cluster.message.HelloBroadcastRequest;
import org.greatfree.cry.framework.cluster.message.HelloBroadcastResponse;
import org.greatfree.cry.framework.cluster.message.HelloUnicastNotification;
import org.greatfree.cry.framework.cluster.message.HelloUnicastRequest;
import org.greatfree.cry.framework.cluster.message.HelloUnicastResponse;
import org.greatfree.cry.framework.multicast.client.MultiOptions;
import org.greatfree.cry.multicast.MulticastConfig;
import org.greatfree.exceptions.DistributedNodeFailedException;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.message.multicast.container.CollectedClusterResponse;
import org.greatfree.util.Time;
import org.greatfree.util.Tools;

/**
 * 
 * @author libing
 * 
 * 04/27/2022
 *
 */
final class ClusterUI
{
	private Scanner in = new Scanner(System.in);

	private ClusterUI()
	{
	}

	private static ClusterUI instance = new ClusterUI();
	
	public static ClusterUI CRY()
	{
		if (instance == null)
		{
			instance = new ClusterUI();
			return instance;
		}
		else
		{
			return instance;
		}
	}
	
	public void dispose()
	{
		this.in.close();
	}

	public void printMenu()
	{
		System.out.println(ChatMenu.MENU_HEAD);
		System.out.println(ChatMenu.TYPE_MESSAGE);
		System.out.println(ChatMenu.QUIT);
		System.out.println(ChatMenu.MENU_TAIL);
		System.out.println(ChatMenu.INPUT_PROMPT);
	}

	public void send(int highOption, int option) throws InvalidKeyException, ClassNotFoundException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, SignatureException, RemoteReadException, IOException, InterruptedException, DistributedNodeFailedException, CryptographyMismatchException, SymmetricKeyUnavailableException, PublicKeyUnavailableException, ShortBufferException, OwnerCheatingException, CheatingException
	{
		int index = 0;
		CollectedClusterResponse response;
		List<HelloBroadcastResponse> bResponses;
		List<HelloAnycastResponse> aResponses;
		List<HelloUnicastResponse> uResponses;
		Date startTime;
		Date endTime;
		switch (option)
		{
			case ChatOptions.TYPE_CHAT:
				System.out.println("Please type your message: ");
				String message = in.nextLine();
				switch (highOption)
				{
					case MultiOptions.BROADCAST_NOTIFICATION:
						startTime = Calendar.getInstance().getTime();
						ClientUI.CRY().notify(new HelloBroadcastNotification(message), MulticastConfig.PLAIN);
						endTime = Calendar.getInstance().getTime();
						System.out.println("You notification is broadcast!");
						System.out.println("It takes " + Time.getTimespanInMilliSecond(endTime, startTime) + " ms to send the notification");
						break;
						
					case MultiOptions.ANYCAST_NOTIFICATION:
						startTime = Calendar.getInstance().getTime();
						ClientUI.CRY().notify(new HelloAnycastNotification(message), MulticastConfig.PLAIN);
						endTime = Calendar.getInstance().getTime();
						System.out.println("You notification is anycast!");
						System.out.println("It takes " + Time.getTimespanInMilliSecond(endTime, startTime) + " ms to send the notification");
						break;
						
					case MultiOptions.UNICAST_NOTIFICATION:
						startTime = Calendar.getInstance().getTime();
						ClientUI.CRY().notify(new HelloUnicastNotification(message), MulticastConfig.PLAIN);
						endTime = Calendar.getInstance().getTime();
						System.out.println("You notification is unicast!");
						System.out.println("It takes " + Time.getTimespanInMilliSecond(endTime, startTime) + " ms to send the notification");
						break;
						
					case MultiOptions.BROADCAST_REQUEST:
						startTime = Calendar.getInstance().getTime();
						response = (CollectedClusterResponse)ClientUI.CRY().read(new HelloBroadcastRequest(message), MulticastConfig.PLAIN);
						bResponses = Tools.filter(response.getResponses(), HelloBroadcastResponse.class);
						endTime = Calendar.getInstance().getTime();
						System.out.println("You request is broadcast!");
						System.out.println("You got " + bResponses.size() + " responses");
						for (HelloBroadcastResponse entry : bResponses)
						{
							System.out.println(++index + ") response = " + entry.getMessage());
						}
						System.out.println("It takes " + Time.getTimespanInMilliSecond(endTime, startTime) + " ms to receive the response");
						break;

					case MultiOptions.ANYCAST_REQUEST:
						startTime = Calendar.getInstance().getTime();
						response = (CollectedClusterResponse)ClientUI.CRY().read(new HelloAnycastRequest(message), MulticastConfig.PLAIN);
						aResponses = Tools.filter(response.getResponses(), HelloAnycastResponse.class);
						endTime = Calendar.getInstance().getTime();
						System.out.println("You request is anycast!");
						System.out.println("You got " + aResponses.size() + " responses");
						for (HelloAnycastResponse entry : aResponses)
						{
							System.out.println(++index + ") response = " + entry.getMessage());
						}
						System.out.println("It takes " + Time.getTimespanInMilliSecond(endTime, startTime) + " ms to receive the response");
						break;

					case MultiOptions.UNICAST_REQUEST:
						startTime = Calendar.getInstance().getTime();
						response = (CollectedClusterResponse)ClientUI.CRY().read(new HelloUnicastRequest(message), MulticastConfig.PLAIN);
						uResponses = Tools.filter(response.getResponses(), HelloUnicastResponse.class);
						endTime = Calendar.getInstance().getTime();
						System.out.println("You request is unicast!");
						System.out.println("You got " + uResponses.size() + " responses");
						for (HelloUnicastResponse entry : uResponses)
						{
							System.out.println(++index + ") response = " + entry.getMessage());
						}
						System.out.println("It takes " + Time.getTimespanInMilliSecond(endTime, startTime) + " ms to receive the response");
						break;

					case MultiOptions.SYM_BROADCAST_NOTIFICATION:
						break;

					case MultiOptions.SYM_ANYCAST_NOTIFICATION:
						break;

					case MultiOptions.SYM_UNICAST_NOTIFICATION:
						break;

					case MultiOptions.SYM_BROADCAST_REQUEST:
						break;

					case MultiOptions.SYM_ANYCAST_REQUEST:
						break;

					case MultiOptions.SYM_UNICAST_REQUEST:
						break;

					case MultiOptions.ASYM_BROADCAST_NOTIFICATION:
						break;
						
					case MultiOptions.ASYM_ANYCAST_NOTIFICATION:
						break;

					case MultiOptions.ASYM_UNICAST_NOTIFICATION:
						break;

					case MultiOptions.ASYM_BROADCAST_REQUEST:
						break;

					case MultiOptions.ASYM_ANYCAST_REQUEST:
						break;

					case MultiOptions.ASYM_UNICAST_REQUEST:
						break;

					case MultiOptions.SIGNED_BROADCAST_NOTIFICATION:
						break;

					case MultiOptions.SIGNED_ANYCAST_NOTIFICATION:
						break;

					case MultiOptions.SIGNED_UNICAST_NOTIFICATION:
						break;

					case MultiOptions.SIGNED_BROADCAST_REQUEST:
						break;

					case MultiOptions.SIGNED_ANYCAST_REQUEST:
						break;

					case MultiOptions.SIGNED_UNICAST_REQUEST:
						break;
						
					case MultiOptions.CLAIM_OWNERSHIP:
						break;

					case MultiOptions.ABANDON_OWNERSHIP:
						break;
				}
				break;

			case ChatOptions.QUIT_CHAT:
				break;
		}
		
		
	}
}
