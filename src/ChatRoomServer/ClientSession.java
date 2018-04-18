package ChatRoomServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

public class ClientSession implements Runnable {
	ServerMain server;

	BufferedReader in;
	PrintWriter out;

	String username;
	final String LOGOUT = "/logout";

	ClientSession(BufferedReader _in, PrintWriter _out, ServerMain _server) throws IOException {

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
				if (inputMessage.equals(LOGOUT))
					running = false;
				else {
					server.Send(inputMessage, this);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();

		} finally {
			server.removeMyReference(this);
			System.out.println(username + " disconnected");
			server.Send("/logout", this);
			System.out.println(ServerMain.clientSessions.size() + " people on the server");
		}
	}

	public void Send(String message) {
		out.println(message);
		out.flush();
	}

}
