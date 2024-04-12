package com.javamall.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 字符串工具类
 */
public class StringUtil {

	/**
	 * 判断是否为空
	 */
	public static boolean isEmpty(String str){
        return str == null || str.trim().isEmpty();
	}
	
	/**
	 * 判断是否不是空
	 */
	public static boolean isNotEmpty(String str){
        return (str != null) && !str.trim().isEmpty();
	}


}
