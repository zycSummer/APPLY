/**
 * JET-APP
 * com.jet.common.utils
 * MD5.java
 * 
 * 2016年6月8日-下午3:53:44
 * 2016济中节能-版权所有
 */
package com.jet.cloud.jetsndcxqyapply.common;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @ClassName com.jet.common.utils.MD5
 */
public class MD5 {

	// 加密成字节数组
	public static byte[] computeToBytes(String srcStr) {
		byte[] md5Bytes = null;
		try {
			byte[] byteArray = srcStr.getBytes("UTF8");
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			md5Bytes = md5.digest(byteArray);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return md5Bytes;
	}

	// 加密成十六进制字符串
	public static String computeToHexStr(String srcStr) {
		String md5Str = byte2hex(computeToBytes(srcStr));
		return md5Str;
	}

	// 把字节数组转换成十六进制字符串
	public static String byte2hex(byte[] b) {
		String hs = "";
		String stmp = "";
		for (int n = 0; n < b.length; n++) {
			stmp = (Integer.toHexString(b[n] & 0XFF));
			if (stmp.length() == 1) {
				hs = hs + "0" + stmp;
			}
			else {
				hs = hs + stmp;
			}
		}
		return hs.toLowerCase();
	}

	// 输出字节数组的所有值
	public static String outputBytesToString(byte[] b) {
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		for (int i = 0; i < b.length; i++) {
			sb.append(b[i]);
			sb.append(",");
		}
		if (sb.length() > 1) {
			sb = sb.deleteCharAt(sb.length() - 1);
		}
		sb.append("]");
		return sb.toString();
	}

	public static void main(String[] args) {
		byte[] md5Bytes = MD5.computeToBytes("123456");
		System.out.println("MD5加密后的byte密文为：" + outputBytesToString(md5Bytes));
		String md5HexStr = MD5.computeToHexStr("csats");
		System.out.println("MD5加密后的十六进制密文为：" + md5HexStr);

		byte[] md5Bytes2 = MD5.computeToBytes("123456");
		System.out.println("MD5加密后的byte密文为：" + outputBytesToString(md5Bytes2));
		String md5HexStr2 = MD5.computeToHexStr("123456");
		System.out.println("MD5加密后的十六进制密文为：" + md5HexStr2);
	}

}
