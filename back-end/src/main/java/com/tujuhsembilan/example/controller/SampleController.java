package com.tujuhsembilan.example.controller;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tujuhsembilan.example.exception.MultipleException;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/sample")
public class SampleController {

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  private static class SampleRequestBody {
    @Size(max = 10)
    private String input;

    @NotBlank
    private String mustHaveSomething;
  }

  // Endpoint baru untuk menerima log error dari frontend
  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  private static class ClientError {
    private String error;
    private String errorInfo;
    private String date;
    private String time;
  }

  @PostMapping("/post")
  public ResponseEntity<?> postSomething(@Valid @RequestBody SampleRequestBody body) {
    throw new EntityNotFoundException("Test");
  }

  @GetMapping("/multiple-exception")
  public ResponseEntity<?> multipleException() {
    throw new MultipleException(new EntityNotFoundException("Test1"), new IndexOutOfBoundsException("Test2"));
  }

   @PostMapping("/log-error")
  public ResponseEntity<?> logError(@Valid @RequestBody List<ClientError> errors) {
    for (ClientError error : errors) {
      log.error("Client error: {}", error);
      saveErrorToFile(error);
    }
    return ResponseEntity.ok().build();
  }

  private void saveErrorToFile(ClientError error) {
    try (FileWriter fw = new FileWriter("client_errors.log", true)) {
      fw.write("Date: " + error.getDate() + "\n");
      fw.write("Date: " + error.getTime() + "\n");
      fw.write("Error: " + error.getError() + "\n");
      fw.write("Error Info: " + error.getErrorInfo() + "\n\n");
    } catch (IOException e) {
      log.error("Error writing to log file", e);
    }
  }

}
