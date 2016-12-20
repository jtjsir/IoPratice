package com.jing.io.bio;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * @author jingsir
 **
 * 
 */
public class BasicIoClient {

	private String ip;

	private int bindPort;

	private Socket socket;

	private String charsetName = "utf-8";

	public BasicIoClient() {
		init();
	}

	private void init() {
		ip = "127.0.0.1";
		bindPort = 8888;
		try {
			socket = new Socket(ip, bindPort);
			socket.setTcpNoDelay(true);
			// socket.setSoTimeout(10 * 1000);
			socket.setKeepAlive(true);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private Runnable clientRun = new Runnable() {

		@Override
		public void run() {
			// while (true) {
			OutputStream out = null;
			InputStream in = null;
			try {
				out = socket.getOutputStream();
				in = socket.getInputStream();

				write(out);

				read(in);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					out.close();
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			// }
		}
	};

	/**
	 * 读操作
	 * 
	 * @param client
	 * @throws Exception
	 */
	private void read(InputStream client) throws Exception {
		byte[] data = new byte[1024];
		int readCount = client.read(data);
		if (readCount > 0) {
			System.err.println("服务端发过来的数据为: " + new String(data, 0, readCount));

		}
	}

	/**
	 * 写操作
	 * 
	 * @param client
	 * @throws Exception
	 */
	private void write(OutputStream client) throws Exception {
		PrintWriter writer = new PrintWriter(client);

		writer.println("hello server!");
		writer.flush();
	}

	public void clientStart() {
		Thread client = new Thread(clientRun, "BasicIoClient");
		client.start();
	}

	public static void main(String[] args) {
		BasicIoClient ioClient = new BasicIoClient();
		ioClient.clientStart();
	}
}
