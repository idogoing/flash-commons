package com.flash.commons.date;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DateUtils {
	public static final long FIVE_DAYS = 5L;

	private static final Logger LOGGER = LoggerFactory.getLogger(DateUtils.class);
	public static final long ONEDAYMILS = 1000*60*60*24L;//一天的毫秒数

	public static final long ONEWEEKMILS = 1000*60*60*24*7L;

	public static final long ONEMONTHMILS = 1000*60*60*24*30L;
	
	public static final long ONE_HOUR_MILS = 1000 * 60 * 60L;

	public static Timestamp getTodayStartTimestamp(){
		Timestamp timestamp = getStartTimestamp(new Timestamp(System.currentTimeMillis()));
		return timestamp;
	}
	/**
	 * 获取今天结束的时间戳
	 * @author lonaking
	 * @return
	 */
	public static Timestamp getTodayEndTimestamp() {
		long startMil = getTodayStartTimestamp().getTime();
		long endMil = startMil + ONEDAYMILS - 1;
		Timestamp endToday = new Timestamp(endMil);
		return endToday;
	}

	/**
	 * 获取自今天起 一个月内的开始日期
	 * @return
	 */
	public static Timestamp getOneMonthAgo(){
		Calendar c = Calendar.getInstance();
		c.add(Calendar.MONTH, -1);
		long startMils = c.getTimeInMillis();
		Timestamp start = new Timestamp(startMils);
		return start;
	}
	/**
	 * 获取当月第一天的毫秒值
	 * @author lonaking
	 * @return
	 */
	public static Timestamp getCurrentMonthStartTime() {
		Calendar ca = Calendar.getInstance();// 获取当前日期
		ca.add(Calendar.MONTH, 0);
		ca.set(Calendar.DAY_OF_MONTH, 1);// 设置为1号,当前日期既为本月第一天
		long startMils = ca.getTimeInMillis();
		Timestamp start = new Timestamp(startMils);
		Timestamp result = getStartTimestamp(start);
		return result;
	}
	/**
	 * 获取当月最后一天的最后一个毫秒值
	 * @author lonaking
	 * @return
	 */
	public static Timestamp getCurrentMonthEndTime(){
		Timestamp startTime = getCurrentMonthStartTime();
		int days = getCurrentMonthDays();
		long time = startTime.getTime() + (ONEDAYMILS * days -1 );
		Timestamp endTimestamp = getDayAfterDays(startTime, days);
		Timestamp result = new Timestamp(time);
		return result;
	}
	/**
	 * 获取当月有多少天
	 * @author lonaking
	 * @return
	 */
	public static int getCurrentMonthDays() {
		Calendar a = Calendar.getInstance();
		a.set(Calendar.DATE, 1);
		a.roll(Calendar.DATE, -1);
		int maxDate = a.get(Calendar.DATE);
		return maxDate;
	}
	/**
	 * 获取1970年第一天
	 * @return
	 */
	public static Timestamp getGreenwichDay() {
		return new Timestamp(0L);
	}
	/**
	 * 将日期format为yyyy-MM-dd 这样的格式
	 * @author lonaking
	 * @param date
	 * @return
	 */
	public static String formatDate(Date date){
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		String stringDate = format.format(date);
		return stringDate;
	}
	
	public static String formatDateSmart(Date date, String formatString){
		if(null == formatString || "".equals(formatString))
			formatString = "yyyy-MM-dd hh:mm:ss";
		DateFormat format = new SimpleDateFormat(formatString);
		String stringDate = format.format(date);
		return stringDate;
	}
	
	public static Date formatStrToDate(String str, String formatString) {
		SimpleDateFormat format = new SimpleDateFormat(formatString);
		Date date = null;
		try {
			date = format.parse(str);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}
	
	/**
	 * 获取一个Tiemstamp的开始日期
	 * @author lonaking
	 * @param time
	 * @return
	 */
	public static Long getStart(Long time){
		Timestamp timestamp = new Timestamp(time);
		return getStartTimestamp(timestamp).getTime();
	}
	/**
	 * 获取一天的最后一个时间点
	 * @author lonaking
	 * @param time
	 * @param dateBlock
	 * @return
	 */
	public static Long getEnd(Long time){
		Timestamp timestamp = new Timestamp(time);
		return getEndTimestamp(timestamp).getTime();
	}
	
	/**
	 * 获取某一天的第一毫秒的时间戳
	 * @author lonaking
	 * @param date
	 * @return
	 */
	public static Timestamp getStartTimestamp(Timestamp date){
		Timestamp start = null;
		try {
			DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			String startStr = format.format(date);
			Date startDate = format.parse(startStr);
			long startMil = startDate.getTime();
			start = new Timestamp(startMil);
		} catch (ParseException e) {
			LOGGER.error("日期时间不正确");
		}
		return start;
	}
	/**
	 * 获取某一天最后一毫秒的时间戳
	 * @author lonaking
	 * @param date
	 * @return
	 */
	public static Timestamp getEndTimestamp(Timestamp date) {
		long startMil = date.getTime();
		long endMil = startMil + ONEDAYMILS - 1;
		Timestamp end = new Timestamp(endMil);
		return end;
	}
	/**
	 * 返回几天后的最后时间临界值
	 * @author lonaking
	 * @param startDay 开始时间
	 * @param block 间隔时间（单位天）
	 * @return
	 */
	public static Timestamp getDayAfterDays(Timestamp startDay,Integer block){
		long startMil = getStartTimestamp(startDay).getTime();
		long endMil = startMil + ONEDAYMILS * block - 1;
		Timestamp end  = new Timestamp(endMil);
		return end;
	}
	
	/**
	 * 获取从今天开始计算第几天后的最后时间临界值
	 * @author lonaking
	 * @param block 间隔时间(单位天)
	 * @return
	 */
	public static Timestamp getTodayAfterDays(Integer block){
		Timestamp end = getDayAfterDays(new Timestamp(System.currentTimeMillis()), block);
		return end;
	}


	/**
	 * 一天的开始
	 * @param date
	 * @return
	 */
	public static Date getStartForDay(Date date){
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		return calendar.getTime();
	}
	/**
	 * 一天的结束
	 * @param date
	 * @return
	 */
	public static Date getEndForDay(Date date){
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.HOUR_OF_DAY, 23);
		calendar.set(Calendar.MINUTE, 59);
		calendar.set(Calendar.SECOND, 59);
		//calendar.set(Calendar.MILLISECOND, 999);
		return calendar.getTime();
	}
}
