package com.jing.io.aio.handler;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

import com.jing.io.aio.AioServer;

/**
 * @author jingsir
 **
 *         接收连接处理类	
 *         第一个参数为客户端连接通道，第二个参数为accept()方法传的第一个参数类型
 */
public class AioServerAcceptHandler implements CompletionHandler<AsynchronousSocketChannel, AioServer> {

	@Override
	public void completed(AsynchronousSocketChannel socketChannel, AioServer server) {
		AioServer.clientCount++ ;
		System.err.println("目前有 " + AioServer.clientCount + " 客户端接入");
		//接收多个请求
		server.getServerSocketChannel().accept(server,this);
		
		ByteBuffer buffer = ByteBuffer.allocate(1024) ;
		//异步读
		socketChannel.read(buffer, buffer, new AioServerReadHandler(socketChannel));
	}

	@Override
	public void failed(Throwable exc, AioServer server) {
		exc.printStackTrace();
		try {
			//关闭连接通道
			server.getServerSocketChannel().close();
			server.getLatch().countDown();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
