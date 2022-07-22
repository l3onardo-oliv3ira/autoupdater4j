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
import java.nio.file.FileVisitResult;
import java.nio.file.Files;

import com.github.autoupdate4j.IFingerPrint;
import com.github.autoupdate4j.IScanner;
import com.github.progress4j.IProgress;
import com.github.utils4j.imp.Args;

class TreeScanner implements IScanner {

  private FingerPrint fp;
  
  protected TreeScanner(File root) throws IOException {
    fp = new FingerPrint(root);
  }

  @Override
  public IScanner reset(File root) throws IOException {
    this.fp = new FingerPrint(root);
    return this;
  }
  
  @Override
  public final IFingerPrint scan(IProgress progress) throws IOException {    
    Args.requireNonNull(progress, "progress is null");
    if (fp.ready())
      return fp;
    Files.walkFileTree(fp.getBasePath().toPath(), new Builder(progress));   
    return fp.signature();
  }
  
  private class Builder extends StepVisitor {

    Builder(IProgress progress){
      super(progress);
    }
    
    protected FileVisitResult doPreVisitDirectory(File dir) throws IOException {
      return FileVisitResult.CONTINUE;
    }

    protected FileVisitResult doVisitFile(File file) throws IOException {
      fp.add(file);
      info("%s", file);
      return FileVisitResult.CONTINUE;
    }

    protected FileVisitResult doPostVisitDirectory(File dir) throws IOException {
      fp.add(dir);
      info("%s", dir);
      return FileVisitResult.CONTINUE;
    }
  }
}
