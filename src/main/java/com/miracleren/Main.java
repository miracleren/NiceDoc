package com.miracleren;

/**
 * 基于Apache POI开的快速模板填充生成word,excel文档工具
 * by miracleren@gmail.com
 */

public class Main {

    public static void main(String[] args) {
        System.out.println(" _   _ _          _____             ");
        System.out.println("| \\ | (_)        |  __ \\            ");
        System.out.println("|  \\| |_  ___ ___| |  | | ___   ___ ");
        System.out.println("| . ` | |/ __/ _ \\ |  | |/ _ \\ / __|");
        System.out.println("| |\\  | | (_|  __/ |__| | (_) | (__ ");
        System.out.println("|_| \\_|_|\\___\\___|_____/ \\___/ \\___|");

        //测试示例模板生成word
        //TestTemplate.buildTestDocx();

        //测试示例模板生成xlsx
        TestTemplate.buildTestXlsx();
    }
}
