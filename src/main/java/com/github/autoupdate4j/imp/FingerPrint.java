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
import static java.util.Comparator.naturalOrder;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.github.autoupdate4j.IFingerPrint;
import com.github.autoupdate4j.IPatch;
import com.github.utils4j.imp.Directory;

class FingerPrint implements IFingerPrint {

  private String id;

  private int length;
  
  private File basePath;

  private final Map<String, String> items = new HashMap<>();

  FingerPrint(File basePath) throws IOException {
    reset(basePath);
  }

  private void reset(File basePath) throws IOException {
    Directory.requireDirectory(basePath, "basePath is not directory");
    this.length = basePath.getCanonicalPath().length();
    this.basePath = basePath;
    this.clear();
  }
  
  final void clear() {
    this.items.clear();
    this.id = "";
  }

  @Override
  public final String getId() {
    return id;
  }  
  
  final File getBasePath() {
    return basePath;
  }

  @Override
  public final void writeTo(File output) throws IOException {
    try(PrintStream printer = new PrintStream(new FileOutputStream(output))) {
      items.forEach((key, value) -> printer.println(key));
    }
  }
  
  final void add(File file) throws IOException {
    String canonical = file.getCanonicalPath();
    String suffixPath = canonical.substring(length);
    String relative = replace(suffixPath, '\\', '/');
    String hashFile = file.isDirectory() ? "directory" : sha1(file);
    String hashId = hashFile + ":" + relative;
    items.put(hashId, relative);    
  }
  
  final boolean ready() {
    return !id.isEmpty();
  }
  
  final FingerPrint signature() {
    if (!ready())
      items.keySet().stream().sorted(naturalOrder()).forEach(key -> id = sha1((id + key).getBytes()));
    return this;
  }
  
  @Override
  public final void readFrom(File input) throws IOException {
    clear();
    try (BufferedReader reader = new BufferedReader(new FileReader(input))) {
      String line; int lineNumber = 0;
      while((line = reader.readLine()) != null) {
        lineNumber++;
        int idx = line.indexOf(':');
        if (idx <= 0) {
          throw new IOException("Linha " + lineNumber + 
              " com formato inválido (falta o ':' ou mal posicionado)");
        }
        String key = line.substring(0, idx);
        String val = line.substring(idx + 1);
        items.put(key, val);    
      }
      signature();
    } catch (IOException e) {
      clear();
      throw e;
    }
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
        patch.delete(new File(basePath, myOrigin));
      }
    });
    
    target.items.forEach((targetHash, targetFile) -> {
      String myPath = items.get(targetHash);
      if (myPath == null) {
        File output = new File(basePath, targetFile);
        if (targetHash.startsWith("directory")) {
          patch.mkdir(output);
        } else {
          patch.copy(new File(target.basePath, targetFile), output);
        }
      }
    });
     
    return patch.asOptional();
  }
}
