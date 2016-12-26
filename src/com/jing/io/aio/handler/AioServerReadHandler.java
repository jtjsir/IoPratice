package com.jing.io.aio.handler;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

/**
 * @author jingsir
 **
 *         server�˶��¼�
 */
public class AioServerReadHandler implements CompletionHandler<Integer, ByteBuffer> {

	private AsynchronousSocketChannel socketChannel;

	public AioServerReadHandler(AsynchronousSocketChannel channel) {
		this.socketChannel = channel;
	}

	//attchmentΪϵͳ���ظ��û��̵߳Ļ�����������ȡ�����ֽ�
	@Override
	public void completed(Integer result, ByteBuffer attachment) {
		System.err.println("�Ѿ���ȡ�� " + result + " ���ֽ�");
		// ���¶�ȡ
		attachment.flip();
		byte[] data = new byte[attachment.remaining()];
		try {
			attachment.get(data);
			System.err.println("�ͻ��˷��͵���ϢΪ: " + new String(data, "UTF-8"));
			// д��һ������Ϣ
			doWrite();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void doWrite() {
		ByteBuffer sendBuf = ByteBuffer.allocate(1024);
		sendBuf.put("hello client".getBytes());
		sendBuf.flip();
		// �첽д
		this.socketChannel.write(sendBuf, sendBuf, new AioServerWriteHandler(socketChannel));
	}

	@Override
	public void failed(Throwable exc, ByteBuffer attachment) {
		exc.printStackTrace();
	}

}
