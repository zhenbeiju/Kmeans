package test_kmeans;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.logging.LogManager;

import org.json.JSONArray;
import org.json.JSONObject;

import com.stromberglabs.cluster.Cluster;
import com.stromberglabs.cluster.ClusterUtils;
import com.stromberglabs.cluster.Clusterable;
import com.stromberglabs.cluster.KClusterer;
import com.stromberglabs.cluster.KMeansClusterer;
import com.stromberglabs.cluster.Point;

public class Test {
    static HashMap<String, String> head = new HashMap<>();
    static {
        head.put("Authorization", " token 943138332225599199df40deb2e95a9f736006a1");
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        // test();
        // buildData(5);
        String lastUpdateTemp = "";
        while (true) {
            String netdata = httpConnectUtil.readContentFromGet(
                    "https://iot.espressif.cn/v1/datastreams/temperature/datapoints/", head);
            JSONObject json = new JSONObject(netdata);
            JSONArray array = json.getJSONArray("datapoints");
            int length = array.length();
            float[] values = new float[length];
            for (int i = 0; i < length; i++) {
                JSONObject point = array.getJSONObject(i);
                double x = point.getDouble("x");
                values[i] = (float) x;
                System.out.println(x);
            }

            String result = getValue(values);
            if (result != null) {
                // update
                System.out.println("updat value" + result);
                if (!result.equals(lastUpdateTemp)) {
                    lastUpdateTemp = result;
                    updatError(result);
                }

            }
            Thread.sleep(5000);
        }

    }

    public static void buildData(int times) throws IOException {

        for (int i = 0; i < times; i++) {
            updateDate(String.valueOf(new Random().nextInt(10) + 20));
        }
    }

    public static void updateDate(String tmp) throws IOException {
        String updateurl = "https://iot.espressif.cn/v1/datastreams/temperature/datapoint/";
        String update = "{\"datapoint\":{\"x\":" + tmp + "}}";
        System.out.println(update);
        httpConnectUtil.readContentFromPost(updateurl, head, update);
    }

    public static void updatError(String errorValue) throws IOException {
        // 30dae74b8ede53f2647de05722f00e9c57247f68
        HashMap<String, String> errorhead = new HashMap<>();
        errorhead.put("Authorization", " token 840bf227d84f21b657244b8fb77b916321fca738");
        String updateurl = "https://iot.espressif.cn/v1/datastreams/temperature/datapoint/";
        String update = "{\"datapoint\":{\"x\":" + errorValue + "}}";
        System.out.println(update);
        httpConnectUtil.readContentFromPost(updateurl, errorhead, update);
    }

    public static void test() {
        // System.out.println(getValue(new float[] { 1.0f, 2, 3, 4, 6, 9, 10,
        // 13, 15 }));
        // System.out.println(getValue(new float[] { 22, 23, 24, 26, 27, 28, 29,
        // 30, 50 }));
        // System.out.println(getValue(new float[] { -10, 22, 1, 23, 24, 26, 27,
        // 28, 29, 30, 50 }));
        // System.out.println(getValue(new float[] { 22, 1, 23, 24, 26, 27, 28,
        // 29, 30, -80 }));
        // System.out.println(getValue(new float[] { 10, -23, -24, -26, -27,
        // -28, -29, -30 }));
        System.out.println(getValue(new float[] { -10, -11, -12, -13, -14, -13, -12, -11, -10, -8, -6, -4, 0, 1, 2, 3,
                4, 5, 6, 7, 8, 9 }));
    }

    public static String getValue(float[] values) {
        List<Clusterable> mPoints = new ArrayList<Clusterable>();
        for (float ff : values) {
            Clusterable point = new Point(1.0f, ff);
            mPoints.add(point);
        }
        KClusterer clusterer = new KMeansClusterer();
        for (int i = 2; i <= values.length / 3; i++) {

            Cluster clusters[] = clusterer.cluster(mPoints, i);
            for (Cluster cluster : clusters) {
                List<Clusterable> result = cluster.getItems();
                if (result.size() == 1) {

                    return String.valueOf(result.get(0).getLocation()[1]);
                }
            }
        }
        return null;
    }
}
