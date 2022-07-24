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

import java.io.IOException;

import com.github.autoupdate4j.IFingerPrint;
import com.github.autoupdate4j.IPatch;
import com.github.autoupdate4j.IScanner;
import com.github.autoupdate4j.IUpdater;
import com.github.progress4j.IProgressFactory;
import com.github.progress4j.IProgressView;
import com.github.progress4j.imp.ProgressFactory;
import com.github.utils4j.gui.imp.AlertDialog;
import com.github.utils4j.gui.imp.ExceptionAlert;
import com.github.utils4j.imp.Args;

public class DefaulUpdater implements IUpdater {

  private static final IProgressFactory FACTORY = new ProgressFactory();
  
  protected final IScanner older;
  
  protected final IScanner newer;

  public DefaulUpdater(IScanner older, IScanner newer) {
    this.older = Args.requireNonNull(older, "older is null");
    this.newer = Args.requireNonNull(newer, "newer is null");
  }
  
  @Override
  public boolean update() {
    IProgressView progress = FACTORY.get();
    boolean success = false;
    try {
      progress.display();
      progress.begin("Atualizando aplicação");
      doUpdate(progress);
      success = true;
      progress.end();
    } catch (Exception e) {
      handleException(e);
    } finally {
      progress.undisplay();
      progress.dispose();
    }   
    if (success) {
      AlertDialog.info("Atualização finalizada com sucesso!");
    }
    return success;
  }

  protected void doUpdate(IProgressView progress) throws IOException, InterruptedException {
    progress.begin("Calculando impressão digital (seja paciente)");

    progress.info("Remote scanning...");
    IFingerPrint newFp = newer.scan(progress);
    String newId = newFp.getId();
    progress.info("Impressão digital remota: %s", newId);

    progress.info("Local scanning...");    
    IFingerPrint oldFp = older.scan(progress);
    String oldId = oldFp.getId();    
    progress.info("Impressão digital local: %s", oldId);
    
    progress.end();
    
    boolean notModified = oldId.equals(newId);
    if (notModified) {
      throw new ApplicationAlreadyUpdatedException();
    }
    
    IPatch diff = oldFp.patch(newFp);
    new Patcher(progress).apply(diff);
  }
  
  protected void handleException(Exception e) {
    if (e instanceof ApplicationAlreadyUpdatedException) {
      AlertDialog.info("A aplicação já se encontra atualizada!");
      return;
    }

    if (e instanceof ApplicationUpdateFailException) {
      ExceptionAlert.show("Não foi possível atualizar a aplicação!", e);
      return;
    }
    
    if (e instanceof InterruptedException || e instanceof CancelledOperationException) {
      AlertDialog.info("A atualização foi cancelada!");
      return;
    }
    
    ExceptionAlert.show("Exceção inesperada", e);
  }

}
