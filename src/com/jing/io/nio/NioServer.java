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
 * Reactorģʽ
 * 
 * @author jingsir
 **
 *         Selector��ע����¼���Channel�ǰ󶨵ģ�һ���߳���ɶ��������ӵ�ע���¼�����ѯ����ѯ�����¼���Ҫͬ������,
 *         ���ڿͻ��˵������Լ�������������Ƿ�������
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
		// ������
		serverSocketChannel.configureBlocking(false);
		// �󶨶˿�
		serverSocketChannel.socket().bind(new InetSocketAddress(port));

		// ע������¼�
		serverSelector = Selector.open();

		serverSocketChannel.register(serverSelector, SelectionKey.OP_ACCEPT);
	}

	private void listen() throws Exception {
		while (true) {
			// �˷���Ϊ����,����ע���¼��������������ͨ�����ӵ�������ע����¼�
			serverSelector.select();

			Set<SelectionKey> keys = serverSelector.selectedKeys();
			for (SelectionKey key : keys) {
				handleRequest(key);
			}
			// ɾ��key��ֹ�ظ�
			keys.clear();
		}
	}

	private void handleRequest(SelectionKey key) {
		ServerSocketChannel serverChannel = null;
		SocketChannel clientChannel = null;
		if (key.isValid()) {
			// �׽������Ӵ���
			if (key.isAcceptable()) {
				serverChannel = (ServerSocketChannel) key.channel();
				try {
					clientChannel = serverChannel.accept();
					clientChannel.configureBlocking(false);
					// ע����¼�����д����ͨ��clientChannelͨ������ɵ�
					clientChannel.register(serverSelector, SelectionKey.OP_READ);
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else if (key.isReadable()) {
				clientChannel = (SocketChannel) key.channel();
				ByteBuffer receiveBuf = ByteBuffer.allocate(1024);
				// ������Ϊ��������
				try {
					int count = clientChannel.read(receiveBuf);
					if (count > 0) {
						System.err.println("�ͻ��˷�������ϢΪ: " + new String(receiveBuf.array(), 0, count));
						clientChannel.register(serverSelector, SelectionKey.OP_WRITE);
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else if (key.isWritable()) {
				clientChannel = (SocketChannel) key.channel();
				ByteBuffer sendBuf = ByteBuffer.allocate(1024);

				sendBuf.put("hello client!".getBytes());
				// position��Ϊ0����д����ʹ��
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
