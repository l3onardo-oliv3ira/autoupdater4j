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

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import com.github.progress4j.IProgressView;
import com.github.utils4j.imp.Args;
import com.github.utils4j.imp.Directory;
import com.github.utils4j.imp.Streams;

final class Copy extends InputAware {

  private final File output;

  private final String hash;

  Copy(File input, File output, String hash) {
    super(input);
    this.output = Args.requireNonNull(output, "output is null");
    this.hash = Args.requireNonNull(hash, "hash is null");
  }

  @Override
  public final String toString() {
    return "COPY " + input + " TO " + output;
  }

  @Override
  public final void handle(IProgressView progress) throws IOException {
    Directory.mkDir(output.getParentFile());
    Files.copy(input.toPath(), output.toPath(), REPLACE_EXISTING);
    Directory.requireExists(output, "Unabled to " + this);
    checkHash();
  }

  private void checkHash() throws IOException {
    try {
      if (!hash.equals(Streams.sha1(output))) {
        throw _throw(null);
      }
    }catch(HashCheckingException e) {
      throw e;
    }catch(IOException e) {
      throw _throw(e);
    }
  }

  private HashCheckingException _throw(Exception cause) {
    return new HashCheckingException("HASH INVÁLIDO para " + output + " HASH: " + hash, cause);
  }
}
