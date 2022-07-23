package com.github.autoupdate4j.imp;

import java.io.IOException;

public class UnavailableUpdateException extends IOException {
  private static final long serialVersionUID = 1L;

  public UnavailableUpdateException() {
    super("Unavailable update");
  }
}
