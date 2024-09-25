package org.example;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

/**
 * @description: xlsx树状并排序（dict_key, title, CODE, parent_code, sort_code）
 */
public class Main {
    private static final String FILE_PATH = "D:\\yzh\\IPDTeamTemplate.xlsx";
    private static final String SHEET_NAME = "Default";

    public static void main(String[] args) {
        List<Role> roles = new ArrayList<>();
        Map<String, List<Integer>> codeToRows = new HashMap<>();
        Set<String> uniqueCodes = new HashSet<>();
        int rowCount = 0;
        int sortOrder = 1;

        try (FileInputStream fis = new FileInputStream(FILE_PATH);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = getSheetByNameOrIndex(workbook, SHEET_NAME);

            for (Row row : sheet) {
                if (row.getRowNum() == 0) {
                    continue;
                }

                int level = (int) row.getCell(0).getNumericCellValue();
                String code = row.getCell(1).getStringCellValue();
                String title = row.getCell(2).getStringCellValue();

                if (code.isEmpty() || title.isEmpty()) {
                    continue;
                }

                rowCount++;

                if (!uniqueCodes.add(code)) {
                    codeToRows.computeIfAbsent(code, k -> new ArrayList<>()).add(row.getRowNum() + 1);
                    continue;
                }

                Role role = new Role(level, code, title);
                role.setSortOrder(sortOrder++);
                roles.add(role);

                codeToRows.computeIfAbsent(code, k -> new ArrayList<>()).add(row.getRowNum() + 1);
            }

            for (Map.Entry<String, List<Integer>> entry : codeToRows.entrySet()) {
                List<Integer> rows = entry.getValue();
                if (rows.size() > 1) {
                    System.out.println("重复的 code: " + entry.getKey() + " 出现在行号: " + rows);
                }
            }
            System.out.println("共处理了 " + rowCount + " 条记录");

        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("INSERT INTO quick_enum_dict(dict_key, title, CODE, parent_code, sort_order)\nVALUES");
        generateSQLValues(roles);
    }

    private static Sheet getSheetByNameOrIndex(Workbook workbook, String sheetName) {
        Sheet sheet = workbook.getSheet(sheetName);
        return sheet;
    }

    private static Role findParentRole(List<Role> roles, int currentIndex) {
        Role currentRole = roles.get(currentIndex);
        for (int i = currentIndex - 1; i >= 0; i--) {
            if (roles.get(i).getLevel() < currentRole.getLevel()) {
                return roles.get(i);
            }
        }
        return null;
    }

    private static void generateSQLValues(List<Role> roles) {
        StringBuilder sqlBuilder = new StringBuilder();
        String template = "('quick_enum_product_role_ae_odm', '%s', '%s', '%s', %d),\n";

        for (Role role : roles) {
            if (role.getLevel() > 0) {
                Role parentRole = findParentRole(roles, roles.indexOf(role));
                if (parentRole != null) {
                    role.setParentCode(parentRole.getCode());
                }
            }

            sqlBuilder.append(String.format(template, role.getTitle(), role.getCode(), role.getParentCode(), role.getSortOrder()));
        }

        if (sqlBuilder.length() > 0) {
            sqlBuilder.setLength(sqlBuilder.length() - 2);
        }

        System.out.println(sqlBuilder.toString() + ";");
    }
}
