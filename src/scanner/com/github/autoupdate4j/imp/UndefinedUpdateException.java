package com.github.autoupdate4j.imp;

import java.io.IOException;

public class UndefinedUpdateException extends IOException {
  private static final long serialVersionUID = 1L;

  public UndefinedUpdateException(String message, Exception cause) {
    super(message, cause);
  }
}
