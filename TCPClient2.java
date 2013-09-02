/*
 * This is a client class for connecting to the TCPMultiThreadServer class.
 * 
 * It can take input from the user and send it to the server class as a string.
 * There is only minor alterations from the course material
 * and addition of annotation.
 * 
 * @author anon, David Kane
 */
import java.io.*; 
import java.net.*;

class TCPClient2 { 

    public static void main(String argv[]) throws Exception 
    { 
        String sentence; 
        String modifiedSentence; 

        BufferedReader inFromUser = 
          new BufferedReader(new InputStreamReader(System.in)); 

        Socket clientSocket = new Socket("localhost",1234);

        DataOutputStream outToServer = 
          new DataOutputStream(clientSocket.getOutputStream()); 
        
        InputStream sin = clientSocket.getInputStream();


 	// Just converting them to different streams, so that string handling becomes easier.
	DataInputStream inFromServer = new DataInputStream(sin);

        try
        {
            // Reads message as entered with keyboard and sends as string
            // via socket connection to server class.
            do
            {
                
                System.out.print("Enter message: ");
                sentence = inFromUser.readLine(); 

                outToServer.writeBytes(sentence + '\n');
                
                if(sentence.equals("CLOSE")) {
                    clientSocket.close();
                    System.out.println("Client socket closed");
                    System.exit(0);
                }
                
                System.out.println("Waiting for server response...");

                modifiedSentence = inFromServer.readUTF();

                System.out.println("FROM SERVER: " + modifiedSentence); 
      
            } while(!sentence.equals("CLOSE"));
            
  } catch(IOException e) {}
   
        clientSocket.close();    
    } 
}  

