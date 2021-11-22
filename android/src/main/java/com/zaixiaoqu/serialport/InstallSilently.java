package com.zaixiaoqu.serialport;

import android.content.Context;
import android.content.Intent;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.Charset;

public class InstallSilently {

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

    /**
     * 重新安装APP并发送广播
     *
     * @param mContext
     * @param currenttempfilepath
     */
    public static String excuteSuCMD(Context mContext, String currenttempfilepath) {
        String resultStr = "-1";

        Process process = null;
        OutputStream out = null;
        InputStream in = null;
        try {
            //请求root
            process = Runtime.getRuntime().exec("su");
            out = process.getOutputStream();
            //调用安装
            out.write(("pm install -r " + currenttempfilepath + "\n").getBytes());
            in = process.getInputStream();
            int len = 0;
            byte[] bs = new byte[256];
            while (-1 != (len = in.read(bs))) {
                String state = new String(bs, 0, len);
                if (state.equals("success\n") || state.equals("Success\n")) {
                    resultStr = "success";

                    //安装成功后的操作
                    //静态注册自启动广播
                    Intent intent = new Intent();
                    //与清单文件的receiver的anction对应
                    intent.setAction("android.intent.action.PACKAGE_REPLACED");
                    //发送广播
                    mContext.sendBroadcast(intent);
                }
            }
        } catch (IOException e) {
            resultStr = "-2";
            e.printStackTrace();
        } catch (Exception e) {
            resultStr = "-3";
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.flush();
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
                resultStr = "-4";
                e.printStackTrace();
            }
        }
        return resultStr;
    }
}
