package powerup.network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import powerup.engine.Util;

public class GameServer {
	
	public static void main(String[] args) {
		GameServer server = new GameServer();
		int listenPort = 9001;
		try {
			server.start(listenPort);
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	public void move(int command) {
		
	}
	
	private void start(int listenPort) throws SocketException {
		boolean listen = true;
		
		DatagramSocket socket = new DatagramSocket(listenPort);
		System.out.println("GameServer.start listening on "+listenPort);
		
		while (listen) {
			byte[] buf = new byte[256];
			DatagramPacket packet = new DatagramPacket(buf, buf.length);
			try {
				socket.receive(packet);
				String received = new String(packet.getData(), 0, packet.getLength());
				Util.log("GameServer.start received packet s:"+received);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			//InetAddress address = packet.getAddress();
			//int port = packet.getPort();
			//packet = new DatagramPacket(buf, buf.length, address, port);
			//socket.send(packet);
			
		}
		socket.close();
	}
}
