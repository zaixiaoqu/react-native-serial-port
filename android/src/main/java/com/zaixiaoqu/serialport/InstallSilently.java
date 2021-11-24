package com.zaixiaoqu.serialport;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

public class InstallSilently {

    /**
     * 静默安装完成以后重新打开应用
     *
     * @param path
     * @param restartActivityName
     * @return
     */
    public static String installNow(String path, final String restartActivityName) {
        return runCommand("LD_LIBRARY_PATH=/vendor/lib*:/system/lib* pm install -r "+path+" && am start -n  "+restartActivityName);
    }

    /**
     * 静默安装
     *
     * @param path
     * @return
     */
    public static String installNow(String path) {
        return runCommand("LD_LIBRARY_PATH=/vendor/lib*:/system/lib* pm install -r "+path+" ");
    }

    /**
     * 执行具体的静默安装逻辑，需要手机ROOT。
     *
     * @param commandStr 脚本字符串
     */
    public static String runCommand(String commandStr) {
        String result = "-1";
        DataOutputStream dataOutputStream = null;
        BufferedReader errorStream = null;
        try {
            // 申请su权限
            Process process = Runtime.getRuntime().exec("su");
            dataOutputStream = new DataOutputStream(process.getOutputStream());
            // 执行pm install命令
            String command = commandStr + "\n";
            dataOutputStream.write(command.getBytes(Charset.forName("utf-8")));
            dataOutputStream.flush();
            dataOutputStream.writeBytes("exit\n");
            dataOutputStream.flush();
            process.waitFor();
            errorStream = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            String msg = "";
            String line;
            // 读取命令的执行结果
            while ((line = errorStream.readLine()) != null) {
                msg += line;
            }
            // 如果执行结果中包含Failure字样就认为是安装失败，否则就认为安装成功
            result = msg;
        } catch (Exception e) {
            result = "-2";
        } finally {
            try {
                if (dataOutputStream != null) {
                    dataOutputStream.close();
                }
                if (errorStream != null) {
                    errorStream.close();
                }
            } catch (IOException e) {
                result = "-3";
            }
        }
        return result;
    }
}