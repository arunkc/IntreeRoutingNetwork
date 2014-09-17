/**
 * Author	: Arun Kumar Konduru Chandra 
 * NetID	: axk138230 
 * Date 	: 05/02/2014
 * Class	: CS6390.001
 * Purpose	: Programming project for Advanced Computer Networks course.
 */

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

/**Node class to simulate the nodes.
 * Node reads from the input file and prints to the output file.*/
public class node extends Thread{
	
	static int ID,duration,receiverID;
	static String message="";
	static String intree;
	static ArrayList<Integer> incomingNeighbors = new ArrayList<Integer>(); 
	static long startTime = System.currentTimeMillis();
	
	//main() runs till the duration given in command line.
	public static void main(String args[]) 
	{
		ID = Integer.parseInt(args[0]);
		duration = Integer.parseInt(args[1]);
		receiverID=Integer.parseInt(args[2]);
		if(receiverID!=-1)
			message=args[3];
		createAllFiles();
		//Initially Intree is intree ID.
		intree = "intree "+ID;
		for(int i=0;i<=duration;i++)
		{
			try {
				if(i%5==0)
					sendHello();//Sends the hello message every 5 seconds.
				if(i%10==0)
					sendRouting();//Sends intree message every 10 seconds.
				if(i%15==0&&receiverID!=-1)
					sendData();//Sends data every 15 seconds.
				readInput();//Reads input every second.
				sleep(1000L);
			} catch (InterruptedException e) {
				System.out.println("Exception: "+e.toString()+" in node.main()");
			}
		}
	}
	
	//Writes the hello message to output file of this node.
	public static void sendHello() 
	{
		File outputFile = new File("output_"+ID+".txt");
		if(outputFile.exists())
		{
			try {
				PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("output_"+ID+".txt",true)));
				out.println("hello "+ID);
				out.close();
			} catch (Exception e) {
				System.out.println("Exception:"+e.toString()+" in node.sendHello() method.");
			}
		}	
	}
	
	//Writes the intree message to the output file of this node.
	public static void sendRouting()
	{
		File outputFile = new File("output_"+ID+".txt");
		if(outputFile.exists())
		{
			try {
				PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("output_"+ID+".txt",true)));
				out.println(intree);
				out.close();
			} catch (Exception e) {
				System.out.println("Exception:"+e.toString()+" in node.sendRouting() method.");
			}
		}
	}
	
	//Writes the data message to the output file of this node.
	public static void sendData()
	{
		if(((System.currentTimeMillis()-startTime)/1000)%15==0)
		{
			//If the node is incoming neighbour then do source routing.
			if(incomingNeighbors.contains(receiverID))
			{
				String[] intree1 = intree.replace("intree ","").replaceAll("\\(","").replaceAll("\\)", "").split(" ");
				String path="";
				char node = (char) receiverID;
				for(int i=0;i<intree1.length;i++)
				{
					if(intree1[i].charAt(0)==node)
					{
						path = path+intree1[i].charAt(1)+" ";
					}
				}
				String msg = "data "+ID+" "+receiverID+" "+path+" begin "+message;
				writeOutput(msg);
			}
			//Else if the node is not an incoming neighbour then write it into the output file.
			else
			{
				writeOutput("data "+ID+" "+receiverID+" begin "+message);	
			}
		}
	}
	
	/*Reads data from input file.
	 * If it is a hello message add the node to incoming neighbors.
	 * If it is a intree message compare the intree and make improvements.
	 * If it is a data message write the message in received file and 
	 * route it to the destination.*/
	public static void readInput() 
	{
		File inputFile = new File("input_"+ID+".txt");
		try {
			if(inputFile.exists())
			{
				FileReader fileReader = new FileReader(inputFile);
				Scanner scanner = new Scanner(fileReader);
				while(scanner.hasNextLine())
				{
					String line = scanner.nextLine();
					String[] mssg = line.split(" ");
					if(mssg[0].equals("hello"))
					{
						//Adds incoming neighbours.
						if(!incomingNeighbors.contains(mssg[1]))
						{
							incomingNeighbors.add(Integer.parseInt(mssg[1]));
						}
					}
					//If a node receives intree message then modify it and print it in output file.
					else if(mssg[0].equals("intree"))
					{
						if(intree.length()<modifyIntree(line).length()&&((System.currentTimeMillis()-startTime)/1000)%10==0)	
							writeOutput(modifyIntree(line));
					}
					else if(mssg[0].equals("data"))
					{
						//If the node is data message process the data.
						processData(line);
					}
				}
				scanner.close();
				fileReader.close();
			}
		} 
		catch (Exception e) {
			System.out.println("Exception: "+e.toString()+" in node.readInput() method.");
		} 
	}
	
	//Process the data and reroutes the data.
	public static void processData(String msg)
	{
		if(((System.currentTimeMillis()-startTime)/1000)%15==0)
		{
			String[] info=msg.split("begin");
			String data="",path="";
			data = info[1].trim();
			path=info[0].substring(8).trim();
			char source=info[0].charAt(5);
			char dest=info[0].charAt(7);
			File receivedFile = new File(ID+"_received.txt");
			if(receivedFile.exists())
			{
				try {
					PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(ID+"_received.txt",true)));
					out.println("message from "+source+": "+data);
					out.close();
					//Removes duplicate in the received file.
					BufferedReader reader = new BufferedReader(new FileReader(ID+"_received.txt"));
					Set<String> newLines = new HashSet<String>(10000); // maybe should be bigger
				    String newline;
				    while ((newline = reader.readLine()) != null) {
				    	newLines.add(newline);
				    }
				    reader.close();
				    BufferedWriter writer = new BufferedWriter(new FileWriter(ID+"_received.txt"));
				    for (String unique : newLines) {
				        writer.write(unique);
				        writer.newLine();
				    }
				    writer.close();
				    writeOutput("data "+source+" "+path+" "+data);
				} catch (Exception e) {
					System.out.println("Exception: "+e.toString()+ "in node.processData() method.");
				}
			}
			if(!path.isEmpty())
			{
				File outputFile = new File("output_"+ID+".txt");
				if(outputFile.exists())
				{
					try {
						PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("output_"+ID+".txt",true)));
						out.println("data "+source+" "+dest+" "+path+" begin "+data);
						out.close();
					} catch (Exception e) {
						System.out.println("Exception:"+e.toString()+" in node.processData() method.");
					}
				}
			}		

		}
		
	}
	
	//Modifies the intree of the node.
	public static String modifyIntree(String msg)
	{
		String newIntree="intree "+ID;
		String nodeIntree = intree;
		String intree1[] = nodeIntree.replace("intree ","").replaceAll("\\(","").replaceAll("\\)", "").split(" ");
		String intree2[] = msg.replace("intree ","").replaceAll("\\(","").replaceAll("\\)", "").split(" ");
		ArrayList<String> nodes1 = new ArrayList<String>(); 
		ArrayList<String> nodes2 = new ArrayList<String>(); 
		ArrayList<String> nodes3 = new ArrayList<String>(); 
		String node = Integer.toString(ID);
		String completedNodes=node;
		int hops=0;
		for(String s:intree1)
		{
			if(s.length()==2)
				nodes1.add(s);
		}
		for(String s:intree2)
		{
			if(s.length()==1)
				nodes2.add(s+ID);
			else if(s.length()==2)
				nodes2.add(s);
		}
		while(hops<10||(!nodes1.isEmpty()||!nodes2.isEmpty()))
		{
			String newNode="";
			if(nodes1.isEmpty())
			{
				for(String s:nodes2)
				{
					if(!completedNodes.contains(Character.toString(s.charAt(0))))
						nodes3.add(s);
				}
				nodes2.clear();
			}
			else if(nodes2.isEmpty())
			{
				for(String s:nodes1)
				{
					if(!completedNodes.contains(Character.toString(s.charAt(0))))
						nodes3.add(s);
				}
				nodes1.clear();
			}
			else
			{
				List<String> tempNode1 = new ArrayList<String>();
				for(String s:nodes1)
				{
					if(node.contains(""+s.charAt(1)))
					{
						if(!completedNodes.contains(Character.toString(s.charAt(0))))
							tempNode1.add(s);
					}
				}
				nodes1.removeAll(tempNode1);
				List<String> tempNode2 = new ArrayList<String>();
				for(String s:nodes2)
				{
					if(node.contains(""+s.charAt(1)))
					{
						if(!completedNodes.contains(Character.toString(s.charAt(0))))
							tempNode2.add(s);
					}
				}
				nodes2.removeAll(tempNode2);
				List<String> tempNode = new ArrayList<String>();
				Collections.sort(tempNode1);
				Collections.sort(tempNode2);
				int length1=tempNode1.size(),length2=tempNode2.size();
				for(int i=0;i<length1;i++)
				{
					boolean clash=false;
					char t1=tempNode1.get(i).charAt(1),t2 = 0;
					for(int j=0;j<length2;j++)
					{
						if(tempNode1.get(i).charAt(0)==tempNode2.get(j).charAt(0))
						{
							clash=true;
							t2=tempNode2.get(j).charAt(1);
							tempNode2.remove(tempNode2.get(j));
							break;
						}
					}
					if(clash)
					{
						if(t1<t2)
							tempNode.add(tempNode1.get(i));
						else
							tempNode.add(tempNode1.get(i).charAt(0)+""+t2);
					}
					else
					{
						tempNode.add(tempNode1.get(i));
					}
				}
				tempNode.addAll(tempNode2);
				for(String s:tempNode)
					newNode=newNode+s.charAt(0);
				Collections.sort(tempNode);
				nodes3.addAll(tempNode);
				Collections.sort(nodes3);
			}
			for(String s:nodes3)
			{
				newIntree=newIntree+" ("+s+")";
			}
			nodes3.clear();
			completedNodes=completedNodes+newNode;
			node=newNode;
			hops++;
		}
		return newIntree;
	}
	
	/*If after 30 seconds the node does not receive messages from 
	 * some neighbor, it believes the node is no longer a neighbor.
	 * The neighbor is considered no longer reachable. Remove the link from topology file.*/
	public static void checkChannelAlive()
	{
		int senderID,receiverID;
		File topologyFile = new File("topology.txt");
		File temporaryFile = new File("topology.tmp");
        String line;
		try {
    		if(topologyFile.exists())
    		{
				BufferedReader br = new BufferedReader(new FileReader("topology.txt"));
				PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("topology.tmp",false)));
				while ((line=br.readLine())!=null) 
    			{
    				String[] nodes = line.split(" ");
    				senderID = Integer.parseInt(nodes[0]);
    				receiverID = Integer.parseInt(nodes[1]);
    				if(receiverID!=ID||(receiverID==ID&&incomingNeighbors.contains(senderID)))
    					out.println(line);
    			}
				out.close();
				br.close();
    		}
    		if(topologyFile.delete())
    			temporaryFile.renameTo(topologyFile);
        }catch (Exception e) {
    			System.out.println("Exception:"+e.toString());
    		} 
	}
	
	//Creates all the files of this node.
	public static void createAllFiles()
		{
		 File inputFile = new File("input_"+ID+".txt");
		 File outputFile = new File("output_"+ID+".txt");	
		 File receivedFile = new File(ID+"_received.txt");
		 try
			{
				inputFile = new File("input_"+ID+".txt");
				outputFile = new File("output_"+ID+".txt");
				receivedFile = new File(ID+"_received.txt");
				if(!inputFile.exists())
					inputFile.createNewFile();
				if(!outputFile.exists())
					outputFile.createNewFile();
				if(!receivedFile.exists())
					receivedFile.createNewFile();
			}
			catch(Exception e)
			{
				System.out.println("Exception: "+e.toString()+" in controller.createAllFiles()");
			}
		}
	
	public static void writeOutput(String data)
	{
		try {
			PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("output_"+ID+".txt",true)));
			out.println(data);
			out.close();
		} catch (Exception e) {
			System.out.println("Exception:"+e.toString()+" in node.writeOutput() method.");
		}
	}
}
