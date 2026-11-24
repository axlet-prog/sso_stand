package com.example.jsso.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UIController {

	private final String frontendUrl;

	public UIController(@Value("${app.frontend-url:}") String frontendUrl) {
		this.frontendUrl = frontendUrl.replaceAll("/+$", "");
	}

	@GetMapping("/ui-test")
	public String uitest(HttpServletRequest request) {
		return page("/ui-test", request);
	}

	@GetMapping("/login")
	public String index(HttpServletRequest request) {
		return page("/login", request);
	}

	private String page(String path, HttpServletRequest request) {
		if (frontendUrl.isBlank()) {
			return "index";
		}
		String query = request.getQueryString();
		return "redirect:" + frontendUrl + path + (query == null ? "" : "?" + query);
	}
}
