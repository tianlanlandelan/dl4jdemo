import org.junit.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

public class FileTest {

    @Test
    public void readLine() throws Exception{
        // 使用ArrayList来存储每行读取到的字符串
        ArrayList<String> arrayList = new ArrayList<String>();
        FileReader fr = new FileReader(CommonUtils.getFile("demo/house-price.cvs"));
        BufferedReader bf = new BufferedReader(fr);
        String str;
        // 按行读取字符串
        while ((str = bf.readLine()) != null) {
            arrayList.add(str);
        }
        bf.close();
        fr.close();

        for(int i = 0 ; i < arrayList.size() ; i ++){
            if(i % 2 == 0){
                System.out.println(arrayList.get(i) + arrayList.get(i + 1));
            }

        }



    }
}
