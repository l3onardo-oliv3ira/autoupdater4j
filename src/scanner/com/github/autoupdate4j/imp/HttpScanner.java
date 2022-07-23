package com.github.autoupdate4j.imp;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

import com.github.autoupdate4j.IFingerPrint;
import com.github.autoupdate4j.IScanner;
import com.github.progress4j.IProgressView;
import com.github.utils4j.IDownloader;
import com.github.utils4j.imp.Args;
import com.github.utils4j.imp.DownloadStatus;
import com.github.utils4j.imp.DownloaderAware;

import io.reactivex.disposables.Disposable;

final class HttpScanner extends DownloaderAware implements IScanner {

  private final String fingerPrint;

  public HttpScanner(IDownloader downloader, String fingerPrint) {
    super(downloader);
    this.fingerPrint = Args.requireNonNull(fingerPrint, "rootUri is null");
  }
  
  @Override
  public IFingerPrint scan(IProgressView progress) throws IOException {
    Disposable ticket = downloader.newRequest().subscribe(req -> progress.cancelCode(req::abort));

    File temp;
    try {
      DownloadStatus status = new DownloadStatus();
    
      downloader.download(fingerPrint, status);

      Optional<File> of  = status.getDownloadedFile();
      if (!of.isPresent()) {
        throw new DownloadFailException(downloader.match(fingerPrint));
      }
      
      temp = of.get();
    } catch (IOException e) {
      throw new UndefinedUpdateException("Não foi possível baixar o arquivo de "
          + "impressão digital do servidor de atualização.", e);
    } finally {
      ticket.dispose();
    }
    
    try {
      return new RemoteFingerPrint(downloader, temp);
    } catch (IOException e) {
      throw new FingerPrintLoadException(e);
    } finally {
      temp.delete();
    }
  }
}
