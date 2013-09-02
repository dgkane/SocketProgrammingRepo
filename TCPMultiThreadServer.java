/*
 * 
 * Accepts socket connections from multiple client classes.
 * 
 * When (string) message is received from a client,
 * class stores it in a buffer particular to each client
 * and returns each constituent character's numerical ASCII code.
 * 
 * Buffer is written to a text file on closing socket connection.
 * Socket connection closed in response to 'CLOSE' message from client.
 * -DK
 * 
 *
 * @author David Kane, Stephen Swires. Michael Kemp, Michael Brookes
 * @version 28 March 2012
 */
import java.io.*; 
import java.net.*; 

// Adapted from course materials
// Original work not credited
public class TCPMultiThreadServer { 
    
    
    private static ServerSocket welcomeSocket;
    
    // Sets port number for this connection - 1024-49151
    // Number used is random
    private static final int PORT = 1234;
    
    private static int clientNo =1;
    
    
    public static void main(String argv[]) throws Exception 
    { 
 
        System.out.println("Opening port...\n");

        try
	{
           // ServerSocket listens for new connections on specified port
           welcomeSocket = new ServerSocket(PORT);
           
        
		
            do
            { 
                // When a connection is made,
                // creates a socket server-side for connected class
                Socket client = welcomeSocket.accept();
                
                System.out.println("\nNew client accepted.\n");
            
                // Create a thread to handle communication with
                // this client and pass its constructor a reference
                // to relevant socket
	    
                TCPMultiThreadServer.ClientHandler handler = 
                new TCPMultiThreadServer().new ClientHandler(client,clientNo);
      
                handler.start(); // Calls run() method in ClientHandler
			
                clientNo++; 
			     
        } while (true);
        
        } catch (IOException e) {}
    }

// Adapted from course materials
// Original work not credited
class ClientHandler extends Thread
{
        private Socket client;
	
        private BufferedReader inFromClient;
        private BufferedReader text_to_Client;
        private DataOutputStream outToClient;
        private FileWriter fstream;
        private BufferedWriter out;
        
        public int clientNo;

        public boolean stopping;
        
        // ***************************************
	// *** Buffer implementation (part 1d) ***
        // ***************************************
        //
        //
        private String[] buffer; // Declare Array of Strings which will later
                                 // store 10 messages
        private int bufferIndex; // Index of the last element inserted into 
                                 // buffer array
        
	public ClientHandler(Socket socket, int clientNos)
	{
            
            //Set up reference to associated socket
            client = socket;
            clientNo= clientNos;

            try
            {   
                 // Gets access to input/output stream of socket
                 inFromClient =
                 new BufferedReader(new InputStreamReader
                 (client.getInputStream()));
                 text_to_Client =
                 new BufferedReader(new InputStreamReader(System.in));

                 outToClient = 
                 new DataOutputStream(client.getOutputStream()); 
                 
            } catch(IOException e) {}
	}

	public void run()
	{
	  
            try
            {
		stopping = false;
		
                // ***************************************
		// *** Buffer implementation (part 1d) ***
                // ***************************************
                //
                buffer = new String[10]; // Create array of 10 Strings 
                bufferIndex = 0; // Initialise bufferIndex to make sure it's 0
                
		String clientSentence; // Used for both message received from
                                       // client, and message to be sent back
                                       // to client
                // String ASCIIValues; // For holding ASCII values of string
                                    // characters
                // String capitalizedSentence;
                
		Thread mythread = Thread.currentThread(); 
		
                // ***********************************************************
		// *** Multithread Exercise 2e: Message by message logging ***
                // ***********************************************************
                //
		// open file for writing our logs to
                //fstream = new FileWriter( "server_log" + clientNo + ".txt" );
                // buffered writer used to write to file
		//out = new BufferedWriter( fstream );
                
		
                do
                {
                    //Accept message from client on socket's input stream
                    OutputStream sout = client.getOutputStream();
                    
                    // Just converting them to different streams, so that string
                    // handling becomes easier.
                    DataOutputStream text_to_send = new DataOutputStream(sout);
    
                    clientSentence = inFromClient.readLine();
                    
                    // ********************************************
                    // *** Multithread exercise part 2c - ASCII ***
                    // ********************************************
                    //
                    // ASCIIValues = " "; // Clears ASCII values string for   
                                       // printing
                    
                    // For loop iterates through characters constituting
                    // clientSentence string
                    // for (int i = 0; i < clientSentence.length(); i++) 
                    // {   
                    //     // Takes character at position i
                    //     char c = clientSentence.charAt(i); 
                    //     // Converts to an integer value (ASCII value)
                    //     int charValue = c;
                    //      // Appends the integer to the ASCII values string
                    //     ASCIIValues += charValue + ", ";
                    //             
                    // }
                    
                    // Prints the ASCII values of all characters in string sent
                    // from client.
                    System.out.println("Message received from client number " +
                            clientNo + ": "+ clientSentence);
                    
                    // for loop for ASCII conversion, using
                    // (int)clientSentence.charAt shows the integer value of the
                    // character at the index value "i" - which is the ASCII
                    // value. This seemed the easiest solution I have found.
                    //
                    for (int i=0; i<clientSentence.length(); i++)
                        System.out.println("The ASCII value of " +
                                clientSentence.charAt(i) + " is: " +
                                (int)clientSentence.charAt(i));
                    
                    // System.out.println("ASCII value of characters in string:"
                    //        + ASCIIValues); 
                    
                    // ***********************************************
                    // *** Multithread Excercise 1d: Close command ***
                    // ***********************************************
                    //
                    // We have received the message "CLOSE" from the client.
                    if(clientSentence.equals("CLOSE")) 
                    {
                        // Print message to server console, also shows which
                        // client closed the connection
                        System.out.println("Socket connection to client number "
                                + clientNo + " closed");
			
                        // ******************************************
			// *** Multithread Excercise 2e: Logging ****
                        // ******************************************
                        //
			// This has two implementations. One of them logs each
                        // line as it is received by the server
			// The other writes the contents of the buffer to a
                        // separate file
                        //
                        try
                        {
                            // Flush to BufferedWriter so there isn't any 
                            // missing lines
                            // out.flush();
                            // Close the BufferedWriter for the line-by-line log
                            // out.close();
							
                            // Reassign these two pointers to a new file for
                            // outputting the buffer when closing
                            fstream = new FileWriter("message_log" + clientNo + 
                                    ".txt"); // Open new FileWriter for saving
                                             // out chatlog
                            out = new BufferedWriter(fstream); // Create
                                    // BufferedWriter for writing to our chatlog
                            
                            // Iterate through message buffer
                            for(int i = 0; i<buffer.length; i++) 
                            {
                                // Write buffer element to text file
                                out.write(buffer[i]); 
                                // Create a new line between each message
                                out.newLine();
                            }
                    
                        } catch(Exception e) {}
                    
			out.flush();
                        out.close(); // Close the file handler
                        client.close(); // Close the connection with the client,
                                        // will flush any buffered text
                        clientNo--; // Decrement the number of clients
                    }
                    else // We didn't get a close command
                    {
			// ***************************************
                        // *** Buffer implementation (part 1d) ***
                        // ***************************************
                        //
                        //
                        // Check to see if the buffer is full.
                        // Checks if bufferIndex is in range of array
                        if (bufferIndex > buffer.length-1) 
			{   
                            // Print message to tell the buffer is full
                            System.out.println("Message buffer for this client "
                                    + "full!");
                            // Clear clientSentence string for printing
                            clientSentence = " "; 
                            
                            // For loop which itterates through the buffer
                            // indices.
                            //
                            for (int i=0; i<buffer.length; i++)
                            {
                               // Append buffer element to clientSentence string
                               clientSentence += buffer[i] + " , "; 
                               buffer[i] = null; // Nullify buffer element
                               
                            }
                            
                            bufferIndex = 0; // Reset bufferIndex back to 0 so
                                             // we can start writing to it from
                                             // the start
                            // Send message back to client
                            text_to_send.writeUTF("Buffer cleared! :" +
                                    clientSentence); 
                            
                        }
                        else
                        {
                            buffer[bufferIndex] = clientSentence;
                            System.out.println("Buffer " + bufferIndex + ": " +
                                    buffer[bufferIndex]);
                            
                            // ********************************
                            // *** Multithread Excercise 2e ***
                            // ********************************
                            //
                            //// message we just received
                            // out.write( buffer[bufferIndex] ); 
                            // out.newLine(); // blank line
							
							
                            bufferIndex++;
                            System.out.println("Enter Message: ");
                            // Reads line of text from server interface
                            // and sends back to client
                            clientSentence = text_to_Client.readLine();
                            //capitalizedSentence = clientSentence.toUpperCase()
                            //+ '\n';
                            //int charCount = clientSentence.length();
                            text_to_send.writeUTF(clientSentence);
                            System.out.println("Your message: " +
                                    clientSentence);
                            //outToClient.write(charCount);
				
                            // System.out.println("\nlength: " + charCount);
                            
                        }
                    }
                    
                    // Exits program if a) a connection has been made and b) all
                    // current connections (threads) are closed
                    if (mythread.activeCount() == 2 &&
                            (clientNo ==0 || clientNo >0) &&
                                clientSentence.equals("CLOSE"))
                    {
                        
                        System.exit(0);
                    }
             
                } while(!clientSentence.equals("CLOSE"));
       
                // client.close();
        
        } catch(IOException e) {} 
     
        } 
    } 
}
