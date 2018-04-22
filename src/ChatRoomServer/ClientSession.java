package ChatRoomServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

public class ClientSession implements Runnable {
	ChatServer server;
	BufferedReader in;
	PrintWriter out;
	String username;

	ClientSession(BufferedReader _in, PrintWriter _out, ChatServer _server) {
		server = _server;
		in = _in;
		out = _out;
		}

	@Override
	public void run() {
		String inputMessage;
		boolean running = true;

		try {
			System.out.println("New client has arrived");

			username = in.readLine();

			while (running) {
				inputMessage = in.readLine();
				System.out.println(username + ": " + inputMessage);
				if (inputMessage.equals(ChatServer.LOGOUT_COMMAND))
					running = false;
				else {
					server.send(inputMessage, this);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();

		} finally {
			server.removeMyReference(this);
			System.out.println(username + " disconnected");
			server.send("/logout", this);
			System.out.println(ChatServer.clientSessions.size() + " people on the server");
		}
	}

	public void sendMessage(String message) {
		out.println(message);
		out.flush();
	}
}