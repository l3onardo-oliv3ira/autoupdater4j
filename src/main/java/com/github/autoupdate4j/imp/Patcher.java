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

import com.github.autoupdate4j.IPatch;
import com.github.autoupdate4j.IPatcher;
import com.github.progress4j.IProgressView;
import com.github.utils4j.gui.imp.AlertDialog;
import com.github.utils4j.gui.imp.ExceptionAlert;
import com.github.utils4j.imp.Args;

class Patcher implements IPatcher {

  protected final IProgressView progress;
  
  public Patcher(IProgressView progress) {
    this.progress = Args.requireNonNull(progress, "progress is null");
  }
  
  @Override
  public final boolean apply(IPatch p) {
    try {
      beforeApply();
      p.apply(progress);
      afterApply();
      return true;
    } catch (Exception e) {
      handleError(e);
      return false;
    }    
  }

  protected void beforeApply() {
    //backup here!
  }

  protected void afterApply() {
    //delete backup!
    AlertDialog.info("Atualização finalizada com sucesso!");
  }
  
  protected void handleError(Exception e) {
    //restore here
    ExceptionAlert.show("Não foi possível atualizar a aplicação", e);
  }
}
