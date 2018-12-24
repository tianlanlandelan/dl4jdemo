import org.apache.commons.io.FileUtils;
import org.datavec.api.records.reader.RecordReader;
import org.datavec.api.records.reader.impl.csv.CSVRecordReader;
import org.datavec.api.records.writer.RecordWriter;
import org.datavec.api.records.writer.impl.csv.CSVRecordWriter;
import org.datavec.api.split.FileSplit;
import org.datavec.api.split.partition.NumberOfRecordsPartitioner;
import org.datavec.api.split.partition.Partitioner;
import org.datavec.api.transform.TransformProcess;
import org.datavec.api.transform.condition.ConditionOp;
import org.datavec.api.transform.condition.column.CategoricalColumnCondition;
import org.datavec.api.transform.condition.column.DoubleColumnCondition;
import org.datavec.api.transform.filter.ConditionFilter;
import org.datavec.api.transform.schema.Schema;
import org.datavec.api.transform.transform.time.DeriveColumnsFromTimeTransform;
import org.datavec.api.writable.DoubleWritable;
import org.datavec.api.writable.Writable;
import org.datavec.local.transforms.LocalTransformExecutor;
import org.joda.time.DateTimeFieldType;
import org.joda.time.DateTimeZone;
import oshi.util.FileUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

/**
 * @author yangkaile
 * @date 2018-12-19 15:09:32
 */
public class DataVecDemo {
    public static void main(String[] args) throws Exception{
//        defineSchema();
//        defineTransformProcessedSchema();
        transformFile();
    }
    /**
     * 定义数据模式
     * @return Schema定义好的数据模式
     */
    public static Schema defineSchema(){
        //定义要导入的数据模式，列的定义要与数据源中的数据的顺序保持一致
        Schema inputDataSchema = new Schema.Builder()
                //定义一个String类型的列
                .addColumnString("DateTimeString")
                //定义相同类型的多列数据
                .addColumnsString("CustomerID", "MerchantID")
                .addColumnInteger("NumItemsInTransaction")
                //定义分类列，指定列名的同时指定分类
                .addColumnCategorical("MerchantCountryCode", Arrays.asList("USA","CAN","FR","MX"))
                .addColumnDouble("TransactionAmountUSD",0.0,null,false,false)
                .addColumnCategorical("FraudLabel", Arrays.asList("Fraud","Legit"))
                .build();
        CommonUtils.println("Input data schema",inputDataSchema);
        CommonUtils.println("Number of columns",inputDataSchema.numColumns());
        CommonUtils.println("Column names",inputDataSchema.getColumnNames());
        CommonUtils.println("Column types",inputDataSchema.getColumnTypes());

        return inputDataSchema;


    }

    /**
     * 将原始数据模式进行转换处理，生成新的数据模式
     * @return TransformProcess 处理好的TransformProcess对象
     */
    public static TransformProcess defineTransformProcessedSchema(){
        TransformProcess transformProcess = new TransformProcess.Builder(defineSchema())
                //删除不需要的列，可以是一列也可以是多列
                .removeColumns("CustomerID","MerchantID")

                //过滤列中不需要的数据
                .filter(new ConditionFilter(
                        new CategoricalColumnCondition("MerchantCountryCode", ConditionOp.NotInSet, new HashSet<String>(Arrays.asList("USA","CAN")))))

                //将0以下的数据替换为默认值0
                .conditionalReplaceValueTransform(
                        //要操作的列
                        "TransactionAmountUSD",
                        //设置默认值
                        new DoubleWritable(0.0),
                        //设置替换条件 此处的条件为：TransactionAmountUSD列中小于0的值
                        new DoubleColumnCondition("TransactionAmountUSD",ConditionOp.LessThan, 0.0))

                //日期格式化，将文本型的日期格式化为时间戳，方便后续处理
                .stringToTimeTransform("DateTimeString","YYYY-MM-DD HH:mm:ss.SSS", DateTimeZone.UTC)

                //列重命名
                .renameColumn("DateTimeString", "DateTime")

                //从DateTime列的基础上派生出新的列HourOfDay
                .transform(new DeriveColumnsFromTimeTransform.Builder("DateTime")
                        .addIntegerDerivedColumn("HourOfDay", DateTimeFieldType.hourOfDay())
                        .build())

                //生成设置好的TransformProcess 对象
                .build();
        //转换后的数据模式
        CommonUtils.println("Schema after transforming data",transformProcess.getFinalSchema());

        return  transformProcess;
    }

    /**
     * 处理文件
     */
    public static void transformFile() throws Exception{
        //输入文件
        File inputFile = CommonUtils.getFile("BasicDataVecExample/exampledata.csv");
        //输出文件
        File outputFile = new File("BasicDataVecExampleLocalOut.csv");
        if(outputFile.exists()){
            outputFile.delete();
        }
        outputFile.createNewFile();

        //读文件
        RecordReader recordReader = new CSVRecordReader(0, ',');
        recordReader.initialize(new FileSplit(inputFile));

        //写文件
        RecordWriter recordWriter = new CSVRecordWriter();
        Partitioner p = new NumberOfRecordsPartitioner();
        recordWriter.initialize(new FileSplit(outputFile), p);

        //过程数据
        List<List<Writable>> originalData = new ArrayList<List<Writable>>();
        while(recordReader.hasNext()){
            originalData.add(recordReader.next());
        }

        //数据转换
        List<List<Writable>> processedData = LocalTransformExecutor.execute(originalData, defineTransformProcessedSchema());
        recordWriter.writeBatch(processedData);
        recordWriter.close();


        //Print before + after:
        System.out.println("\n\n---- Original Data File ----");
        String originalFileContents = FileUtils.readFileToString(inputFile,"UTF8");
        System.out.println(originalFileContents);

        System.out.println("\n\n---- Processed Data File ----");
        String fileContents = FileUtils.readFileToString(outputFile,"UTF8");
        System.out.println(fileContents);

        System.out.println("\n\nDONE");
    }
}
