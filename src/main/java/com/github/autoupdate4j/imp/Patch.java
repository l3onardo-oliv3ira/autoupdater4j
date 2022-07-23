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
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import com.github.autoupdate4j.IPatch;
import com.github.progress4j.IProgressView;
import com.github.utils4j.IDownloader;

class Patch implements IPatch {
  
  private final List<Command> cmds = new LinkedList<>();
  
  Patch() {
  }
  
  final void delete(File file) {
    cmds.add(new Delete(file));
  }

  final void copy(File from, File to) {
    cmds.add(new Copy(from, to));
  }
  
  final void mkdir(File output) {
    cmds.add(new Mkdir(output));
  }
  
  final void download(IDownloader downloader, String uri, File output) {
    cmds.add(new Download(downloader, uri, output));
  }

  final void upload(IDownloader downloader, File file, String output) {
    notImplemented("upload");
  }
  
  final void remoteDelete(IDownloader downloader, String myOrigin) {
    notImplemented("remoteDelete");
  }
  
  final void remoteMkdir(IDownloader downloader, String output) {
    notImplemented("remoteMkdir");
  }
  
  final void notImplemented(String command) {
    cmds.add(new NotImplemented(command));
  }

  final Optional<IPatch> asOptional() {
    return cmds.isEmpty() ? Optional.empty() : Optional.of(this);
  }
  
  @Override
  public void apply(IProgressView progress) throws Exception {
    progress.begin("Aplicando o patch", cmds.size());
    for(Command cmd: cmds) {
      progress.step(cmd.toString());
      cmd.run(progress);
    }
    progress.end();
  }
}



