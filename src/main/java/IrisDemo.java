import org.datavec.api.records.reader.RecordReader;
import org.datavec.api.records.reader.impl.csv.CSVRecordReader;
import org.datavec.api.split.FileSplit;
import org.deeplearning4j.api.storage.StatsStorage;
import org.deeplearning4j.datasets.datavec.RecordReaderDataSetIterator;
import org.deeplearning4j.datasets.iterator.impl.ListDataSetIterator;
import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.deeplearning4j.ui.api.UIServer;
import org.deeplearning4j.ui.stats.StatsListener;
import org.deeplearning4j.ui.storage.InMemoryStatsStorage;
import org.deeplearning4j.util.ModelSerializer;
import org.nd4j.evaluation.classification.Evaluation;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.SplitTestAndTrain;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.learning.config.Nesterovs;
import org.nd4j.linalg.lossfunctions.LossFunctions;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * 这个例子演示MLP分类（Iris 鸢尾花分类问题）
 * @author yangkaile
 * @date 2018-12-23 12:01:02
 *
 */
public class IrisDemo {
    /**
     * batchSize 是每个批次的训练数据大小。
     */
    private static int batchSize = 3;
    /**
     * labelColIndex 是“指定 CSV 文件中第几列是标注”。
     */
    private static int labelColIndex = 4;
    /**
     * numClasses 是分类的类别数目。
     */
    private static int numClasses = 3;


    public static DataSetIterator loadIrisIter(File file) throws Exception {

        RecordReader recordReader = new CSVRecordReader();
        recordReader.initialize(new FileSplit(file));
        DataSetIterator iterator = new RecordReaderDataSetIterator(recordReader, batchSize, labelColIndex, numClasses);

        return iterator;
    }

    /**
     * 我们还定义了很多超参数，如学习率、参数的初始分布、优化算法及优化器、激活函数，默认使用 BP 反向传播算法等。这些超参数对最后网络参数的收敛有直接影响，
     * @return
     */
    public static MultiLayerNetwork model(){
        MultiLayerConfiguration.Builder builder = new NeuralNetConfiguration.Builder()
                .seed(12345)
                .weightInit(WeightInit.XAVIER)
                .optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
                .updater(new Nesterovs(0.01, 0.9))
                .list()
                .layer(0, new DenseLayer.Builder().activation(Activation.RELU)
                        .nIn(4).nOut(2).build())
                .layer(1, new OutputLayer.Builder(LossFunctions.LossFunction.MCXENT)
                        .activation(Activation.SOFTMAX)
                        .nIn(2).nOut(3).build());

        MultiLayerConfiguration conf = builder.build();
        MultiLayerNetwork model = new MultiLayerNetwork(conf);
        return model;
    }


    public static void main(String[] args) throws Exception{
        /*--------------超参数常量声明------------------*/
        final int batchSize = 3;
        final long SEED = 1234L;
        final int trainSize = 120;
        /*--------------数据集构建------------------*/
        DataSetIterator dataSetIterator = loadIrisIter(CommonUtils.getFile("demo/iris.data"));
        List<DataSet> irisList = new ArrayList<>();
        int j = 0;
        while (dataSetIterator.hasNext()){
            System.out.println(j ++);
            irisList.add(dataSetIterator.next());
        }
        DataSet allData = DataSet.merge(irisList);
        allData.shuffle(SEED);
        SplitTestAndTrain split = allData.splitTestAndTrain(trainSize);
        DataSet dsTrain = split.getTrain();
        DataSet dsTest = split.getTest();
        DataSetIterator trainIter = new ListDataSetIterator(dsTrain.asList() , batchSize);
        DataSetIterator testIter = new ListDataSetIterator(dsTest.asList() , batchSize);

        MultiLayerNetwork mlp = model();
        //loss score 监听器
        mlp.setListeners(new ScoreIterationListener(1));

        //使用 Deeplearning4j 内置的 UI 页面进行观察
        //当开始训练后，我们在浏览器中键入 http://localhost:9000/train/overview，即可看到当前训练的情况。
//        UIServer uiServer = UIServer.getInstance();
//        StatsStorage statsStorage = new InMemoryStatsStorage();
//        uiServer.attach(statsStorage);
//        mlp.setListeners(new StatsListener(statsStorage));

        for( int i = 0; i < 20; ++i ){
            //训练模型
            mlp.fit(trainIter);
            trainIter.reset();
            //在验证集上进行准确性测试
            Evaluation eval = mlp.evaluate(testIter);
            System.out.println(eval.stats());
            testIter.reset();
        }



        //保存模型
        ModelSerializer.writeModel(mlp, new File("mlp.mod"), true);
    }

}
