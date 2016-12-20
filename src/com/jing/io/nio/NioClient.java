package com.jing.io.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Set;

/**
 * @author jingsir
 **
 * 
 */
public class NioClient {

	private SocketChannel socketChannel;
	private Selector clientSelector;
	private String host;
	private int port;

	public NioClient(String host, int port) {
		this.host = host;
		this.port = port;
		try {
			socketChannel = SocketChannel.open();
			socketChannel.configureBlocking(false);
			// socketChannel.socket().bind(new InetSocketAddress(host, port));

			clientSelector = Selector.open();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void connect() {
		try {
			socketChannel.register(clientSelector, SelectionKey.OP_CONNECT);
			socketChannel.connect(new InetSocketAddress(host, port));
			handleRequest();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void handleRequest() throws Exception {
		Set<SelectionKey> keys = null;
		SocketChannel client = null;
		while (true) {
			clientSelector.select();
			keys = clientSelector.selectedKeys();
			for (SelectionKey key : keys) {
				if (key.isValid()) {
					// 连接事件处理
					if (key.isConnectable()) {
						client = (SocketChannel) key.channel();
						// 是否正在连接
						if (client.isConnectionPending()) {
							client.finishConnect();
							ByteBuffer sendBuffer = ByteBuffer.allocate(1024);
							sendBuffer.put("hello server".getBytes());
							sendBuffer.flip();
							client.write(sendBuffer);
						}
						client.register(clientSelector, SelectionKey.OP_READ);
					} else if (key.isReadable()) {
						client = (SocketChannel) key.channel();
						ByteBuffer receiveBuf = ByteBuffer.allocate(1024);
						int count = client.read(receiveBuf);
						if (count > 0) {
							System.err.println("服务端发送的消息为: " + new String(receiveBuf.array(), 0, count));
						}
						client.register(clientSelector, SelectionKey.OP_WRITE);
					} else if (key.isWritable()) {
						client = (SocketChannel) key.channel();
						ByteBuffer sendBuffer = ByteBuffer.allocate(1024);
						sendBuffer.put("message to server".getBytes());
						sendBuffer.flip();
						client.write(sendBuffer);
						client.register(clientSelector, SelectionKey.OP_READ);
					}
				}
			}
			keys.clear();
		}
	}

	public static void main(String[] args) {
		NioClient client = new NioClient("127.0.0.1", 8888);
		client.connect();
	}
}
