import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.*;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.File;
import java.nio.file.*;
import java.io.DataInputStream;
public class client implements Runnable 
{
	BufferedReader br1, br2;
	PrintWriter pr1;
	Socket socket;
	Thread t1, t2;
	String in = "", out = "";
	public client()
	{
		try
		{
			t1 = new Thread(this);
			t2 = new Thread(this);
			socket = new Socket("localhost", 5000);
			t1.start();;
			t2.start();
		}
		catch (Exception e)
		{
		}
	}
	public void run()
	{
		try
		{
			if (Thread.currentThread() == t2) 
			{
				while(true)
				{
					br1 = new BufferedReader(new InputStreamReader(System.in));
					pr1 = new PrintWriter(socket.getOutputStream(), true);
					in = br1.readLine();
					if (in.contains("files"))
					{
						String[] splited = in.split("\\s+");
						byte[] allBytes = Files.readAllBytes(Paths.get(splited[1]));
						File file = new File(splited[1]);
						pr1.println(in+" "+file.length());
						DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
						dos.write(allBytes);
					}
					else
					{
						pr1.println(in);
						if (in.equals("END") || out.equals("END"))
							socket.close();
					}
				}
			} 
			else
			{
				while(true)
				{
					br2 = new BufferedReader(new   InputStreamReader(socket.getInputStream()));
					out = br2.readLine();
					if (out.contains("files"))
					{
						String[] splited = out.split("\\s+");
						FileOutputStream fos = new FileOutputStream("recfrmserver"+splited[1]);
						byte[] b = new byte[4096];
						int remaining = Integer.parseInt(splited[2]);
						int read=0;
						DataInputStream dis = new DataInputStream(socket.getInputStream());
						while((read = dis.read(b, 0, Math.min(b.length, remaining))) > 0)
						{
							remaining-=read;
							fos.write( b, 0 , read);
						}
					}
					else
					{
						//System.out.println("down");
						System.out.println("Server says : : : " + out);
						if (in.equals("END")|| out.equals("END"))
							socket.close();
					}
				}
			}
		}
		catch (Exception e) 
		{
		}
	}
	public static void main(String[] args) 
	{
		new client();
	}
}
