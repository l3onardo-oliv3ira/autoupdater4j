/*
* MIT License
* 
* Copyright (c) 2022 Leonardo de Lima Oliveira
* 
* https://github.com/l3onardo-oliv3ira
* 
* Permission is hereby granted, free of charge, to any person obtaining a copy
* of this software and associated documentation files (the "Software"), to deal
* in the Software without restriction, including without limitation the rights
* to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
* copies of the Software, and to permit persons to whom the Software is
* furnished to do so, subject to the following conditions:
* 
* The above copyright notice and this permission notice shall be included in all
* copies or substantial portions of the Software.
* 
* THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
* IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
* FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
* AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
* LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
* OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
* SOFTWARE.
*/

package com.github.autoupdate4j.imp;

import java.io.File;
import java.io.IOException;

import com.github.progress4j.IProgressView;
import com.github.utils4j.IDownloader;
import com.github.utils4j.imp.Args;
import com.github.utils4j.imp.DownloadStatus;
import com.github.utils4j.imp.DownloaderAware;
import com.github.utils4j.imp.Threads;

import io.reactivex.disposables.Disposable;

final class Download extends DownloaderAware implements Command {

  private final String uri;
  
  private final File output;

  private String hash;

  Download(IDownloader downloader, String uri, File output, String hash) {
    super(downloader);
    this.uri = Args.requireText(uri, "uri is empty or null");
    this.output = Args.requireNonNull(output, "output is null");
    this.hash = Args.requireNonNull(hash, hash);
  }

  @Override
  public final String toString() {
    return "DOWNLOAD " + uri + " TO " + output;
  }

  @Override
  public final void handle(IProgressView progress) throws IOException {
    int attempt = 0;
    do {
    
      Disposable ticket = downloader.newRequest().subscribe(req -> progress.cancelCode(req::abort));
  
      File temp;
      try {
        DownloadStatus status = new DownloadStatus(false);
  
        downloader.download(uri, status);
      
        temp = status.getDownloadedFile().orElseThrow(() -> new IOException("Não foi possível baixar o arquivo: " + uri));
      } finally {
        ticket.dispose();
      }    
      
      try {
        new Copy(temp, output, hash).handle(progress);
        
        break;
      } catch (HashCheckingException e) {

        if (++attempt >= 3)
          throw e;
        Threads.sleep(2000);

      } finally {
        temp.delete();
      }
      
    } while(true);
  }
}
