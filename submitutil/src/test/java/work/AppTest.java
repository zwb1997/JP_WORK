package work;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.LinkedHashMap;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.junit.Test;

import work.model.GoodModel;

/**
 * Unit test for simple App.
 */
public class AppTest {
    /**
     * Rigorous Test :-)
     */
    @Test
    public void shouldAnswerWithTrue() {
        Path currentPath = Paths.get("");
        System.out.println(currentPath.toAbsolutePath().toString());
    }

    @Test
    public void tes1() {
        LinkedHashMap<String, String> map = new LinkedHashMap<>();

        map.put("1", "1");
        map.put("1", "2");
        System.out.println(map);
    }

    @Test
    public void test2() {
        String word = "1|1|2|3|$|%";
        var arr = word.split("\\|");
        System.out.println(arr);
    }

}
