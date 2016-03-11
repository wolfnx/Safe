package cn.itcast.lost.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.EncodedKeySpec;

/**
 * MD5工具类
 * @author wolfnx
 *
 */
public class Md5Utils {

	//加密
	public static String encode(String password) {
		try {
			MessageDigest instance = MessageDigest.getInstance("MD5");// 获取MD5算法对象
			byte[] digest = instance.digest(password.getBytes());// 对字符串加密,返回字节数组

			StringBuffer sb = new StringBuffer();
			for (byte b : digest) {
				int i = b & 0xff;// 获取字节的低八位有效值
				String hexString = Integer.toHexString(i);// 将整数转为16进制

				if (hexString.length() < 2) {
					hexString = "0" + hexString;// 如果是1位的话,补0
				}

				sb.append(hexString);
			}

			return sb.toString();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			// 没有该算法时,抛出异常, 不会走到这里
		}

		return "";
	}

	/**
	 * 获取文件的Md5值(也叫病毒特征码)
	 * @return
	 */
	public static String getFileMd5(String sourceDir) {

		File file = new File(sourceDir);

		try {
			FileInputStream fileInputStream = new FileInputStream(file);

			byte[] buffer = new byte[1024];
			int len=-1;

			//获取数字摘要
			MessageDigest messageDigest = MessageDigest.getInstance("md5");

			while((len=fileInputStream.read(buffer))!=-1){
					messageDigest.update(buffer,0,len);
			}
			byte[] digest = messageDigest.digest();

			/**
			 * 下面这段与上面的方法里的代码相同
			 */
			StringBuffer sb = new StringBuffer();
			for (byte b : digest) {
				int i = b & 0xff;// 获取字节的低八位有效值
				String hexString = Integer.toHexString(i);// 将整数转为16进制

				if (hexString.length() < 2) {
					hexString = "0" + hexString;// 如果是1位的话,补0
				}

				sb.append(hexString);
			}

			return sb.toString();


		} catch (Exception e) {
			e.printStackTrace();
		}

		return "";
	}
}

