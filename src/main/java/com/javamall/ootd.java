package com.javamall;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ootd {
    public static void main(String[] args) {
        try {
            // Python 脚本的路径
            String pythonScriptPath = "F:\\300-Program\\310-Code\\ootd\\ootd_dc.py";
            // 要传递的两个字符串参数
            String arg1 = "F:\\300-Program\\310-Code\\ootd\\img\\model_8.png";
            String arg2 = "F:\\300-Program\\310-Code\\ootd\\img\\02015_00.jpg";
            String arg3 = "Upper-body";

            // 构建命令
            ProcessBuilder processBuilder = new ProcessBuilder("python", pythonScriptPath, arg1, arg2);

            // 启动进程
            Process process = processBuilder.start();

            // 获取进程的输出流
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String output;
            // 读取进程的输出
            while ((output = reader.readLine()) != null) {
                System.out.println("Python script output: " + output);
            }

            // 等待进程执行完成
            int exitCode = process.waitFor();

            // 输出进程的退出代码
            System.out.println("Process exited with code " + exitCode);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
