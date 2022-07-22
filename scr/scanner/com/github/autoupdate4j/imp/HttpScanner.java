package com.github.autoupdate4j.imp;

import java.io.IOException;

import com.github.autoupdate4j.IFingerPrint;
import com.github.autoupdate4j.IScanner;
import com.github.progress4j.IProgress;
import com.github.utils4j.imp.Args;

final class HttpScanner implements IScanner {

  private final String rootUri;

  public HttpScanner(String rootUri) {
    this.rootUri = Args.requireNonNull(rootUri, "rootUri is null");
  }
  
  @Override
  public IFingerPrint scan(IProgress progress) throws IOException {
    return null;
  }
}
