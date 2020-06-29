package com.smit.mywidget.ui.util;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author xiexi
 */
public class SavingFile {

    private ExecutorService mExecutorService = Executors.newSingleThreadExecutor();
    public static final String TAG = "SavingFile";

    private File createFile(String name) {

        String dirPath = Environment.getExternalStorageDirectory().getPath() + "/smitTest/";
        File file = new File(dirPath);

        if (!file.exists()) {
            file.mkdirs();
        }

        String filePath = dirPath + name;
        File objFile = new File(filePath);
        if (!objFile.exists()) {
            try {
                objFile.createNewFile();
                return objFile;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public void saving(){
        String tmpName = System.currentTimeMillis() + "";
        final File tmpFile = createFile(tmpName + ".txt");
        if(tmpFile == null){
            Log.e(TAG, "saving: No such file or directory");
            return;
        }
        mExecutorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                        FileOutputStream outputStream = new FileOutputStream(tmpFile.getAbsoluteFile());
                        OutputStreamWriter oStreamWriter = new OutputStreamWriter(outputStream, "utf-8");
                        int i = 0;

                        for(int j = 0; j < 100; j++) {
                            oStreamWriter.write(i + " ");
                            if(++i % 10 == 0){
                                oStreamWriter.write("\n");
                            }
                        }
                        oStreamWriter.close();
                        outputStream.close();

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

}
