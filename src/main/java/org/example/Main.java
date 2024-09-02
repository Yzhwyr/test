package org.example;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main {
    private static final String FILE_PATH = "D:\\yzh/IPDTeamTemplate.xlsx";

    public static void main(String[] args) {
        List<Role> roles = new ArrayList<>();

        try (FileInputStream fis = new FileInputStream(FILE_PATH);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(0);

            for (Row row : sheet) {
                if (row.getRowNum() == 0) {

                    continue;
                }

                int level = (int) row.getCell(0).getNumericCellValue();
                String code = row.getCell(1).getStringCellValue();
                String title = row.getCell(2).getStringCellValue();

                Role role = new Role(level, code, title);
                roles.add(role);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("INSERT INTO quick_enum_dict(dict_key, title, CODE, parent_code)\nVALUES");
        for (int i = 0; i < roles.size(); i++) {
            Role currentRole = roles.get(i);
            if (currentRole.getLevel() > 0) {
                Role parentRole = findParentRole(roles, i);
                if (parentRole != null) {
                    currentRole.setParentCode(parentRole.getCode());
                }
            }
        }


        generateSQLValues(roles);
        System.out.println(";");
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
        String template = "('quick_enum_product_role_pc', '%s', '%s', '%s'),\n";

        for (Role role : roles) {
            sqlBuilder.append(String.format(template, role.getTitle(), role.getCode(), role.getParentCode()));
        }


        if (sqlBuilder.length() > 0) {
            sqlBuilder.setLength(sqlBuilder.length() - 2);
        }

        System.out.println(sqlBuilder.toString());
    }
}

