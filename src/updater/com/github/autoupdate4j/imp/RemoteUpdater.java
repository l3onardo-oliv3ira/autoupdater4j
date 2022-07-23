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

import com.github.utils4j.IDownloader;
import com.github.utils4j.gui.imp.ExceptionAlert;

public class RemoteUpdater extends DefaulUpdater {

  public RemoteUpdater(IDownloader downloader, File older, String fingerPrint) throws IOException {
    super(
      new TreeScanner(older),
      new HttpScanner(downloader, fingerPrint)
    );
  }
  
  protected void handleException(Exception e) {

    if (e instanceof DownloadFailException) {
      ExceptionAlert.show("Não foi possível acessar o endereço de atualização", e);
      return;
    }
    
    if (e instanceof UndefinedUpdateException) {
      ExceptionAlert.show("Não foi possível determinar se há ou não uma nova "
          + "versão do aplicativo!\n"
          + " O repositório de atualização encontra-se inacessível ou em formato inválido.", e);
      return;
    }
    
    if (e instanceof FingerPrintLoadException) {
      ExceptionAlert.show("Não foi possível carregar impressão digital da atualização.", e);
      return;
    }
    
    super.handleException(e);
  }
}
