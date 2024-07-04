package kg.bitruby.authservice.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/public")
public class PublicController {

  @PostMapping("/check")
  public ResponseEntity<String> check() {
    return new ResponseEntity<>("OK", HttpStatus.OK);
  }
}
