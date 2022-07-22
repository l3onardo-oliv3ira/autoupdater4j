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

import io.reactivex.disposables.Disposable;

final class Download extends Command {

  private final String url;
  
  private final File output;

  private final IDownloader downloader;

  private final IProgressView progress;
  
  Download(IProgressView progress, IDownloader downloader, String url, File output) {
    this.url = Args.requireText(url, "url is empty or null");
    this.output = Args.requireNonNull(output, "output is null");
    this.progress = Args.requireNonNull(progress, "progress is null");
    this.downloader = Args.requireNonNull(downloader, "downloader is null");
  }

  @Override
  public final String toString() {
    return "DOWNLOAD " + url + " TO " + output;
  }

  @Override
  public final void execute() throws IOException {
    Disposable ticket = downloader.newRequest().subscribe(req -> {
      progress.cancelCode(req::abort);
    });

    File input;
    try {
      DownloadStatus status = new ProgressStatus(
        progress, 
        new Stage("Downloading url: " + url)
      );
      
      downloader.download(url, status);
      
      input = status.getDownloadedFile().orElseThrow(() -> 
        new IOException("Arquivo vazio (length: 0): " + url)
      );
      
    } finally {
      ticket.dispose();
    }    
    
    try {
      new Copy(input, output).execute();
    }finally {
      input.delete();
    }
  }
}
