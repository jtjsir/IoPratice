package com.jing.io.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Set;

/**
 * Reactor模式
 * 
 * @author jingsir
 **
 *         Selector上注册的事件与Channel是绑定的，一个线程完成对所有连接的注册事件的轮询；轮询到的事件需要同步进行,
 *         对于客户端的连接以及读操作则可以是非阻塞的
 */
public class NioServer {

	private Selector serverSelector;

	public NioServer(int port) {
		try {
			init(port);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void init(int port) throws Exception {
		ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
		// 非阻塞
		serverSocketChannel.configureBlocking(false);
		// 绑定端口
		serverSocketChannel.socket().bind(new InetSocketAddress(port));

		// 注册接收事件
		serverSelector = Selector.open();

		serverSocketChannel.register(serverSelector, SelectionKey.OP_ACCEPT);
	}

	private void listen() throws Exception {
		while (true) {
			// 此方法为阻塞,返回注册事件个数，包含多个通道连接到本服务注册的事件
			serverSelector.select();

			Set<SelectionKey> keys = serverSelector.selectedKeys();
			for (SelectionKey key : keys) {
				handleRequest(key);
			}
			// 删除key防止重复
			keys.clear();
		}
	}

	private void handleRequest(SelectionKey key) {
		ServerSocketChannel serverChannel = null;
		SocketChannel clientChannel = null;
		if (key.isValid()) {
			// 套接字连接处理
			if (key.isAcceptable()) {
				serverChannel = (ServerSocketChannel) key.channel();
				try {
					clientChannel = serverChannel.accept();
					clientChannel.configureBlocking(false);
					// 注册读事件，读写都是通过clientChannel通道来完成的
					clientChannel.register(serverSelector, SelectionKey.OP_READ);
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else if (key.isReadable()) {
				clientChannel = (SocketChannel) key.channel();
				ByteBuffer receiveBuf = ByteBuffer.allocate(1024);
				// 读操作为阻塞操作
				try {
					int count = clientChannel.read(receiveBuf);
					if (count > 0) {
						System.err.println("客户端发来的消息为: " + new String(receiveBuf.array(), 0, count));
						clientChannel.register(serverSelector, SelectionKey.OP_WRITE);
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else if (key.isWritable()) {
				clientChannel = (SocketChannel) key.channel();
				ByteBuffer sendBuf = ByteBuffer.allocate(1024);

				sendBuf.put("hello client!".getBytes());
				// position置为0，供写操作使用
				sendBuf.flip();
				try {
					clientChannel.write(sendBuf);
					clientChannel.register(serverSelector, SelectionKey.OP_READ);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static void main(String[] args) throws Exception {
		NioServer server = new NioServer(8888);
		server.listen();
	}
}
