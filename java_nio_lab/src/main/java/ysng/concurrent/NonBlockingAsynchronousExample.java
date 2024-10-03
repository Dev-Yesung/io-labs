package ysng.concurrent;

import java.util.concurrent.CompletableFuture;

public class NonBlockingAsynchronousExample {

	public static void main(String[] args) {
		System.out.println("Non-Blocking Asynchronous 동작 시작...");
		CompletableFuture<Void> future = CompletableFuture.supplyAsync(() -> {
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			System.out.println("동작이 끝났기 때문에 콜백에 결과를 전달합니다...");
			return "하이하이염 완료되었답니다~";
		}).thenAccept((result) -> {
			System.out.println("결과를 콜백으로 처리합니다...");
			System.out.println("결과 : " + result);
		});
		System.out.println("Non-Blocking Asynchronous 동작 끝...");

		future.join();
	}
}
