/* Copyright 2017 The TensorFlow Authors. All Rights Reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
==============================================================================*/

package com.my;

import android.app.Activity;
import android.content.res.AssetFileDescriptor;
import android.os.SystemClock;
import android.util.Log;

import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.gpu.GpuDelegate;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * This classifier works with the Inception-v3 slim model.
 * It applies floating point inference rather than using a quantized model.
 */
public class BpMultipleClassifier {
    private static final String TAG = "TfLiteCameraDemo";

    /**
     * An array to hold inference results, to be feed into Tensorflow Lite as outputs.
     * This isn't part of the super class, because we need a primitive array here.
     */
    Interpreter.Options tfliteOptions;
    protected Interpreter tflite;


    protected ByteBuffer input1 = null;
    protected ByteBuffer input2 = null;
    protected ByteBuffer input3 = null;
    protected ByteBuffer input4 = null;
    protected ByteBuffer input5 = null;
    protected ByteBuffer input6 = null;

    protected Object[] inputs = null;
    private Map<Integer, Object> outputs = null;

    private List<String> labelList;

    Activity activity;
    /**
     * Initializes an {@code ImageClassifier}.
     *
     * @param activity
     */
    BpMultipleClassifier(Activity activity, boolean gpuDelegate) throws IOException {
        this.activity = activity;
        tfliteOptions = new Interpreter.Options();
        tfliteOptions.setUseNNAPI(false);
        if (gpuDelegate){
            tfliteOptions.addDelegate(new GpuDelegate());
        }
        labelList = loadLabelList(activity);

        tflite = new Interpreter(loadModelFile(activity), tfliteOptions);
        input1 = ByteBuffer.allocateDirect(1 * 1000 * 1);
        input1.order(ByteOrder.nativeOrder());
        input2 = ByteBuffer.allocateDirect(1 * 1000 * 1);
        input2.order(ByteOrder.nativeOrder());
        input3 = ByteBuffer.allocateDirect(1 * 2 * 1);
        input3.order(ByteOrder.nativeOrder());
        input4 = ByteBuffer.allocateDirect(1 * 1000 * 1);
        input4.order(ByteOrder.nativeOrder());
        input5 = ByteBuffer.allocateDirect(1 * 1000 * 1);
        input5.order(ByteOrder.nativeOrder());
        input6 = ByteBuffer.allocateDirect(1 * 2 * 1);
        input6.order(ByteOrder.nativeOrder());

        loadIO();
        Log.d(TAG, "Created a Tensorflow Lite Bp Multiple Classifier.");

    }

    private void loadIO() throws IOException {
        input1.rewind();
        input2.rewind();
        input3.rewind();
        input4.rewind();
        input5.rewind();
        input6.rewind();
        input1 = loadInputFile(activity, "input1.bin");
        input2 = loadInputFile(activity, "input2.bin");
        input3 = loadInputFile(activity, "input3.bin");
        input4 = loadInputFile(activity, "input4.bin");
        input5 = loadInputFile(activity, "input5.bin");
        input6 = loadInputFile(activity, "input6.bin");

        inputs = new Object[]{input1, input2, input3, input4, input5, input6};

        outputs = new HashMap<>();
        outputs.put(0, new float[1][1]);
        outputs.put(1, new float[1][1]);
        outputs.put(2, new float[1][1]);
        outputs.put(3, new float[1][1]);
    }

    /**
     * Memory-map the model file in Assets.
     */
    private MappedByteBuffer loadModelFile(Activity activity) throws IOException {
        AssetFileDescriptor fileDescriptor = activity.getAssets().openFd(getModelPath());
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }

    /**
     * Memory-map the model file in Assets.
     */
    private MappedByteBuffer loadInputFile(Activity activity, String inputName) throws IOException {
        AssetFileDescriptor fileDescriptor = activity.getAssets().openFd(inputName);
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }


    protected String getLabelPath() {
        return "bp_labels.txt";
    }

    protected String getModelPath() {
//        return "tflite_model.tflite";
        return "tflite_model_optim.tflite";
    }


    protected void runInference() {
        tflite.runForMultipleInputsOutputs(inputs, outputs);
    }


    /**
     * Get the total number of labels.
     *
     * @return
     */
    protected int getNumLabels() {
        return labelList.size();
    }

    /**
     * Reads label list from Assets.
     */
    private List<String> loadLabelList(Activity activity) throws IOException {
        List<String> labelList = new ArrayList<String>();
        BufferedReader reader =
                new BufferedReader(new InputStreamReader(activity.getAssets().open(getLabelPath())));
        String line;
        while ((line = reader.readLine()) != null) {
            labelList.add(line);
        }
        reader.close();
        return labelList;
    }

    ArrayList<String> classifyFrame() {

        ArrayList<String> dataList = new ArrayList<>();

        try {
            loadIO();
        } catch (IOException e) {
            Log.e(TAG, "io初始化失败");
            e.printStackTrace();
            return dataList;
        }


        if (tflite == null) {
            Log.e(TAG, "Image classifier has not been initialized; Skipped.");
        }

        long startTime = SystemClock.uptimeMillis();
        runInference();
        long endTime = SystemClock.uptimeMillis();

        long costTime = endTime - startTime;
        Log.d(TAG, "Timecost to run model inference: " + Long.toString(costTime));

        //打印结果
        StringBuilder sb = new StringBuilder();
        int size = outputs.size();
        for (int i = 0; i < size; i++) {
            float[][] floats = (float[][]) outputs.get(i);

            String result = String.format("dataPos=%d,result:%d,value:%f", 0, i, floats[0][0]);

            sb.append(result).append("\n");
        }
        sb.append(String.format("time=%d", costTime));
        dataList.add(sb.toString());

        return dataList;
    }

}
