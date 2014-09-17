/**
 * Author	: Arun Kumar Konduru Chandra 
 * NetID	: axk138230 
 * Date 	: 04/27/2014
 * Class	: CS6390.001
 * Purpose	: Programming project for Advanced Computer Networks course.
 */

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

/**Controller class to simulate controller node.
 * Controller reads from the output file checks topology 
 * and prints in input file of neighbours.*/
public class controller extends Thread{
	
    static int duration;

    //main() runs till the given duration in command line argument and stops.
    public static void main(String args[]) 
	{
		duration = Integer.parseInt(args[0]);
		for(int i=0;i<=duration;i++)
		{	
			try {	
    			copyData();
   	         	Thread.sleep(1000L);  
			} catch (Exception e) {
   	    	System.out.println("Exception:Exception in controller.run()");
			}
		}
	}
    
    /*Copies data from the output files of active nodes to the input files 
     * of all the neighbors of the node by checking the topology file.*/
    public static void copyData()
    {
    	int senderID,receiverID;
        File topologyFile = new File("topology.txt");
        try {
    		if(topologyFile.exists())
    		{
    			FileReader fileReader = new FileReader(topologyFile);
				Scanner scanner = new Scanner(fileReader);
    			while (scanner.hasNextLine()) 
    			{
    				String line = scanner.nextLine();
    				String lines;
    				String[] nodes = line.split(" ");
    				senderID = Integer.parseInt(nodes[0]);
    				receiverID = Integer.parseInt(nodes[1]);
    				BufferedReader br = new BufferedReader(new FileReader("output_"+senderID+".txt"));
    				PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("input_"+receiverID+".txt",true)));
					while((lines=br.readLine())!=null)
					{
						out.println(lines);
					}
					out.close();
					br.close();
					//To remove duplicate messages from input file.
					BufferedReader reader = new BufferedReader(new FileReader("input_"+receiverID+".txt"));
				    Set<String> newLines = new HashSet<String>(10000); // maybe should be bigger
				    String newline;
				    while ((newline = reader.readLine()) != null) {
				        newLines.add(newline);
				    }
				    reader.close();
				    BufferedWriter writer = new BufferedWriter(new FileWriter("input_"+receiverID+".txt"));
				    for (String unique : newLines) {
				        writer.write(unique);
				        writer.newLine();
				    }
				    writer.close();
    			}
    			scanner.close();
    		}
    		else
    		{
    			System.out.println("Topology file does not exist. Create a topology file.");
    			System.exit(1);
    		}
		} catch (Exception e) {
			System.out.println("Exception:"+e.toString());
		} 
    }

}
