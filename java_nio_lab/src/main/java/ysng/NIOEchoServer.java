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
		ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
		int port = 8080;
		if (args.length != 0 && !args[0].isBlank()) {
			port = Integer.parseInt(args[0]);
		}
		serverSocketChannel.bind(new InetSocketAddress("localhost", port));
		// Non-Blocking I/O로 동작해야하므로 false
		serverSocketChannel.configureBlocking(false);

		Selector selector = Selector.open();
		// 클라이언트와 연결된 소켓에 추가적인 설정을 진행해야 하므로
		// ServerSocketChannel을 등록할 때 이벤트 타입을 SelectionKey.OP_ACCEPT로 설정
		serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

		ByteBuffer buffer = ByteBuffer.allocate(256);

		while (true) {
			// 무한 반복문을 통해 셀렉터에게 데이터가 준비된 채널이 있는지 확인
			selector.select();
			// 셀렉터에 채널을 등록하는 즉시, 채널의 상태를 모니터링 할 수 있는 셀렉션 키를 반환
			// 셀렉터를 통해 준비된 채널들의 정보를 셀렉션 키를 통해 반환받음
			Set<SelectionKey> selectedKeys = selector.selectedKeys();
			// 무한 반복문을 통해 준비된 채널을 감지
			Iterator<SelectionKey> iterator = selectedKeys.iterator();
			while (iterator.hasNext()) {
				SelectionKey key = iterator.next();
				iterator.remove();

				// 발생한 이벤트에 따라 분기 처리해 필요한 로직을 수행합니다.
				if (key.isAcceptable()) {
					register(selector, serverSocketChannel);
				}
				// 전송한 데이터가 커널 영역에서 유저 영역으로 복사됨
				// 이는 전송한 데이터를 읽을 준비가 된 것이기 때문에,
				// ServerSocketChannel가 I/O를 수행하기 위해 준비되었음을 의미
				if (key.isReadable()) {
					answerWithEcho(buffer, key);
				}
			}
		}
	}

	private static void register(Selector selector, ServerSocketChannel serverSocketChannel) throws IOException {
		// 셀렉터에 등록된 ServerSocketChannel의 ServetSocket이 클라이언트의 연결 요청을 감지하고 수락
		// ServerSocket이 클라이언트의 연결 요청을 수락했다는 의미는 ServerSocket을 통해
		// I/O를 하기 위한 데이터가 준비되었음을 감지(클라이언트의 소켓 연결 요청 데이터 세팅)했음을 의미
		SocketChannel client = serverSocketChannel.accept();
		// 클라이언트와 연결된 SocketChannel을 Non-Blocking I/O로 설정합니다.
		client.configureBlocking(false);
		// 클라이언트에서 전송한 데이터를 조회해야하므로 SocketChannel을 셀렉터에 등록할 때
		// 이벤트 타입을 SelectionKey.OP_READ로 설정
		client.register(selector, SelectionKey.OP_READ);

		log.info("클라이언트와 연결되었습니다.");
		log.info("Client :  " + client.getRemoteAddress());
	}

	private static void answerWithEcho(ByteBuffer buffer, SelectionKey key) throws IOException {
		// 클라이언트가 전달한 데이터를 버퍼로 유저 영역에서 읽어옴
		SocketChannel client = (SocketChannel)key.channel();
		int readBytes = client.read(buffer);
		String clientInput = new String(buffer.array()).trim();

		// 데이터를 토대로 클라이언트의 요청을 처리
		if (readBytes == -1 || "EXIT".equals(clientInput)) {
			log.info("Client(" + client.getRemoteAddress() + ")와의 연결을 종료합니다.");
			client.close();
			return;
		}

		// 하나의 버퍼를 양방향으로 쓰기 위해, flip()을 활용해 모드를 변경해주어야 함
		buffer.flip();
		// 그 결과를 버퍼에 write해 클라이언트에게 데이터를 전달
		client.write(buffer);
		// 버퍼 클리어
		buffer.clear();
	}
}
