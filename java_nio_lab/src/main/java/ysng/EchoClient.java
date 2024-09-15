package ysng;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Scanner;
import java.util.logging.Logger;

class EchoClient {

	private static final Logger log = Logger.getLogger(EchoClient.class.getName());

	public static void main(String[] args) throws IOException {
		try (SocketChannel client = SocketChannel.open(new InetSocketAddress("localhost", 8080))) {
			log.info("서버와 연결되었습니다.");
			log.info("Server: " + client.getRemoteAddress());

			Scanner scanner = new Scanner(System.in);
			while (true) {
				log.info("서버에 전달할 문자열을 입력해주세요 : ");
				String textLine = scanner.nextLine();
				ByteBuffer buffer = ByteBuffer.wrap(textLine.getBytes());

				client.write(buffer);
				buffer.clear();
				client.read(buffer);
				String echoResponse = new String(buffer.array()).trim();
				log.info("서버로부터의 응답 : " + echoResponse);

				if ("EXIT".equals(echoResponse)) {
					log.info("서버와의 연결을 종료합니다...");
					client.close();
					break;
				}

				buffer.clear();
			}
		}
	}
}
