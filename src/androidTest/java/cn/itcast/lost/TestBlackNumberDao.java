package cn.itcast.lost;

import android.content.Context;
import android.test.AndroidTestCase;

import java.util.List;
import java.util.Random;

import cn.itcast.lost.bean.BlackNumberInfo;
import cn.itcast.lost.db.dao.BlackNumberDao;

/**
 * Created by wolfnx on 2016/1/24.
 */
public class TestBlackNumberDao extends AndroidTestCase {

    public Context mConetext;
    @Override
    protected void setUp() throws Exception {
        this.mConetext=getContext();
        super.setUp();
    }

    /**
     * 测试增加黑名单号码
     */
    public void testadd(){
       BlackNumberDao dao= new BlackNumberDao(mConetext);
        Random r=new Random();
        for(int i=0;i<200;i++){
            Long number=13612866035L+i;
            dao.add(number+"",String.valueOf(r.nextInt(3)+1));
        }
    }

    public void testdelete(){
        BlackNumberDao dao=new BlackNumberDao(mConetext);
        for(int i=0;i<100;i++){
            Long number=13612866035L+i;
            dao.delete(number + "");
        }
    }

    public void testfindNumber(){
        BlackNumberDao dao=new BlackNumberDao(mConetext);
        String mode=dao.findNumber("13612866137");
        System.out.print(mode);
    }

    public void testFindAll(){
        BlackNumberDao dao=new BlackNumberDao(mConetext);
        List<BlackNumberInfo> blackNumberInfos=dao.findAll();
        for(BlackNumberInfo t:blackNumberInfos){
            System.out.print(t.getNumber()+","+t.getMode());
        }
    }

    public void testChangeMode(){
        BlackNumberDao dao=new BlackNumberDao(mConetext);
        dao.changeNumberMode("13612866137","3");
    }

}
