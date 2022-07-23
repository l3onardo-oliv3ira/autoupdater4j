package com.github.autoupdate4j.imp;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

import com.github.autoupdate4j.IFingerPrint;
import com.github.autoupdate4j.IScanner;
import com.github.progress4j.IProgress;
import com.github.utils4j.IDownloader;
import com.github.utils4j.imp.Args;
import com.github.utils4j.imp.DownloadStatus;
import com.github.utils4j.imp.DownloaderAware;

final class HttpScanner extends DownloaderAware implements IScanner {

  private final String rootUri;

  public HttpScanner(IDownloader downloader, String rootUri) {
    super(downloader);
    this.rootUri = Args.requireNonNull(rootUri, "rootUri is null");
  }
  
  @Override
  public IFingerPrint scan(IProgress progress) throws IOException {
    DownloadStatus status = new DownloadStatus();
    try {
      downloader.download(rootUri, status);
    } catch (IOException e) {
      throw new DownloadFailException(rootUri, e);
    }
    
    Optional<File> of  = status.getDownloadedFile();
    if (!of.isPresent()) {
      throw new UnavailableUpdateException();
    }
    
    final File file = of.get();
    
    try {
      return new RemoteFingerPrint(downloader, file);
    } catch (IOException e) {
      throw new FingerPrintLoadException(e);
    }
  }
}
