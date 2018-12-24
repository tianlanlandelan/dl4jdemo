import org.datavec.api.records.reader.RecordReader;
import org.datavec.api.records.reader.impl.csv.CSVRecordReader;
import org.datavec.api.split.CollectionInputSplit;
import org.datavec.api.split.FileSplit;
import org.datavec.api.split.NumberedFileInputSplit;
import org.deeplearning4j.datasets.datavec.RecordReaderDataSetIterator;
import org.junit.Test;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.io.ClassPathResource;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.*;

/**
 * DataVec：向量化及表达式模板库
 * DataVec帮助克服机器学习及深度学习实现过程中最重大的障碍之一：将数据转化为神经网络能够识别的格式。
 * 神经网络所能识别的是向量。因此，对许多数据科学家而言，在开始用数据定型自己的算法之前，首先必须要解决向量化的问题。
 * 如果您的数据以CSV（逗号分隔值）格式储存在平面文件中，必须先转换为数值格式再加以摄取，
 * 又或者您的数据是一些有标签的图像的目录结构，那么DataVec这款工具就可以帮助您组织数据，以供在Deeplearning4J中使用。
 *
 * DataVec采用输入/输出格式系统（就像Hadoop MapReduce用InputFormat来确定具体的InputSplit和RecordReader一样，DataVec也会用不同的RecordReader来将数据序列化）
 * 支持所有主要的输入数据类型（文本、CSV、音频、图像、视频），每种类型都有相应的输入格式
 * 采用输出格式系统来指定一种与实现无关的向量格式（ARFF、SVMLight等）
 * 可以为特殊输入格式（如某些罕见的图像格式）进行扩展；也就是说，您可以编写自定义的输入格式，让余下的基本代码来处理转换加工管道
 * 让向量化成为“一等公民”
 * 内置数据转换及标准化工具
 *
 * @author yangkaile
 * @date 2018-12-17 14:55:34
 */
public class DataVecTest {

    String rootPath = "files/";




    /**
     * 递归加载文件--默认加载方式
     * @throws Exception
     */
    @Test
    public void loadDefault(){
        //递归加载文件夹下所有文件
        FileSplit fileSplit = new FileSplit(CommonUtils.getFile(rootPath));

        System.out.println("--------------- Example 1: Loading every file ---------------");
        URI[] fileSplitUris = fileSplit.locations();
        for (URI uri: fileSplitUris) {
            System.out.println(uri);
        }
    }

    /**
     * 非递归加载文件
     */
    @Test
    public void loadNonRecursively(){
        //三个参数：根目录 过滤规则 是否递归
        FileSplit fileSplit2 = new FileSplit(CommonUtils.getFile(rootPath), null, false);

        System.out.println("--------------- Example 2: Loading non-recursively ---------------");
        URI[] fileSplit2Uris = fileSplit2.locations();
        for (URI uri: fileSplit2Uris) {
            System.out.println(uri);
        }
    }

    /**
     * 过滤文件
     */
    @Test
    public void loadWithFilter(){
        String[] extensionsToFilter = new String[]{".jpg"};
        FileSplit fileSplit3 = new FileSplit(CommonUtils.getFile(rootPath), extensionsToFilter, false);

        System.out.println("--------------- Example 3: Loading with filters ---------------");
        URI[] fileSplit3Uris = fileSplit3.locations();
        for (URI uri: fileSplit3Uris) {
            System.out.println(uri);
        }
    }

    /**
     * 使用随机种子加载
     * 这将递归和随机加载所有文件，由随机种子指定。
     * 只有在使用迭代器时，随机化才会得到反映。
     */
    @Test
    public void loadWithRandomSeed(){
        FileSplit fileSplit4 = new FileSplit(CommonUtils.getFile(rootPath), null, new Random(222));

        System.out.println("--------------- Example 4: Loading with a random seed ---------------");
        Iterator<URI> fileSplit4UrisIterator = fileSplit4.locationsIterator();
        while (fileSplit4UrisIterator.hasNext()) {
            System.out.println(fileSplit4UrisIterator.next());
        }
    }

    /**
     * 加载单文件
     * @throws Exception
     */
    @Test
    public void loadSingleFile() throws Exception{
        FileSplit fileSplit5 = new FileSplit(
                new ClassPathResource("files/cats/domestic_cat_s_001970.jpg").getFile()
        );

        System.out.println("--------------- Example 5: FileSplit with a single file ---------------");
        Iterator<URI> fileSplit5UrisIterator = fileSplit5.locationsIterator();
        while (fileSplit5UrisIterator.hasNext()) {
            System.out.println(fileSplit5UrisIterator.next());
        }
    }

    /**
     *
     */
    @Test
    public void collectionInputSplitTest(){
        /*
          Creating a FileSplit this just to receive a list of URIs. From those URIs we'll create the CollectionInputSplit.
         */
        FileSplit fileSplit = new FileSplit(CommonUtils.getFile(rootPath), new String[]{"jpg"}, false);

        /*
          Now you can create the CollectionInputSplit and print it as follows.
         */
        CollectionInputSplit collectionInputSplit = new CollectionInputSplit(fileSplit.locations());
        System.out.println("--------------- Printing the input splits from CollectionInputSplit ---------------");
        Iterator<URI> collectionInputSplitIterator = collectionInputSplit.locationsIterator();
        while (collectionInputSplitIterator.hasNext()) {
            System.out.println(collectionInputSplitIterator.next());
        }
    }

    /**
     * 文件名是相同结构不同编号时，可以使用NumberedFileInputSplit的方式读取
     */
    @Test
    public void NumberedFileInputSplitTest(){
        /*
        file1.*
        file2.*
        对于这样的命名格式的文件，可以这样使用
        NumberedFileInputSplit(String baseString, int minIdxInclusive, int maxIdxInclusive)三个参数分别是：
        格式化文件名，要读取文件的最小编号，要读取文件的最大编号
         */
        NumberedFileInputSplit split1 = new NumberedFileInputSplit("file%house-price.cvs.txt",
                1,
                5);

        System.out.println("--------------- Example 1: Loading simple numbered files ---------------");
        URI[] split1Uris = split1.locations();
        for (URI uri: split1Uris) {
            System.out.println(uri);
        }

        /*
        file001.*
        file002.*
        对于这样的命名格式的文件，可以这样使用
         */
        NumberedFileInputSplit split2 = new NumberedFileInputSplit("/path/to/files/prefix-%03d.suffix",
                1,
                5);

        System.out.println("--------------- Example 2: Loading files with leading zeros ---------------");
        URI[] split2Uris = split2.locations();
        for (URI uri: split2Uris) {
            System.out.println(uri);
        }
    }

    @Test
    public void loadIrisIter() throws IOException, InterruptedException {
        File file = CommonUtils.getFile("demo/iris.data");
        int batchSize = 3;
        //第5列是标注
        int labelColIndex = 4;
        //三个分类 分类标识应该是0，1，2 要不然会报错
        int numClasses = 3;
        RecordReader recordReader = new CSVRecordReader();
        recordReader.initialize(new FileSplit(file));
        /*
        batchSize 是每个批次的训练数据大小。labelColIndex 是“指定 CSV 文件中第几列是标注”。numClasses 是分类的类别数目。
         */
        DataSetIterator iterator = new RecordReaderDataSetIterator(recordReader, batchSize, labelColIndex, numClasses);
        while (iterator.hasNext()){
            System.out.println(iterator.next());
        }
    }

}
