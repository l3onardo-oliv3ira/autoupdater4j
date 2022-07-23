package com.github.autoupdate4j.imp;

import java.io.IOException;

public class FingerPrintLoadException extends IOException {
  private static final long serialVersionUID = 1L;

  public FingerPrintLoadException(IOException cause) {
    super(cause);
  }
}
