package com.zaixiaoqu.serialport;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class InstallSilently {
    /**
     * The install command installs a package to the system. Options:
     *
     * @command -l: install the package with FORWARD_LOCK.
     * @command -r: reinstall an existing app, keeping its data.
     * @command -t: allow test .apks to be installed.
     * @command -i: specify the installer package name.
     * @command -s: install package on sdcard.
     * @command -f: install package on internal flash.
     */
    /**
     * The uninstall command removes a package from the system. Options:
     *
     * @command -k: keep the data and cache directories around. after the
     *          package removal.
     */
    public static boolean installNow(String path) {
        try {
            String[] installArgs = { "pm", "install", "-r", path};
            String installState = runCommand(installArgs);
            try {
                String[] removeAPKArgs = { "rm", "-f", path};
                runCommand(removeAPKArgs);
            } catch (Exception e) {
            }
            if (
                null != installState && (
                        installState.equals("-1") ||
                        installState.equals("-2") ||
                        installState.equals("-3")
                )
            ) {
                return false;
            }
            return true;
        } catch (Exception e) {
        }
        return false;
    }

    public static String runCommand(String[] args) {
        String result = "";
        ProcessBuilder processBuilder = new ProcessBuilder(args);
        Process process = null;
        InputStream errIs = null;
        InputStream inIs = null;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            int read = -1;
            process = processBuilder.start();
            errIs = process.getErrorStream();
            while ((read = errIs.read()) != -1) {
                baos.write(read);
            }
            baos.write('\n');
            inIs = process.getInputStream();
            while ((read = inIs.read()) != -1) {
                baos.write(read);
            }
            byte[] data = baos.toByteArray();
            result = new String(data);
        } catch (IOException e) {
            result = "-1";
            e.printStackTrace();
        } catch (Exception e) {
            result = "-2";
            e.printStackTrace();
        } finally {
            try {
                if (errIs != null) {
                    errIs.close();
                }
                if (inIs != null) {
                    inIs.close();
                }
            } catch (IOException e) {
                result = "-3";
                e.printStackTrace();
            }
            if (process != null) {
                process.destroy();
            }
        }
        return result;
    }
}
