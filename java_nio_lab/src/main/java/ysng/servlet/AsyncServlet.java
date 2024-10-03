package ysng.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import org.springframework.http.MediaType;

import jakarta.servlet.AsyncContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(urlPatterns = "/hello", asyncSupported = true)
public class AsyncServlet extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
		throws ServletException, IOException {
		Thread thread = new Thread(() -> {
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}

			AsyncContext asyncContext = req.getAsyncContext();
			HttpServletResponse response = (HttpServletResponse)asyncContext.getResponse();
			response.setContentType(MediaType.TEXT_PLAIN_VALUE);
			response.setCharacterEncoding("UTF-8");

			try {
				PrintWriter printWriter = response.getWriter();
				printWriter.println("Hello!!!");
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
			System.out.println("Complete response");
			asyncContext.complete();
		});

		thread.start();

		System.out.println("doGet return");
	}
}
