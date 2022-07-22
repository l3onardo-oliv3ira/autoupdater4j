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

import java.awt.Toolkit;
import java.io.File;
import java.io.IOException;

import com.github.autoupdate4j.IFingerPrint;
import com.github.autoupdate4j.IScanner;
import com.github.autoupdate4j.IUpdater;
import com.github.progress4j.IProgress;
import com.github.progress4j.IProgressFactory;
import com.github.progress4j.IProgressView;
import com.github.progress4j.IStage;
import com.github.progress4j.imp.ProgressFactory;
import com.github.utils4j.gui.imp.AlertDialog;
import com.github.utils4j.imp.Args;

public class Updater implements IUpdater {

  private static final IProgressFactory FACTORY = new ProgressFactory();
  
  private static enum Stage implements IStage {
    UPDATING("Atualizando a aplicação"),
    
    SCANNING("Calculando impressão digital (seja paciente)");

    private final String message;
    
    Stage(String message) {
      this.message = message;
    }
    
    @Override
    public String toString() {
      return message;
    }
  }

  protected final File older, newer;

  public Updater(File older, File newer) {
    this.older = Args.requireNonNull(older, "older is null");
    this.newer = Args.requireNonNull(newer, "newer is null");
  }
  
  @Override
  public void update() {
    IProgressView progress = FACTORY.get();
    try {
      progress.display();
      progress.begin(Stage.UPDATING);
      
      doUpdate(progress);
      
      progress.end();
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      progress.undisplay();
      progress.dispose();
    }   
  }

  protected void doUpdate(IProgress progress) throws IOException, InterruptedException {
    
    progress.begin(Stage.SCANNING);
    
    IScanner scanner = new TreeScanner(older);
    
    progress.info("Escaneando '%s'", older);    
    IFingerPrint oldFp = scanner.scan(progress);
    String oldId = oldFp.getId();    
    progress.info("Impressão digital de %s : %s", older.getName(), oldId);
    
    progress.info("Escaneando '%s'", newer);
    IFingerPrint newFp = scanner.reset(newer).scan(progress);
    String newId = newFp.getId();
    progress.info("Impressão digital de %s : %s", newer.getName(), newId);
    
    progress.end();
    
    boolean notModified = oldId.equals(newId);
    
    if (notModified) {
      Toolkit.getDefaultToolkit().beep();
      AlertDialog.info("Não há diferenças entre a instalação atual e a mais nova.");      
      return;
    }

    oldFp.patch(newFp).ifPresent(new Patcher(progress)::apply);
  }
}
