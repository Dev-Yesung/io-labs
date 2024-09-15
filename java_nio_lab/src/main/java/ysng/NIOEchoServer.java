package ysng;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Logger;

class NIOEchoServer {

	private static final Logger log = Logger.getLogger(NIOEchoServer.class.getName());

	public static void main(String[] args) throws IOException {
		Selector selector = Selector.open();
		ServerSocketChannel server = ServerSocketChannel.open();
		server.bind(new InetSocketAddress("localhost", 8080));
		server.configureBlocking(false);
		server.register(selector, SelectionKey.OP_ACCEPT);

		ByteBuffer buffer = ByteBuffer.allocate(256);

		while (true) {
			selector.select();
			Set<SelectionKey> selectedKeys = selector.selectedKeys();
			Iterator<SelectionKey> iterator = selectedKeys.iterator();
			while (iterator.hasNext()) {
				SelectionKey key = iterator.next();
				iterator.remove();

				if (key.isAcceptable()) {
					register(selector, server);
				}
				if (key.isReadable()) {
					answerWithEcho(buffer, key);
				}
			}
		}
	}

	private static void register(Selector selector, ServerSocketChannel serverSocketChannel) throws IOException {
		SocketChannel client = serverSocketChannel.accept();
		client.configureBlocking(false);
		client.register(selector, SelectionKey.OP_READ);
		log.info("클라이언트와 연결되었습니다.");
		log.info("Client :  " + client.getRemoteAddress());
	}

	private static void answerWithEcho(ByteBuffer buffer, SelectionKey key) throws IOException {
		SocketChannel client = (SocketChannel)key.channel();
		int readBytes = client.read(buffer);
		String clientInput = new String(buffer.array()).trim();

		if (readBytes == -1 || "EXIT".equals(clientInput)) {
			log.info("Client(" + client.getRemoteAddress() + ")와의 연결을 종료합니다.");
			client.close();
			return;
		}

		buffer.flip();
		client.write(buffer);
		buffer.clear();
	}
}
