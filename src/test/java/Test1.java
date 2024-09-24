import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

/**
 *description: txt全量并排序（dict_key, code, title, sort_order）
 */
public class Test1 {
    public static void main(String[] args) {
        String filePath = "D:\\yzh\\新建文本文档.txt";
        String txt = "";

        try {
            String encoding = "UTF-8";
            File file = new File(filePath);

            if (file.isFile() && file.exists()) {
                InputStreamReader read = new InputStreamReader(new FileInputStream(file), encoding);
                BufferedReader bufferedReader = new BufferedReader(read);
                StringBuilder contentBuilder = new StringBuilder();
                String line;

                while ((line = bufferedReader.readLine()) != null) {
                    contentBuilder.append(line).append("\n");
                }

                txt = contentBuilder.toString();
                read.close();
            } else {
                System.out.println("找不到指定的文件");
            }

        } catch (Exception e) {
            System.out.println("读取文件内容出错");
            e.printStackTrace();
        }

        if (txt.isEmpty()) {
            System.out.println("读取的文本内容为空");
        } else {
            try {
                JSONArray jsonArray = new JSONArray(txt);
                StringBuilder valuesBuilder = new StringBuilder();
                System.out.println(jsonArray.length());

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String innerName = jsonObject.getString("innerName");
                    String displayName = jsonObject.getString("displayName");

                    int sortOrder = i + 1;
                    valuesBuilder.append("(\'quick_enum_product_version_requirements\', \'").append(innerName).append("\', \'").append(displayName).append("\', \'").append(sortOrder).append("\')");

                    if (i < jsonArray.length() - 1) {
                        valuesBuilder.append(",\n");
                    }
                }

                String sql = "INSERT INTO quick_enum_dict(dict_key, code, title, sort_order)\nVALUES\n" + valuesBuilder.toString() + ";";

                System.out.println(sql);
            } catch (org.json.JSONException e) {
                System.out.println("文本内容不是有效的 JSON 数组格式");
                e.printStackTrace();
            }
        }
    }
}
