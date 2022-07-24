package com.github.autoupdate4j.imp;

import java.io.IOException;

public class FingerPrintLoadingException extends IOException {
  private static final long serialVersionUID = 1L;

  public FingerPrintLoadingException(IOException cause) {
    super(cause);
  }
}
