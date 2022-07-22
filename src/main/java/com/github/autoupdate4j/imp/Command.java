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

import static java.nio.file.Files.walk;
import static java.util.Comparator.reverseOrder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;

import com.github.utils4j.imp.Args;
import com.github.utils4j.imp.function.Executable;

abstract class Command implements Executable<IOException> {

  protected static void mkDir(File folder) throws IOException {
    if (folder.exists() || folder.mkdirs())
      return;
    throw new IOException("Unabled to create directory tree " + folder);
  }
  
  protected static void rmDir(File input) throws IOException {
    walk(input.toPath()).sorted(reverseOrder()).map(Path::toFile).forEach(File::delete);   
  }
  
  protected static void requireNotExists(File input) throws IOException {
    if (!input.exists())
      return;
    throw new IOException("Unabled to delete " + input);
  }
  
  protected static void requireExists(File input) throws IOException {
    if (input.exists())
      return;
    throw new FileNotFoundException(input.getAbsolutePath());    
  }

  protected final File input;
  
  protected Command(File input) {
    this.input = Args.requireNonNull(input, "input is null");
  }  
}
