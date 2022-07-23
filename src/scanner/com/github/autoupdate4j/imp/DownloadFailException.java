package com.github.autoupdate4j.imp;

import java.io.IOException;

public class DownloadFailException extends IOException {
  private static final long serialVersionUID = 1L;
  
  public DownloadFailException(String uri) {
    super("Failed URI: " + uri);
  }
}
