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

import java.io.IOException;

/**
 * This classifier works with the Inception-v3 slim model.
 * It applies floating point inference rather than using a quantized model.
 */
public class MnistClassifer extends BaseClassifier {

  /**
   * The inception net requires additional normalization of the used input.
   */
  private static final int IMAGE_MEAN = 128;
  private static final float IMAGE_STD = 128.0f;

  /**
   * An array to hold inference results, to be feed into Tensorflow Lite as outputs.
   * This isn't part of the super class, because we need a primitive array here.
   */
  private float[][] labelProbArray = null;

  /**
   * Initializes an {@code ImageClassifier}.
   *
   * @param activity
   */
  MnistClassifer(Activity activity,String modelFilePath,String dataFilePath,int batchSize,boolean nnApi) throws IOException {
    super(activity,modelFilePath,dataFilePath,batchSize,nnApi);
    //initLabelProbArray();
  }

  @Override
  protected String getLabelPath() {
    return "mnist_labels.txt";
  }

  @Override
  protected String getModelPath() {
    // you can download this file from

    //return "mnist.tflite"; //正确
    //return "mnist-same.tflite";
    //return "mnist-1d.tflite";
    return modelFilePath;
  }

  @Override
  protected  String getTestDataPath()
  {
    //return "mnist_test.dat";
    return dataFilePath;
  }

  @Override
  protected int getBatchSize(){
    return 1;
    //return batchSize;
  }

  @Override
  protected int getImageSizeX() {
    return 28;
  }

  @Override
  protected int getImageSizeY() {
    return 28;
  }

  @Override
  protected int getNumBytesPerChannel() {
    // a 32bit float value requires 4 bytes
    return 4;
  }

  protected int getChannelNum()
  {
    return 1;
  }


  @Override
  protected void addPixelValue(float pixelValue) {
    imgData.putFloat(pixelValue);
  }

  @Override
  protected float getProbability(int index,int labelIndex) {
    return labelProbArray[index][labelIndex];
  }

  @Override
  protected void setProbability(int labelIndex, Number value) {
    labelProbArray[0][labelIndex] = value.floatValue();
  }

  @Override
  protected float getNormalizedProbability(int index,int labelIndex) {
    // TODO the following value isn't in [0,1] yet, but may be greater. Why?
    return getProbability(index,labelIndex);
  }

  @Override
  protected void runInference() {
    tflite.run(imgData, labelProbArray);

  }

  protected float[][] getLabelProbArray(){
    return labelProbArray;
  }

  protected void initLabelProbArray(){
    labelProbArray = new float[getBatchSize()][getNumLabels()];// output shape
  }
}
