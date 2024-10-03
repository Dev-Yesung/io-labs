package ysng.concurrent;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

class BlockingAsynchronousExample {

	public static void main(String[] args) {
		ExecutorService executorService = Executors.newSingleThreadExecutor();

		System.out.println("Blocking-Asynchronous 동작 시작...");
		Future<String> futureResult = executorService.submit(() -> {
			try {
				// Asynchronous 작업
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			System.out.println("작업 완료됨~!");
			return "작업이 완료되었습니다!!";
		});

		try {
			System.out.println("Blocking 되기 전...");
			String result = futureResult.get();
			System.out.println("Blocking 풀린 후...");
		} catch (InterruptedException | ExecutionException e) {
			throw new RuntimeException(e);
		}

		executorService.shutdown();
		System.out.println("Blocking-Asynchronous 동작 끝...");
	}
}
