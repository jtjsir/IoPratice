package com.jing.io.aio.handler;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

/**
 * @author jingsir
 **
 * �ͻ��˶�ȡ�¼�
 */
public class AioClientReadHandler implements CompletionHandler<Integer, ByteBuffer> {

	private AsynchronousSocketChannel socketChannel;

	public AioClientReadHandler(AsynchronousSocketChannel socketChannel) {
		this.socketChannel = socketChannel;
	}

	@Override
	public void completed(Integer arg0, ByteBuffer buffer) {
		System.err.println("��ȡ�� " + arg0 + " ���ֽ�");
		buffer.flip();
		byte[] data = new byte[buffer.remaining()];
		try {
			System.err.println("���յ��ķ���˵���ϢΪ: " + new String(data, "UTF-8"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		// write����
		ByteBuffer src = ByteBuffer.allocate(1024);
		src.put("hello server".getBytes());
		src.flip();
		socketChannel.write(src, src, new AioClientWriteHandler(socketChannel));
	}

	@Override
	public void failed(Throwable arg0, ByteBuffer arg1) {
		arg0.printStackTrace();
	}

}
