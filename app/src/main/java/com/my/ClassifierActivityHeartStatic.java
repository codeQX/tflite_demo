///*
// * Copyright 2016 The TensorFlow Authors. All Rights Reserved.
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *       http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//
//package com.my;
//
//import android.app.Activity;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.Message;
//import android.os.SystemClock;
//import android.text.SpannableStringBuilder;
//import android.text.TextUtils;
//import android.view.View;
//import android.widget.AbsListView;
//import android.widget.Button;
//import android.widget.CompoundButton;
//import android.widget.EditText;
//import android.widget.ListView;
//import android.widget.Toast;
//import android.widget.ToggleButton;
//
//import com.example.android.tflitecamerademo.R;
//
//import java.io.IOException;
//import java.util.ArrayList;
//
///**
// * 动态数据测试
// */
//public class ClassifierActivityHeartStatic extends Activity {
//
//    private static final int MSG_UPDATE_DATA = 10;
//    private static final int MSG_FAIL = 11;
//    private static final int MSG_SUCCESS = 12;
//
//    private static final int IMAGE_MEAN = 0;
//    private static final float IMAGE_STD = 1;
//    private static final String INPUT_NAME = "input_node";
//    private static final String OUTPUT_NAME = "myoutputnode";
//
//    private static final String MODEL_FILE = "file:///android_asset/static/static1.pb";//  static1.pb
//    private static final String MODEL_DATA = "static/static_data.dat";
//    private static final String LABEL_FILE =
//            "file:///android_asset/static/heart_result.txt";
//
//    ListView dataListView;
//    DataAdapter dataAdapter;
//    ArrayList<BeanData> dataList = new ArrayList<BeanData>();
//    int mNumber = 1;
//    int INPUT_SIZE = 16800;
//
//    BaseClassifier baseClassifier;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_heart);
//
//        init();
//    }
//
//    @Override
//    protected void onDestroy() {
//
//        if(baseClassifier != null){
//            baseClassifier.close();
//        }
//        super.onDestroy();
//    }
//
//    //=========================
//    private void init() {
//        dataListView = (ListView) findViewById(R.id.dataListView);
//        dataAdapter = new DataAdapter(this, dataList);
//        dataListView.setAdapter(dataAdapter);
//
//        try {
//            baseClassifier = new HeartStaticClassifierQuantizedMobileNet(this);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        ToggleButton toggleButton = findViewById(R.id.toggleButton);
//        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                baseClassifier.setUseNNAPI(isChecked);
//            }
//        });
//    }
//
//    public void viewClick(View view) {
//        int rid = view.getId();
//        switch (rid) {
//            case R.id.computeButton:
//
//                EditText contentEditText = (EditText) findViewById(R.id.contentEditText);
//                String content = contentEditText.getText().toString().trim();
//
//                if(TextUtils.isEmpty(content)){
//                    Toast.makeText(this,"empty",Toast.LENGTH_SHORT).show();
//                    return;
//                }
//
//                int number = 1;
//                try {
//                    number = Integer.parseInt(content);
//                }catch (Exception e){
//                    e.printStackTrace();
//                }
//
//                if(number > 10 || number < 1){
//                    Toast.makeText(this,"支持1-10的整数",Toast.LENGTH_SHORT).show();
//                    return;
//                }
//
//                mNumber = number;
//                INPUT_SIZE = 16800;
//                INPUT_SIZE *= mNumber;
//
//                if (dataList.size() > 0) {
//                    dataList.clear();
//                    dataAdapter.notifyDataSetChanged();
//                }
//                compute();
//                view.setEnabled(false);
//                mHandler.sendEmptyMessage(MSG_SUCCESS);
//                break;
//            default:
//                break;
//        }
//    }
//
//    private void compute() {
//
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//
//                    SpannableStringBuilder textToShow = new SpannableStringBuilder();
//
//                    byte[] dataArray = Utils.getFromAssets(ClassifierActivityHeartStatic.this, MODEL_DATA);
//                    int tempValue = 16801*4*mNumber;
//                    int dataLength = dataArray.length / tempValue;//数据长度
//                    for (int i = 0; i < dataLength; i++) {
//                        final long startTime = SystemClock.uptimeMillis();
//
//                        float[] array = new float[INPUT_SIZE];
//                        for (int j = 0; j < INPUT_SIZE; j++) {
//                            byte[] tempArray = new byte[4];
//                            int index = (j*4 + i * tempValue + 0) + 4;
//                            System.arraycopy(dataArray,index,tempArray,0,4);
//                            array[j] = Utils.byte2float(tempArray,0);
//                        }
//
//                        baseClassifier.classifyFrame(null);
//                        long lastProcessingTimeMs = SystemClock.uptimeMillis() - startTime;
//
//                        if (textToShow.length() > 0) {
//
////                            for(int k=0;k<results.size();k++){
////                                BeanData item = new BeanData();
////                                item.result = results.get(k).toString();
////                                item.time = lastProcessingTimeMs;
////                                dataList.add(item);
////                            }
////                            mHandler.sendEmptyMessage(MSG_UPDATE_DATA);
//                        }
//                    }
//
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    mHandler.sendEmptyMessage(MSG_FAIL);
//                }
//            }
//        }).start();
//    }
//
//    Handler mHandler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            switch (msg.what) {
//                case MSG_UPDATE_DATA:
//
//                    dataListView.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
//                    dataAdapter.notifyDataSetChanged();
//                    break;
//                case MSG_FAIL:
//                    Button computeButton = (Button) findViewById(R.id.computeButton);
//                    computeButton.setEnabled(true);
//                    Toast.makeText(ClassifierActivityHeartStatic.this,"异常",Toast.LENGTH_LONG).show();
//                    break;
//                case MSG_SUCCESS:
//                    Toast.makeText(ClassifierActivityHeartStatic.this,"成功,正在计算,请稍等",Toast.LENGTH_LONG).show();
//                    break;
//                default:
//                    break;
//            }
//        }
//    };
//
//}
