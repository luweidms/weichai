package com.youming.youche.util;

import cn.hutool.core.codec.Base64;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

/**
 * @ClassName: PassportUtil
 * @Description: 加密工具类，包含可逆加密和不可逆加密两种，其中可逆加密为AzDGCrypt算法，不可逆加密有md5、SHA、SHA256、SHA512等
 * @author: 温泉
 * @company: 上海航盛实业有限公司
 * @date 2012-03-22 09:21:45 +0800
 */
public class PassportUtil {

	private final static String key = "shanghai.soooqooo.shenyang";

	/**
	 * @Fields hexDigits : 加密所需字符串
	 */
	private final static String[] hexDigits = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d",
			"e", "f" };

	/**
	 * @param byteArray 字节数组
	 * @return String 16进制的字符串
	 * @throws
	 * @Title: byteArrayToHexString
	 * @Description: 将字节数组转换为16进制的字符串
	 * @author 温泉
	 * @date 2012-2-22
	 */
	private static String byteArrayToHexString(byte[] byteArray) {
		StringBuffer sb = new StringBuffer();
		for (byte byt : byteArray) {
			sb.append(byteToHexString(byt));
		}
		return sb.toString();
	}

	/**
	 * @param byt 字节
	 * @return String 16进制字符串
	 * @throws
	 * @Title: byteToHexString
	 * @Description: 将字节转换为16进制字符串
	 * @author A18ccms A18ccms_gmail_com
	 * @date 2012-2-22
	 */
	private static String byteToHexString(byte byt) {
		int n = byt;
		if (n < 0)
			n = 256 + n;
		return hexDigits[n / 16] + hexDigits[n % 16];
	}

	/**
	 * @param code 编码类型
	 * @param message 摘要信息
	 * @return String 相应的编码字符串
	 * @throws
	 * @Title: Encode
	 * @Description: 将摘要信息转换为相应的编码
	 * @author 温泉
	 * @date 2012-2-22
	 */
	private static String Encode(String code, String message) {
		MessageDigest md;
		String encode = null;
		try {
			md = MessageDigest.getInstance(code);
			encode = byteArrayToHexString(md.digest(message.getBytes()));
		}
		catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return encode;
	}

	/**
	 * @param message 摘要信息
	 * @return String MD5编码之后的字符串
	 * @throws
	 * @Title: md5Encode
	 * @Description: 将摘要信息转换成MD5编码
	 * @author 温泉
	 * @date 2012-2-22
	 */
	public static String md5Encode(String message) {
		return Encode("MD5", message);
	}

	/**
	 * @param message 摘要信息
	 * @return String SHA编码之后的字符串
	 * @throws
	 * @Title: shaEncode
	 * @Description: 将摘要信息转换成SHA编码
	 * @author 温泉
	 * @date 2012-2-22
	 */
	public static String shaEncode(String message) {
		return Encode("SHA", message);
	}

	/**
	 * @param message 摘要信息
	 * @return String SHA-256编码之后的字符串
	 * @throws
	 * @Title: sha256Encode
	 * @Description: 将摘要信息转换成SHA-256编码
	 * @author 温泉
	 * @date 2012-2-22
	 */
	public static String sha256Encode(String message) {
		return Encode("SHA-256", message);
	}

	/**
	 * @param message 摘要信息
	 * @return SHA-512编码之后的字符串
	 * @throws
	 * @Title: sha512Encode
	 * @Description: 将摘要信息转换成SHA-512编码
	 * @author 温泉
	 * @date 2012-2-22
	 */
	public static String sha512Encode(String message) {
		return Encode("SHA-512", message);
	}

	/**
	 * @param txt
	 * @return String 返回类型
	 * @throws
	 * @Title: azdgcryptEncode
	 * @Description: 可逆加密算法 使用默认的key
	 * @author: 温泉
	 * @date 2012-03-22 14:29:16 +0800
	 */
	public static String azdgcryptEncode(String txt) {
		return azdgcryptEncode(txt, key);
	}

	public static String azdgcryptEncode2(String txt) {
		return azdgcryptEncode2(txt, key);
	}

	public static String azdgcryptEncode2(String txt, String key) {
		Random random = new Random();
		String rad = String.valueOf(random.nextInt(32000));
		// 使用随机数发生器产生 0~32000 的值并 MD5()
		// srand((double)microtime() * 1000000);
		String encrypt_key = md5Encode(rad);

		// 变量初始化
		int ctr = 0;
		String tmp = "";

		// for 循环，$i 为从 0 开始，到小于 $txt 字串长度的整数
		char encrypt_key_char[] = encrypt_key.toCharArray();
		char txt_char[] = txt.toCharArray();

		for (int i = 0; i < txt.length(); i++) {
			// 如果 $ctr = $encrypt_key 的长度，则 $ctr 清零
			ctr = ctr == encrypt_key_char.length ? 0 : ctr;
			// $tmp 字串在末尾增加两位，其第一位内容为 $encrypt_key 的第 $ctr 位，
			// 第二位内容为 $txt 的第 $i 位与 $encrypt_key 的 $ctr 位取异或。然后 $ctr = $ctr + 1
			char tmp1 = txt_char[i];
			char tmp4 = encrypt_key_char[ctr];
			char tmp2 = encrypt_key_char[ctr++];
			char tmp3 = (char) (tmp1 ^ tmp2);
			tmp += tmp4 + "" + tmp3;
		}
		return md5Encode(passportKey(tmp, key)).toUpperCase();

		// 返回结果，结果为 passport_key() 函数返回值的 base65 编码结果

		// return base64_encode(passportKey(tmp, key));

	}

	/**
	 * @param txt 待加密明文
	 * @param key 密钥
	 * @param @return 设定文件
	 * @return String 返回类型
	 * @throws
	 * @Title: azdgcryptEncode
	 * @Description: 可逆加密算法
	 * @author: 温泉
	 * @date 2012-03-22 09:25:05 +0800
	 */
	public static String azdgcryptEncode(String txt, String key) {
		Random random = new Random();
		String rad = String.valueOf(random.nextInt(32000));
		// 使用随机数发生器产生 0~32000 的值并 MD5()
		// srand((double)microtime() * 1000000);
		String encrypt_key = md5Encode(rad);

		// 变量初始化
		int ctr = 0;
		String tmp = "";

		// for 循环，$i 为从 0 开始，到小于 $txt 字串长度的整数
		char encrypt_key_char[] = encrypt_key.toCharArray();
		char txt_char[] = txt.toCharArray();

		for (int i = 0; i < txt.length(); i++) {
			// 如果 $ctr = $encrypt_key 的长度，则 $ctr 清零
			ctr = ctr == encrypt_key_char.length ? 0 : ctr;
			// $tmp 字串在末尾增加两位，其第一位内容为 $encrypt_key 的第 $ctr 位，
			// 第二位内容为 $txt 的第 $i 位与 $encrypt_key 的 $ctr 位取异或。然后 $ctr = $ctr + 1
			char tmp1 = txt_char[i];
			char tmp4 = encrypt_key_char[ctr];
			char tmp2 = encrypt_key_char[ctr++];
			char tmp3 = (char) (tmp1 ^ tmp2);
			tmp += tmp4 + "" + tmp3;
		}
		// 返回结果，结果为 passport_key() 函数返回值的 base65 编码结果
		return base64_encode(passportKey(tmp, key));

	}

	/**
	 * @param txt
	 * @return String 返回类型
	 * @throws
	 * @Title: azdgcryptDecode
	 * @Description: 可逆解密方法，使用默认的key
	 * @author: 温泉
	 * @date 2012-03-22 14:29:42 +0800
	 */
	public static String azdgcryptDecode(String txt) {
		return azdgcryptDecode(txt, key);
	}

	/**
	 * @param txt
	 * @param key
	 * @return String 返回类型
	 * @throws
	 * @Title: azdgcryptDecode
	 * @Description: 可逆解密方法
	 * @author: 温泉
	 * @date 2012-03-22 09:24:01 +0800
	 */
	public static String azdgcryptDecode(String txt, String key) {

		// $txt 的结果为加密后的字串经过 base64 解码，然后与私有密匙一起，
		// 经过 passport_key() 函数处理后的返回值

		txt = passportKey(base64_decode(txt), key);

		// 变量初始化
		String tmp = "";

		// for 循环，$i 为从 0 开始，到小于 $txt 字串长度的整数
		char txt_char[] = txt.toCharArray();
		for (int i = 0; i < txt.length(); i++) {
			// $tmp 字串在末尾增加一位，其内容为 $txt 的第 $i 位，
			// 与 $txt 的第 $i + 1 位取异或。然后 $i = $i + 1
			tmp += (char) (txt_char[i] ^ txt_char[++i]);
		}

		// 返回 $tmp 的值作为结果
		return tmp;

	}

	/**
	 * Passport 密匙处理函数
	 * @param txt
	 * @param encrypt_key(用于解密和加密)
	 * @return string 处理后的密匙
	 */
	private static String passportKey(String txt, String encrypt_key) {

		// 将 $encrypt_key 赋为 $encrypt_key 经 md5() 后的值
		encrypt_key = md5Encode(encrypt_key);
		// 变量初始化
		int ctr = 0;
		String tmp = "";

		// for 循环，$i 为从 0 开始，到小于 $txt 字串长度的整数
		char encrypt_key_char[] = encrypt_key.toCharArray();
		char txt_char[] = txt.toCharArray();
		for (int i = 0; i < txt.length(); i++) {
			// 如果 $ctr = $encrypt_key 的长度，则 $ctr 清零
			ctr = ctr == encrypt_key.length() ? 0 : ctr;
			// $tmp 字串在末尾增加一位，其内容为 $txt 的第 $i 位，
			// 与 $encrypt_key 的第 $ctr + 1 位取异或。然后 $ctr = $ctr + 1
			char c = (char) (txt_char[i] ^ encrypt_key_char[ctr++]);
			tmp = tmp + c;
		}

		// 返回 $tmp 的值作为结果
		return tmp;
	}

	/**
	 * 本函数将字符串以 MIME BASE64 编码。此编码方式可以让中文字或者图片也能在网络上顺利传输。在 BASE64
	 * 编码后的字符串只包含英文字母大小写、阿拉伯数字、加号与反斜线，共 64 个基本字符，不包含其它特殊的字符， 因而才取名
	 * BASE64。编码后的字符串比原来的字符串长度再加 1/3 左右。更多的 BASE64 编码信息可以参考 RFC2045 文件之 6.8 节
	 * @param txt 等待编码的原字串
	 * @return
	 */
	private static String base64_decode(String txt) {
		byte[] encrypted1 = Base64.decode(txt);
		// BASE64Decoder base64_decode = new BASE64Decoder();
		String str = "";
		str = new String(encrypted1.toString());
		return str;
	}

	private static String base64_encode(String txt) {

		// BASE64Encoder base64_encode = new BASE64Encoder();
		// return base64_encode.encode(txt.getBytes());
		return Base64.decode(txt).toString();
	}

	// public static void main(String[] args) {
	// String txt = "asdsderwqewqdsadadfdasddas";
	// String key = "1a";
	// String jia_str = PassportUtil.azdgcryptEncode(txt);
	// String jie_str = PassportUtil.azdgcryptDecode(jia_str);
	// System.out.println("加密函数测试：" + jia_str);
	// System.out.println("解密函数测试：" + jie_str);
	//
	// }

}
