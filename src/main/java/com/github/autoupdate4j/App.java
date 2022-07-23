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

package com.github.autoupdate4j;

import java.io.File;
import java.io.IOException;
import java.net.ProxySelector;

import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.cookie.StandardCookieSpec;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.routing.SystemDefaultRoutePlanner;
import org.apache.hc.core5.util.Timeout;

import com.github.autoupdate4j.imp.LocalUpdater;
import com.github.autoupdate4j.imp.RemoteUpdater;
import com.github.utils4j.imp.Downloader;

public class App {

  public static void main(String[] args) throws IOException, InterruptedException {
    final File older = new File("D:\\temp\\comparacaoUPDATE\\PjeOffice PRO");
    localUpdate(older);
    remoteUpdate(older);
  }

  private static void localUpdate(File older) throws IOException {
    final File newer = new File("D:\\temp\\comparacaoUPDATE\\PjeOffice PRO New Version");
    IUpdater lup = new LocalUpdater(older, newer);
    lup.update();
  }

  private static void remoteUpdate(File older) throws IOException {
    String remoteUri = "http://update.pjeoffice-pro.fakeaddress";
    try(CloseableHttpClient client = buildClient()) {
      IUpdater rup = new RemoteUpdater(new Downloader(client), older, remoteUri);
      rup.update();
    }
  }

  private static CloseableHttpClient buildClient() {
    final Timeout _1m = Timeout.ofMinutes(1);
    final Timeout _3m = Timeout.ofMinutes(3);
    final Timeout _30s = Timeout.ofSeconds(30);
    final HttpClientBuilder builder = HttpClients.custom();
    builder.setRoutePlanner(new SystemDefaultRoutePlanner(ProxySelector.getDefault()))
      .evictExpiredConnections()
      .evictIdleConnections(_1m)
      .setDefaultRequestConfig(RequestConfig.custom()
        .setResponseTimeout(_30s)
        .setConnectTimeout(_3m)    
        .setConnectionKeepAlive(_3m)
        .setConnectionRequestTimeout(_3m)
        .setCookieSpec(StandardCookieSpec.IGNORE).build()
      );
    return builder.build();
  }
}
