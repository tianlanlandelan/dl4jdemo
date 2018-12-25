package com.yangkaile.dl4j.learn;

import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.nd4j.linalg.dataset.DataSet;

import java.io.File;
import java.util.List;

public abstract class BaseMlp {

     public abstract MultiLayerNetwork createModel();

     public abstract List<DataSet> loadData(File file);
}
