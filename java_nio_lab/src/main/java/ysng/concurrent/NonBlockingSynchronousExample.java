package ysng.concurrent;

class NonBlockingSynchronousExample {

	public static void main(String[] args) {
		System.out.println("Non-Blocking Synchronous 동작 시작...");
		nonBlockingSynchronousOperation();
		System.out.println("Non-Blocking Synchronous 동작 끝...");
	}

	private static void nonBlockingSynchronousOperation() {
		new Thread(() -> {
			try {
				Thread.sleep(10000);
				System.out.println("Non-Blocking 동작 끝!!");
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}).start();
	}
}
