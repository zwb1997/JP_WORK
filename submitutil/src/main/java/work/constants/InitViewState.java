package work.constants;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component("InitViewState")
public class InitViewState {
    private static final Logger LOG = LoggerFactory.getLogger(InitViewState.class);
    public static final String LOGIN_VIEWSTATE = "LOGIN_VIEWSTATE";
    private String viewStateFileFolderPath = "";
    private static Map<String, String> FileParamsMap = new LinkedHashMap<>();
    private static Map<String, String> ParamsValueMap = new LinkedHashMap<>();
    static {
        FileParamsMap.put("login_viewstate.txt", "LOGIN_VIEWSTATE");
        ParamsValueMap.put("LOGIN_VIEWSTATE", "");
    }

    // get filenames from \src\main\resources\viewstates\ then init these params
    public InitViewState() {
        Path path = Paths.get("");
        String currentWorkDir = FileSystems.getDefault().getPath("").resolve("submitutil").toAbsolutePath().toString();
        Path paramsPath = path.resolve(currentWorkDir + File.separator + "src" + File.separator + "main"
                + File.separator + "resources" + File.separator + "viewstates");
        File[] paramsFile = paramsPath.toFile().listFiles();
        LOG.info(" current work dir :{} ", currentWorkDir);
        LOG.info("init some params... please waiting....");
        for (File f : paramsFile) {
            String fn = f.getAbsolutePath().toString();
            try {
                try (BufferedReader reader = new BufferedReader(new FileReader(f))) {
                    String temp = reader.readLine();
                    ParamsValueMap.put(FileParamsMap.get(fn), temp);
                }
            } catch (Exception e) {
                LOG.error("init viewState params error,file:{},message:{}", fn, e.getMessage());
            }
        }
        LOG.info("init done...");
    }

    public static Map<String, String> getParamsMap() {
        return ParamsValueMap;
    }
}
