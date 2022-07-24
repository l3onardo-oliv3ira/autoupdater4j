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
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

import com.github.progress4j.IProgress;
import com.github.progress4j.IStage;
import com.github.utils4j.imp.Args;
import com.github.utils4j.imp.function.IExecutable;

abstract class StepVisitor extends SimpleFileVisitor<Path> {
  
  private final IProgress progress;
  
  StepVisitor(IProgress progress) {
    this.progress = Args.requireNonNull(progress, "progress is null");
  }
  
  @Override
  public final FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
    super.preVisitDirectory(dir, attrs);
    return doPreVisitDirectory(dir.toFile());
  }
  
  @Override
  public final FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException  {
    return super.visitFileFailed(file, exc);
  }
  
  @Override
  public final FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
    super.visitFile(file, attrs);
    return doVisitFile(file.toFile());
  }

  @Override
  public final FileVisitResult postVisitDirectory(Path dir, IOException exc)  throws IOException {
    super.postVisitDirectory(dir, exc);
    return doPostVisitDirectory(dir.toFile());
  }
  
  protected FileVisitResult doPreVisitDirectory(File dir) throws IOException {
    return FileVisitResult.CONTINUE;
  }

  protected FileVisitResult doVisitFile(File file) throws IOException {
    return FileVisitResult.CONTINUE;
  }

  protected FileVisitResult doPostVisitDirectory(File dir) throws IOException {
    return FileVisitResult.CONTINUE;
  }
  
  protected final void info(String message, Object... args) throws IOException {
    checkIo(() -> progress.info(message, args));
  }
  
  protected final void step(String message, Object... args) throws IOException {
    checkIo(() -> progress.step(message, args));
  }
  
  protected final void begin(IStage stage) throws IOException {
    checkIo(() -> progress.begin(stage));
  }
  
  protected final void end() throws IOException {
    checkIo(() -> progress.end());
  }

  protected final void checkIo(IExecutable<Exception> method) throws IOException {
    try {
      method.execute();
    } catch (InterruptedException e) {
      throw new CancelledOperationException(e);
    } catch (Exception e) { 
      throw new IOException(e);
    }
  }
}
