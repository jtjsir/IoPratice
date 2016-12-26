package com.jing.io.aio.handler;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

import com.jing.io.aio.AioServer;

/**
 * @author jingsir
 **
 *         �������Ӵ�����	
 *         ��һ������Ϊ�ͻ�������ͨ�����ڶ�������Ϊaccept()�������ĵ�һ����������
 */
public class AioServerAcceptHandler implements CompletionHandler<AsynchronousSocketChannel, AioServer> {

	@Override
	public void completed(AsynchronousSocketChannel socketChannel, AioServer server) {
		AioServer.clientCount++ ;
		System.err.println("Ŀǰ�� " + AioServer.clientCount + " �ͻ��˽���");
		//���ն������
		server.getServerSocketChannel().accept(server,this);
		
		ByteBuffer buffer = ByteBuffer.allocate(1024) ;
		//�첽��
		socketChannel.read(buffer, buffer, new AioServerReadHandler(socketChannel));
	}

	@Override
	public void failed(Throwable exc, AioServer server) {
		exc.printStackTrace();
		try {
			//�ر�����ͨ��
			server.getServerSocketChannel().close();
			server.getLatch().countDown();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
