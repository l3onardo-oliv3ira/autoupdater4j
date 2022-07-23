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
import java.util.Optional;

import com.github.autoupdate4j.IFingerPrint;
import com.github.autoupdate4j.IPatch;
import com.github.utils4j.IDownloader;
import com.github.utils4j.imp.Args;

class RemoteFingerPrint extends FingerPrint {

  private final IDownloader downloader;
  
  RemoteFingerPrint(IDownloader downloader, File file) throws IOException {
    this.downloader = Args.requireNonNull(downloader, "downloader is null");
    readFrom(file);
  }

  @Override
  public Optional<IPatch> patch(IFingerPrint to) {
    if (!(to instanceof FingerPrint))
      return Optional.empty();
    
    Patch patch = new Patch();
    
    final FingerPrint target = (FingerPrint)to;

    items.forEach((myHash, myOrigin) -> {
      String otherFile = target.items.get(myHash);
      if (otherFile == null) {        
        patch.remoteDelete(downloader, myOrigin);
      }
    });
    
    target.items.forEach((targetHash, targetFile) -> {
      String myPath = items.get(targetHash);
      if (myPath == null) {
        String output = targetFile;
        if (targetHash.startsWith("directory")) {
          patch.remoteMkdir(downloader, output);
        } else {
          target.remoteUpdate(downloader, patch, targetFile, output);
        } 
      }
    });
     
    return patch.asOptional();
  }
  
  @Override
  void update(Patch patch, String input, File output) {
    patch.download(downloader, input, output);
  }

  @Override
  void remoteUpdate(IDownloader downloader, Patch patch, String input, String output) {
    //download from input and upload to output
    patch.notImplemented("Both remote finger print");
  }
}
