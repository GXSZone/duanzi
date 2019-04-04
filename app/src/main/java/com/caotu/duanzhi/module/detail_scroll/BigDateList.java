package com.caotu.duanzhi.module.detail_scroll;

import com.caotu.duanzhi.Http.bean.MomentsDataBean;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

public class BigDateList {
    private static final BigDateList ourInstance = new BigDateList();
    private ArrayList<MomentsDataBean> beans;

    public static BigDateList getInstance() {
        return ourInstance;
    }

    private BigDateList() {
    }

    public void setBeans(ArrayList<MomentsDataBean> beanList) {
        beans = beanList;
    }

    public List<MomentsDataBean> getBeans() {
        if (beans == null || beans.size() == 0) return null;
        return depCopy(beans);
    }

    public void clearBeans() {
        if (beans != null) {
            beans.clear();
            beans = null;
        }
    }

    /***
     * 方法一对集合进行深拷贝 注意需要对泛型类进行序列化(实现Serializable)
     *link { https://blog.csdn.net/mxd446814583/article/details/80355572 } -----对try--catch---finally解释例子
     * 1  try、catch、finally语句中，如果只有try语句有return返回值，此后在catch、finally中对变量做任何的修改，都不影响try中return的返回值。
     *
     * 2、try、catch中有返回值，而try中抛出的异常恰好与catch中的异常匹配，则返回catch中的return值。
     *
     * 3  如果finally块中有return 语句，则返回try或catch中的返回语句忽略。
     *
     * 4  如果finally块中抛出异常，则整个try、catch、finally块中抛出异常.并且没有返回值。
     *
     *  
     *
     * 所以在使用try、catch、finally语句块时需要注意以下几点：
     *
     * 1 尽量在try或者catch中使用return语句。通过finally块中达到对try或者catch返回值修改是不可行的。
     *
     * 2 finally块中避免使用return语句，因为finally块中如果使用return语句，会显示的忽略掉try、catch块中的异常信息，屏蔽了错误的发生。
     *
     * 3 finally块中避免再次抛出异常，否则整个包含try语句块的方法回抛出异常，并且会忽略掉try、catch块中的异常。
     * @param * @param <T>
     * @return
     */
    public static <T> List<T> depCopy(List<T> srcList) {
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        ObjectOutputStream out = null;
        ByteArrayInputStream byteIn = null;
        ObjectInputStream inStream = null;
        try {
            out = new ObjectOutputStream(byteOut);
            out.writeObject(srcList);

            byteIn = new ByteArrayInputStream(byteOut.toByteArray());
            inStream = new ObjectInputStream(byteIn);
            List<T> destList = (List<T>) inStream.readObject();
            return destList;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                byteOut.close();
                if (out != null) {
                    out.close();
                }
                if (byteIn != null) {
                    byteIn.close();
                }
                if (inStream != null) {
                    inStream.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return srcList;
    }
}
