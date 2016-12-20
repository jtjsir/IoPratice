package com.jing.io.bio;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author jingsir
 **
 *         最基本的IO原型，即同步阻塞IO,读写应该采用PrintWriter/字节流读取
 *         
 *         
 */
public class BasicIoServer {

	private ServerSocket serverSocket;

	private int port;

	public BasicIoServer() {
		port = 8888;
		init();
	}

	private void init() {
		try {
			serverSocket = new ServerSocket(port);
			// serverSocket.setSoTimeout(timeout);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

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
			System.err.println("客户端发过来的数据为: " + new String(data, 0, readCount));

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

		writer.println("hello client!");
		writer.flush();

		// writer.close();
	}

	// 读写操作
	private Runnable serverRun = new Runnable() {
		@Override
		public void run() {
			while (true) {
				Socket client = null;
				InputStream in = null;
				OutputStream out = null;
				try {
					// 在此会阻塞,阻塞时间已设置
					client = serverSocket.accept();

					in = client.getInputStream();
					out = client.getOutputStream();

					read(in);

					write(out);
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
			}
		}
	};

	public void serverStart() {
		Thread server = new Thread(serverRun, "BasicIoServer");
		server.start();
	}

	public static void main(String[] args) {
		BasicIoServer ioServer = new BasicIoServer();
		ioServer.serverStart();
	}
}
