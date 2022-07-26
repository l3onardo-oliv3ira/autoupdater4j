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

import static com.github.utils4j.imp.Streams.sha1;
import static com.github.utils4j.imp.Strings.replace;

import java.io.File;
import java.io.IOException;

import com.github.autoupdate4j.IFingerPrint;
import com.github.autoupdate4j.IPatch;
import com.github.utils4j.IDownloader;
import com.github.utils4j.imp.Directory;

class LocalFingerPrint extends FingerPrint {
  
  private int length;
  
  private File rootPath;
  
  LocalFingerPrint(File rootPath) throws IOException {
    reset(rootPath);
  }

  private void reset(File rootPath) throws IOException {
    Directory.requireDirectory(rootPath, "basePath is not directory");
    this.length = rootPath.getCanonicalPath().length();
    this.rootPath = rootPath;
    this.clear();
  }
  
  final File getBasePath() {
    return rootPath;
  }
  
  final void add(File file) throws IOException {
    String canonical = file.getCanonicalPath();
    String suffixPath = canonical.substring(length);
    String relative = replace(suffixPath, '\\', '/');
    String hashFile = file.isDirectory() ? DIRECTORY_KEY : sha1(file);
    put(hashFile, relative);
  }
  
  @Override
  public final IPatch patch(IFingerPrint to) {
    
    if (!(to instanceof FingerPrint))
      return Patch.NOTHING;

    Patch patch = new Patch();
    
    final FingerPrint target = (FingerPrint)to;

    this.forEach((myHash, myOrigin) -> {
      String otherFile = target.get(myHash);
      if (otherFile == null) {        
        patch.delete(new File(rootPath, myOrigin));
      }
    });
    
    target.forEach((targetHash, targetFile) -> {
      String myPath = this.get(targetHash);
      if (myPath == null) {
        File output = new File(rootPath, targetFile);
        if (targetHash.startsWith("directory")) {
          patch.mkdir(output);
        } else {
          target.update(patch, targetFile, output, hashOf(targetHash));
        } 
      }
    });
     
    return patch;
  }
  
  @Override
  final void update(Patch patch, String input, File output, String hash) {
    patch.copy(new File(rootPath, input), output, hash);
  }

  @Override
  final void remoteUpdate(IDownloader downloader, Patch patch, String input, String output) {
    patch.upload(downloader, new File(rootPath, input), output);
  }
}
