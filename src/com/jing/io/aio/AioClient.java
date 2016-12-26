package com.jing.io.aio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.CountDownLatch;

import com.jing.io.aio.handler.AioClientConnectHandler;

/**
 * @author jingsir
 **
 * 
 */
public class AioClient {

	private AsynchronousSocketChannel socketChannel;

	private CountDownLatch latch;

	public AioClient() {
		try {
			latch = new CountDownLatch(1);
			socketChannel = AsynchronousSocketChannel.open();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void start() {
		try {
			socketChannel.connect(new InetSocketAddress("127.0.0.1", 8888), this,
					new AioClientConnectHandler(socketChannel));
			latch.await();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public CountDownLatch getLatch() {
		return this.latch;
	}

	public static void main(String[] args) {
		AioClient client = new AioClient();
		client.start();
	}

}
