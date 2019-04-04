package com.caotu.duanzhi.module.detail_scroll;

import com.caotu.duanzhi.Http.bean.MomentsDataBean;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
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
     *
     * @param srcList
     * @param <T>
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
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
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
        return null;
    }
}
