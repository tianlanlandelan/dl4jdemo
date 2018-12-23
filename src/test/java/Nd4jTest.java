import org.junit.Test;
import org.nd4j.linalg.api.ndarray.INDArray;

import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.factory.Nd4jBackend;
/**
 * ND4J是JVM的科学计算库，运行速度快，RAM要求低
 * 多用途，多维数组对象
 * 多平台功能，包括GPU
 * 线性代数和信号处理功能
 *
 * @author yangkaile
 * @date 2018-12-17 14:55:15
 */
public class Nd4jTest {


    /**
     * 矩阵创建与控制
     */
    @Test
    public void testCreat(){
        //生成一个全0二维矩阵
        INDArray tensorA =  Nd4j.zeros(4,5);
        println("全0二维矩阵",tensorA);

        //生成一个全1二维矩阵
        INDArray tensorB =  Nd4j.ones(4,5);
        println("全1二维矩阵",tensorB);

        //生成一个全1二维矩阵
        INDArray tensorC =  Nd4j.rand(4,5);
        println("随机二维矩阵",tensorC);


        println("水平拼接若干矩阵，矩阵必须有相同的行数", Nd4j.hstack(tensorA,tensorB));
        println("垂直拼接若干矩阵，矩阵必须有相同的列数", Nd4j.vstack(tensorA,tensorB));
    }

    /**
     * 矩阵加减乘除运算
     */
    @Test
    public void Nd4jTest2d() {
        //查看矩阵计算后台是cpu还是gpu的
        Nd4jBackend backend = Nd4j.getBackend();
        System.out.println("====");
        System.out.println(backend.toString());

        INDArray  tensorA = Nd4j.create(new int[]{2,3});
        println("创建空的2*3矩阵tensorA",tensorA);

        INDArray  tensorB = Nd4j.rand(new int[]{2,3});
        println("创建随机的2*3矩阵 tensorB",tensorB);


        println("矩阵元素加上一个标量",tensorA.add(10));

        println("矩阵相加",tensorA.add(tensorB));

        println("矩阵元素加上标量后覆盖原矩阵tensorA",tensorA.addi(10));

        println("矩阵相减",tensorA.sub(tensorB));

        println("矩阵对应元素相乘",tensorA.mul(tensorB));

        println("矩阵元素除以一个标量",tensorA.div(2));

        println("矩阵对应元素相除",tensorA.div(tensorB));

        println("矩阵转置",tensorB.transpose());

        println("矩阵转置后替换原矩阵tensorB",tensorB.transposei());

        /*
        矩阵A*B=C
        需要注意：
        1、当矩阵A的列数等于矩阵B的行数时，A与B可以相乘。
        2、矩阵C的行数等于矩阵A的行数，C的列数等于B的列数。（ A:2,3; B:3,4; C:2,4 ）
        3、乘积C的第m行第n列的元素等于矩阵A的第m行的元素与矩阵B的第n列对应元素乘积之和。

         */
        println("矩阵相乘",tensorA.mmul(tensorB));



    }



    /**
     * 三维矩阵
     */
    @Test
    public void Nd4jTest3d(){
        //创建一个三维矩阵 2*3*4
        INDArray tensor3d_1 = Nd4j.create(new int[]{2,3,4});
        println("创建空的三维矩阵",tensor3d_1);

        //创建一个随机的三维矩阵 2*3*4
        INDArray tensor3d_2 =  Nd4j.rand(new int[]{2,3,4});
        println("创建随机三维矩阵",tensor3d_2);

        //矩阵的每个元素减去一个标量后覆盖原矩阵
        println("矩阵元素减去一个标量",tensor3d_1.subi(-5));

        //矩阵相减
        println("三维矩阵相减",tensor3d_1.sub(tensor3d_2));

    }

    public void println(String text,INDArray indArray){
        System.out.println("====" + text + "===");
        System.out.println(indArray);
    }


}

