package ysng;

import java.util.concurrent.ForkJoinPool;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.context.request.async.DeferredResult;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class AsyncServletController {

	@GetMapping("/async-deferred-result")
	public DeferredResult<ResponseEntity<?>> handleRequestDeferredResult(Model model) {
		log.info("Now received async-deferred-result request...");
		DeferredResult<ResponseEntity<?>> output = new DeferredResult<>();

		try (ForkJoinPool forkJoinPool = ForkJoinPool.commonPool()) {
			forkJoinPool.submit(() -> {
				log.info("Now processing in separate thread...");
				try {
					Thread.sleep(6000);
				} catch (InterruptedException ignored) {
				}
				output.setResult(ResponseEntity.ok("OK!!"));
			});
		}

		for (int i = 0; i < 4; i++) {
			log.info("Servlet thread freed...");
		}

		return output;
	}

}
