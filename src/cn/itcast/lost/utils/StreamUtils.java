package cn.itcast.lost.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * 文件读取工具类
 * @author wolfnx
 *
 */
public class StreamUtils {
	/**
	 * 
	 * @param in
	 * @return
	 * @throws IOException 
	 */
	public static String readFromStream(InputStream in) throws IOException{
		ByteArrayOutputStream out=new ByteArrayOutputStream();
		
		int len=0;
		byte[] buffer=new byte[1024];
		
		while((len=in.read(buffer))!=-1){
			out.write(buffer, 0, len);
		}
		String result=out.toString();
		in.close();
		out.close();
		return result;
	}
}
