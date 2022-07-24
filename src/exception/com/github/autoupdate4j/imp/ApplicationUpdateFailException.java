package com.github.autoupdate4j.imp;

import java.io.IOException;

public class ApplicationUpdateFailException extends IOException {
  private static final long serialVersionUID = 1L;

  ApplicationUpdateFailException(Exception cause) {
    super(cause);
  }
}
