package com.github.autoupdate4j.imp;

import java.io.IOException;

import com.github.progress4j.IProgressView;
import com.github.utils4j.imp.Args;

final class NotImplemented implements Command {

  private String commandName;
  
  NotImplemented(String commandName) {
    this.commandName = Args.requireNonNull(commandName, "commandName is null");
  }
  
  @Override
  public void run(IProgressView T) throws IOException {
    throw new IOException(commandName + " not implemented yet");
  }
}
