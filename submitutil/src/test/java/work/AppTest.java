package work;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

  @Test
  public void test33() {
    // try {
    //   Document doc = Jsoup.parse(new File("C:\\Users\\ZZZ\\Desktop\\demo1.html"), "UTF-8");
    //   Elements eles = doc.getElementsByTag("option");
    //   for (Element e : eles) {
    //     LOG.info("\"{}:{}\",", e.attr("value"), e.text());
    //   }
    // } catch (Exception e) {
    //   LOG.error(" error ,message :{}", e);
    // }
  }

}
