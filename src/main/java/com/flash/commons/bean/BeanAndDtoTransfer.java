package com.flash.commons.bean;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

public class BeanAndDtoTransfer {
	private static final Logger LOGGER = LoggerFactory.getLogger(BeanAndDtoTransfer.class);

	/**
	 * 将dto转换为Bean
	 * 
	 * @author lonaking
	 * @param dto
	 * @param cla
	 * @deprecated 请使用新的方法@see transOneToAnoter
	 * @return
	 */
	public static <D, B> B PutDtoIntoBean(D dto, Class<B> cla) {
		B bean;
		try {
			bean = cla.newInstance();
			BeanUtils.copyProperties(dto, bean);
			return bean;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 将Bean转换为Dto
	 * 
	 * @author lonaking
	 * @param bean
	 * @param cla
	 * @deprecated 请使用新的方法@see transOneToAnoter
	 * @return
	 */
	public static <B, D> D putBeanIntoDto(B bean, Class<D> cla) {
		try {
			D dto = cla.newInstance();
			BeanUtils.copyProperties(bean, dto);
			return dto;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 将一个Bean(或者dto)的列表中的所有元素复制到另一哥Dto(或者bean)的列表中
	 * 
	 * @author lonaking
	 * @param one
	 *            一个已知列表
	 * @param cla
	 *            要复制到的列表的类型
	 * @return
	 */
	public static <B, D> List<D> transOneListToAnoterList(List<B> one, Class<D> cla) {
		List<D> listD = new ArrayList<D>();
		for (B b : one) {
			D d = BeanAndDtoTransfer.transOneToAnother(b, cla);
			listD.add(d);
		}
		return listD;
	}

	/**
	 * 快捷复制对象到领一个对象 只能复制字段名相同的字段 底层使用copyProperties()
	 * 
	 * @author lonaking
	 * @param bean
	 * @param cla
	 * @return
	 */
	public static <B, D> D transOneToAnother(B bean, Class<D> cla) {
		try {
			D dto = cla.newInstance();
			BeanUtils.copyProperties(bean, dto);
			return dto;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static <B, D> D transOneToAnother(B bean, Class<D> cla, boolean camelUpperTrans) {
		try {
			if (camelUpperTrans) {
				D dto = cla.newInstance();
				transUpperLowerToCamel(bean, dto);
				return dto;
			} else {
				return transOneToAnother(bean, cla);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static <B, D> List<D> transOneListToAnoterList(List<B> one, Class<D> cla, boolean camelUpperTrans) {
		try {
			if (camelUpperTrans) {
				List<D> listD = new ArrayList<D>();
				for (B b : one) {
					D d = BeanAndDtoTransfer.transOneToAnother(b, cla, true);
					listD.add(d);
				}
				return listD;
			} else {
				return transOneListToAnoterList(one, cla);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 转换下划线的model 到驼峰的model
	 * 
	 * @author lonaking
	 * @param source
	 * @param target
	 */
	private static void transUpperLowerToCamel(Object source, Object target) {
		Assert.notNull(source, "Source must not be null");
		Assert.notNull(target, "Target must not be null");

		Class sourceClass = source.getClass();
		Class targetClass = target.getClass();
		Field[] sourceField = sourceClass.getDeclaredFields();
		Field[] targetField = targetClass.getDeclaredFields();
		Map<String, String> sourceStandardMap = new HashMap<String, String>();
		Map<String, String> targetStandardMap = new HashMap<String, String>();

		for (Field f : sourceField) {
			f.setAccessible(true);
			String sourceFiledNameString = f.getName();
			// 除掉下划线
			String lowString = sourceFiledNameString.replace("_", "");
			// 转化成小写
			lowString = lowString.toLowerCase();
			// key:处理后的字段名 value:处理前的字段名
			sourceStandardMap.put(sourceFiledNameString, lowString);
		}

		for (Field f : targetField) {
			f.setAccessible(true);
			String targetFiledNameString = f.getName();
			// 除掉下划线
			String lowString = targetFiledNameString.replace("_", "");
			// 转化成小写
			lowString = lowString.toLowerCase();
			// key:处理后的字段名 value:处理前的字段名
			targetStandardMap.put(targetFiledNameString, lowString);
		}

		// 准备工作完成 开始重头戏
		for (Field field : sourceField) {
			try {
				String sourceName = field.getName(); // 属性名
				String sourceStandardName = sourceStandardMap.get(sourceName);
				Class tpye = field.getType(); // 属性类型

				String methodName = sourceName.substring(0, 1).toUpperCase() + sourceName.substring(1);
				Method getMethod = sourceClass.getMethod("get" + methodName); // 拿到set方法
																				// 如果get方法不是标准驼峰
																				// 自己玩去吧

				Object value = getMethod.invoke(source); // 执行source的get 拿到value

				for (Field f : targetField) {
					String targetName = f.getName(); // 目标的属性名
					String setTargetName = targetName.substring(0, 1).toUpperCase() + targetName.substring(1);
					String targetStandardName = targetStandardMap.get(targetName);
					// 如果有重名,直接set
					if (sourceStandardName.equals(targetStandardName)) {
						Method setMethod = targetClass.getMethod("set" + setTargetName, tpye); // 目标属性的set方法

						setMethod.invoke(target, value);// 写入
					}

				}
			} catch (Exception e) {
				LOGGER.debug(e.getMessage());
			}
		}
	}
}
