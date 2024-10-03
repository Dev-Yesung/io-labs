package ysng.concurrent;

public class BlockingSynchronousExample {

	public static void main(String[] args) {
		System.out.println("Blocking-Synchronous 동작 시작");
		String result = blockingSynchronousOperation();
		System.out.println("결과 값 : " + result);
		System.out.println("Blocking-Synchronous 동작 끝");
	}

	private static String blockingSynchronousOperation() {
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return "동작 완료!!";
	}
}
