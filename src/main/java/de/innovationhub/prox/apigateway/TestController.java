package de.innovationhub.prox.apigateway;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {
  @GetMapping("/hello")
  private String helloWorld() {
    return "Hello, World!";
  }
}
