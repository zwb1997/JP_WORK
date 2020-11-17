package work;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.reader.StreamReader;

/**
 * Unit test for simple App.
 */
public class AppTest {
  /**
   * Rigorous Test :-)
   */
  private static final Logger LOG = LoggerFactory.getLogger(AppTest.class);

  @Test
  public void shouldAnswerWithTrue() {
    Path currentPath = Paths.get("");
    System.out.println(currentPath.toAbsolutePath().toString());
  }

  @Test
  public void test2() {
    HttpClient client = HttpClients.createDefault();
    HttpGet get = new HttpGet("");
  }

  @Test
  public void test3() {
    String host = "127.0.0.1";
    int port = 32998;
    // try {
    // ServerSocket server = new ServerSocket(port);
    // server.setSoTimeout(0);
    // Socket socket = server.accept();
    // BufferedInputStream reader = new
    // BufferedInputStream(socket.getInputStream());
    // byte[] dataByteArr = null;
    // dataByteArr = reader.readAllBytes();
    // String s = new String(dataByteArr);
    // System.out.println(s);
    // } catch (IOException e) {
    // LOG.error("error,message :{}", e);
    // }
  }

}
