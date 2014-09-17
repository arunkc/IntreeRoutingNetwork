Unix Machine Used	:	cs2.utdallas.edu


Compiling Instruction:

1.Compile both the controller.java and the node.java using following commands.
{cslinux2:~/acn} javac controller.java
{cslinux2:~/acn} javac node.java

Running Instruction:

1.Before Running the code you need to create a topology file with the description of node topology. Otherwise it returns a exception and the process terminates.

2.You can run the code using the following commands
{cslinux2:~/acn} java controller "duration" &
{cslinux2:~/acn} javac node "nodeID" "duration" "receiverID" "message" &
duration = keep alive time for process
nodeID = name of the node(from 0 to 9)
receiverID = name of the receiver message needs to be sent(from 0 to 9)
Note: If there is no message to send then receiverID = -1
message = message tosend to the receiver
Note:add & at the end to run the process in background.

3.Alternatively you can add all the commands in scenario.txt file and run by using the following command
{cslinux2:~/acn} bash scenario.txt

4.All output files will be created in the same directory.