package com.github.autoupdate4j.imp;

import java.io.IOException;

public class HashCheckingException extends IOException {
  private static final long serialVersionUID = 1L;

  public HashCheckingException(String message, Exception cause) {
    super(message, cause);
  }
}
