package com.jing.io.aio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.util.concurrent.CountDownLatch;

import com.jing.io.aio.handler.AioServerAcceptHandler;

/**
 * @author jingsir
 **
 *         �첽����������
 */
public class AioServer {

	private AsynchronousServerSocketChannel serverSocketChannel;

	public static int clientCount = 0;

	private CountDownLatch latch;

	public AioServer() {
		try {
			latch = new CountDownLatch(1);
			serverSocketChannel = AsynchronousServerSocketChannel.open();
			serverSocketChannel.bind(new InetSocketAddress(8888));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void start() {
		/**
		 * �첽���� 
		 * ��һ��������ڶ��������󶨵�A���ͱ���һ�� 
		 * ��һ������Ϊ�󶨲���
		 */
		serverSocketChannel.accept(this, new AioServerAcceptHandler());
		try {
			latch.await();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public AsynchronousServerSocketChannel getServerSocketChannel() {
		return serverSocketChannel;
	}

	public void setServerSocketChannel(AsynchronousServerSocketChannel serverSocketChannel) {
		this.serverSocketChannel = serverSocketChannel;
	}

	public CountDownLatch getLatch(){
		return this.latch ;
	}
	
	public static void main(String[] args) {
		AioServer server = new AioServer();
		server.start();
	}
}
