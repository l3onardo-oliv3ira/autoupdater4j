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
import com.github.progress4j.imp.ProgressStatus;
import com.github.progress4j.imp.Stage;
import com.github.utils4j.IDownloader;
import com.github.utils4j.imp.Args;
import com.github.utils4j.imp.DownloadStatus;
import com.github.utils4j.imp.DownloaderAware;

import io.reactivex.disposables.Disposable;

final class Download extends DownloaderAware implements Command {

  private final String uri;
  
  private final File output;

  Download(IDownloader downloader, String uri, File output) {
    super(downloader);
    this.uri = Args.requireText(uri, "uri is empty or null");
    this.output = Args.requireNonNull(output, "output is null");
  }

  @Override
  public final String toString() {
    return "DOWNLOAD " + uri + " TO " + output;
  }

  @Override
  public final void run(IProgressView progress) throws IOException {
    
    Disposable ticket = downloader.newRequest().subscribe(req -> {
      progress.cancelCode(req::abort);
    });

    File temp;
    try {
      DownloadStatus status = new ProgressStatus(
        progress, 
        new Stage("Downloading url: " + uri)
      );
      
      downloader.download(uri, status);
      
      temp = status.getDownloadedFile().orElseThrow(() -> 
        new IOException("Arquivo vazio (length: 0): " + uri)
      );
      
    } finally {
      ticket.dispose();
    }    
    
    try {
      new Copy(temp, output).run(progress);
    }finally {
      temp.delete();
    }
  }
}
