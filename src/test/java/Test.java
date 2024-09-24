import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;


/**
 * @description: xlsx全量（dict_key, title, code）
 */
public class Test {

    public static void main(String[] args) {
        String filePath = "D:\\yzh\\IPDTeamTemplate.xlsx";
        String tableName = "quick_enum_dict";
        String dictKey = "quick_enum_product_role_all";
        StringBuilder sqlBuilder = new StringBuilder();

        try (FileInputStream fis = new FileInputStream(new File(filePath));
             Workbook workbook = new XSSFWorkbook(fis)) {

            sqlBuilder.append("INSERT INTO ").append(tableName).append("(dict_key, title, CODE)\n")
                    .append("VALUES\n");

            Set<String> seenCodes = new LinkedHashSet<>();
            boolean isFirstEntry = true;

            for (Sheet sheet : workbook) {
                boolean isFirstRow = true;

                for (Row row : sheet) {
                    if (isFirstRow) {
                        isFirstRow = false;
                        continue;
                    }

                    Cell codeCell = row.getCell(1);
                    Cell titleCell = row.getCell(2);

                    if (codeCell != null && codeCell.getCellType() == CellType.STRING &&
                            titleCell != null && titleCell.getCellType() == CellType.STRING) {
                        String code = codeCell.getStringCellValue();
                        String title = titleCell.getStringCellValue();

                        if (isFirstEntry || seenCodes.add(code)) {
                            if (!isFirstEntry) {
                                sqlBuilder.append(",\n");
                            }
                            isFirstEntry = false;
                            sqlBuilder.append("('").append(dictKey).append("', '")
                                    .append(title).append("', '").append(code).append("')");
                        }
                    }
                }
            }

            if (!seenCodes.isEmpty()) {
                System.out.println(sqlBuilder.toString() + ";");
            } else {
                System.out.println("没有找到有效的CODE-title对。");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
