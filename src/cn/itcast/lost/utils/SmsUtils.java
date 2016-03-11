package cn.itcast.lost.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.SystemClock;
import android.util.Xml;

import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileOutputStream;

/**
 * 短信备份的工具类
 * Created by wolfnx on 2016/2/21.
 */
public class SmsUtils {

    /**
     * 由于经常会改需求所以需要将工具类封装
     * 备份短信接口
     */
    public interface BackUpCallBackSms{

        public void befor(int count);

        public void onBackUpSms(int process);

    }
    /**
     * 目的：备份短信
     * 1.想把短信备份到SD卡，那首先就要判断是否存在SD卡
     * 2.由于短信的权限全都是---所以不能直接去读，就要使用内容观察者
     * 3.写短息（写到SD卡）
     * 此处需要权限：
     * <uses-permission android:name="android.permission.READ_SMS" />
     * <uses-permission android:name="android.permission.WRITE_SMS" />
     * @param context
     *
     * @return
     */
    public static boolean backUp(Context context, BackUpCallBackSms callBack){
       //判断SD卡的状态
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){

            //通过设置里看一下是否还有存储空间
            ContentResolver resolver = context.getContentResolver();
            //获取短信的路径
            Uri uri = Uri.parse("content://sms/");
            //type=1接收短信
            //type=2发送短信
            Cursor cursor = resolver.query(uri, new String[]{"address", "date", "type", "body"}, null, null, null);
            //获取总共有多少条短信
            int count = cursor.getCount();
            //设置progressDialog的最大值
            //progressDialog.setMax(count);
            callBack.befor(count);

            int process=0;


            try {
                //把短信备份SD卡，第二个参数表示名字
                File file= new File(Environment.getExternalStorageDirectory(),"backup.xml");
                FileOutputStream os = new FileOutputStream(file);
                //由于要把查出来的短信变得格式化的保存，所以获取一个序列化器
                //在android所有关于xml的解析都是pull解析
                XmlSerializer serializer = Xml.newSerializer();
                //把短信序列化到SD卡，然后设置编码格式
                serializer.setOutput(os, "utf-8");
                //standalone表示当前xml文件是否独立,true表示独立
                serializer.startDocument("utf-8",true);
                //第一个参数是命名空间，第二个参数是节点名称
                serializer.startTag(null, "smss");
                //设置smss的属性值，第二个参数是名字，第三个是值
                serializer.attribute(null,"size",String.valueOf(count));

                while(cursor.moveToNext()){
                    System.err.println("-----------------");
                    System.out.println(cursor.getString(0));
                    System.out.println(cursor.getString(1));
                    System.out.println(cursor.getString(2));
                    System.out.println(cursor.getString(3));
                    serializer.startTag(null, "sms");

                    serializer.startTag(null, "address");
                    //设置文本内容
                    serializer.text(cursor.getString(0));

                    serializer.endTag(null, "address");

                    serializer.startTag(null, "date");
                    //设置文本内容
                    serializer.text(cursor.getString(1));

                    serializer.endTag(null, "date");

                    serializer.startTag(null, "type");
                    //设置文本内容
                    serializer.text(cursor.getString(2));

                    serializer.endTag(null, "type");

                    serializer.startTag(null, "body");
                    //设置文本内容
                    //加密，第一个种子表示秘钥，第二个参数表示内容
                    serializer.text(Crypto.encrypt("1314",cursor.getString(3)));

                    serializer.endTag(null, "body");

                    serializer.endTag(null,"sms");
                    //序列化一条短信后就++
                    process++;
                    //progressDialog.setProgress(process);
                    callBack.onBackUpSms(process);

                    SystemClock.sleep(200);
                }

                //关闭游标，可关可不关
                cursor.close();
                serializer.endTag(null,"smss");

                serializer.endDocument();
                //刷新
                os.flush();
                //关流
                os.close();

                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        return false;
    }

}
