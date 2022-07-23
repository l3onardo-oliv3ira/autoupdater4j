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
import static java.util.Comparator.naturalOrder;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

import com.github.autoupdate4j.IFingerPrint;
import com.github.utils4j.IDownloader;
import com.github.utils4j.imp.Args;

abstract class FingerPrint implements IFingerPrint {

  private String id = "";

  private final Map<String, String> items = new HashMap<>();

  protected FingerPrint() {
  }

  final void clear() {
    this.items.clear();
    this.id = "";
  }

  @Override
  public final String getId() {
    return id;
  }  
  
  protected final void put(String key, String value) {
    items.put(key + ":" + value, value);
  }
  
  protected final String get(String key) {
    return items.get(key);
  }
  
  protected final void forEach(BiConsumer<String, String> action) {
    items.forEach(action);
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
  public final void writeTo(File output) throws IOException {
    try(PrintStream printer = new PrintStream(new FileOutputStream(output))) {
      items.forEach((key, value) -> printer.println(key));
    }
  }
  
  @Override
  public final void readFrom(File input) throws IOException {
    Args.requireNonNull(input, "input is null");
    clear();
    try (BufferedReader reader = new BufferedReader(new FileReader(input))) {
      String line;
      while((line = reader.readLine()) != null) {
        int idx = line.indexOf(':');
        String key = line.substring(0, idx);
        String val = line.substring(idx + 1);
        put(key, val);
      }
      signature();
    } catch (IOException e) {
      clear();
      throw e;
    }
  }
  
  abstract void update(Patch patch, String input, File output);

  abstract void remoteUpdate(IDownloader downloader, Patch patch, String input, String output);
}
