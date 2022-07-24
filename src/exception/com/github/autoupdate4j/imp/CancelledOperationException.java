package com.github.autoupdate4j.imp;

import java.io.IOException;

public class CancelledOperationException extends IOException {
  private static final long serialVersionUID = 1L;

  public CancelledOperationException(Exception e) {
    super(e);
  }
}
