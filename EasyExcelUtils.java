package com.ray.qjy.util;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.excel.write.metadata.style.WriteCellStyle;
import com.alibaba.excel.write.metadata.style.WriteFont;
import com.alibaba.excel.write.style.HorizontalCellStyleStrategy;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ray.qjy.exception.OrderException;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.springframework.stereotype.Component;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * ClassName: EasyExcelUtils
 * Description: 阿里excel操作工具
 * date: 2022/6/30 15:50
 *
 * @author luorenjie
 * @version 1.0
 * @since JDK 1.8
 */
@Slf4j
@Component
public class EasyExcelUtils<T> {

    private BaseMapper<T> baseMapper;

    public EasyExcelUtils() {
    }

    public EasyExcelUtils(BaseMapper<T> baseMapper) {
        this.baseMapper = baseMapper;
    }

    public BaseMapper<T> getBaseMapper() {
        return baseMapper;
    }

    public void setBaseMapper(BaseMapper<T> baseMapper) {
        this.baseMapper = baseMapper;
    }

    /**
     * 一次写导出
     *
     * @param fileName
     * @param sheetName
     * @param dataList
     * @param response
     * @throws IOException
     */
    public void exportForOnce(String fileName, String sheetName, List<Object> dataList, Class<T> itemClass, HttpServletResponse response) throws IOException {
        ServletOutputStream outputStream = getOutputStream(fileName, response);
        HorizontalCellStyleStrategy horizontalCellStyleStrategy = getStandardExcelStyle();
        EasyExcel.write(outputStream, itemClass)
                .excelType(ExcelTypeEnum.XLSX)
                .registerWriteHandler(horizontalCellStyleStrategy)
                .sheet(sheetName)
                .doWrite(dataList);
        outputStream.flush();
        outputStream.close();
    }

    /**
     * 重复写导出
     * @param sheetName
     * @param dataList
     * @param itemClass
     * @param excelWriter
     * @param response
     * @param i
     * @throws IOException
     */
    public void exportForMore(String sheetName, List<Object> dataList, Class<T> itemClass, ExcelWriter excelWriter, HttpServletResponse response, int i) throws IOException {
        HorizontalCellStyleStrategy horizontalCellStyleStrategy = getStandardExcelStyle();
        WriteSheet writeSheet = EasyExcel.writerSheet(0, sheetName).automaticMergeHead(true).head(itemClass)
                .registerWriteHandler(horizontalCellStyleStrategy).build();
        excelWriter.write(dataList, writeSheet);
        log.info("导出数据：dataList: 第{}批次，总数:{}", i, dataList.size());
    }

    /**
     * 获取ExcelWriter
     * @param fileName
     * @param response
     * @return
     */
    public ExcelWriter getExcelWriter(String fileName, HttpServletResponse response){
        ServletOutputStream outputStream = getOutputStream(fileName, response);
        ExcelWriter excelWriter = EasyExcel.write(outputStream).build();
        return excelWriter;
    }

    /**
     * 刷新ExcelWriter流
     * @param excelWriter
     */
    public void finishExcelWriter(ExcelWriter excelWriter){
        excelWriter.finish();
    }

    /**
     * EasyExcel标准样式
     *
     * @return
     */
    private HorizontalCellStyleStrategy getStandardExcelStyle() {
        /*
         * 表头的策略
         */
        WriteCellStyle headWriteCellStyle = new WriteCellStyle();
        //设置表头居中对齐
        headWriteCellStyle.setHorizontalAlignment(HorizontalAlignment.CENTER);
        headWriteCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        //设置表头颜色
        headWriteCellStyle.setFillForegroundColor(IndexedColors.WHITE.index);
        WriteFont headWriteFont = new WriteFont();
        headWriteFont.setFontHeightInPoints((short) 10);
        headWriteFont.setFontName("宋体");
        // 字体
        headWriteCellStyle.setWriteFont(headWriteFont);
        headWriteCellStyle.setWrapped(true);
        headWriteCellStyle.setLocked(true);

        /*
         * 内容的策略
         */
        WriteCellStyle contentWriteCellStyle = new WriteCellStyle();
        //设置 自动换行
        contentWriteCellStyle.setWrapped(false);
        //设置内容水平居中
        contentWriteCellStyle.setHorizontalAlignment(HorizontalAlignment.CENTER);
        //垂直居中
        contentWriteCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        HorizontalCellStyleStrategy horizontalCellStyleStrategy = new HorizontalCellStyleStrategy(headWriteCellStyle, contentWriteCellStyle);
        return horizontalCellStyleStrategy;
    }

    /**
     * 导出文件时为Writer生成OutputStream.
     *
     * @param fileName 文件名
     * @param response response
     * @return ""
     */
    private ServletOutputStream getOutputStream(String fileName, HttpServletResponse response) {
        try {
            fileName = URLEncoder.encode(fileName, "UTF-8");
            response.setContentType("application/vnd.ms-excel");
            response.setCharacterEncoding("utf-8");
            response.setHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode(fileName, "UTF-8") + ".xlsx");
//            response.setHeader("Pragma", "public");
//            response.setHeader("Cache-Control", "no-store");
//            response.addHeader("Cache-Control", "max-age=0");
            return response.getOutputStream();
        } catch (IOException e) {
            throw new OrderException("导出excel表格失败");
        }
    }


    /**
     * 设置动态表头，通过反射的方式
     * @param titleName
     * @param clazzPath
     * @throws ClassNotFoundException
     * @throws NoSuchFieldException
     * @throws IllegalAccessException
     * @return
     */
    public Class<?> setTitleForAnnotation(String titleName, String clazzPath) throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException {
        // 得到Class类对象
        Class<?> clazz = Class.forName(clazzPath);
        // 获取类的所有属性
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            ExcelProperty excelProperty = field.getAnnotation(ExcelProperty.class);
            String[] values = excelProperty.value();
            String[] tmp = new String[2];
            tmp[0] = titleName;
            tmp[1] = values[values.length -1];
            values = tmp;
            //获取 excelProperty 这个代理实例所持有的 InvocationHandler
            InvocationHandler h = Proxy.getInvocationHandler(excelProperty);
            // 获取 AnnotationInvocationHandler 的 memberValues 字段
            Field hField = h.getClass().getDeclaredField("memberValues");
            // 因为这个字段事 private final 修饰，所以要打开权限
            hField.setAccessible(true);
            // 获取 memberValues
            Map memberValues = (Map) hField.get(h);
            // 修改 value 属性值
            memberValues.put("value", values);
        }
        return clazz;
    }

    public static void main(String[] args) throws NoSuchMethodException, ClassNotFoundException, NoSuchFieldException, IllegalAccessException {
        // 得到Class类对象
        Class<?> clazz = Class.forName("com.ray.qjy.vo.easyExcelItem.RechargeDetailsForBuyItem");

        // 获取类的所有属性
        Field[] fields = clazz.getDeclaredFields();

        // 获取属性上的所有注解
        int i = 1;
        for (Field field : fields) {
            System.out.println("第"+i+++"个属性的注解有：");
            Annotation[] annotations = field.getAnnotations();
            for (Annotation annotation : annotations) {
                System.out.println(annotation.annotationType());
            }
        }

        // 获取属性上指定MyField2类型的注解
        System.out.println();
        System.out.println("获取属性上指定MyField2类型的注解:");
        for (Field field : fields) {
            ExcelProperty excelProperty = field.getAnnotation(ExcelProperty.class);
            String[] values = excelProperty.value();
            String[] tmp = new String[values.length + 1];
            tmp[0] = "titleName";
            tmp[tmp.length - 1] = values[values.length - 1];
            values = tmp;
            //获取 foo 这个代理实例所持有的 InvocationHandler
            InvocationHandler h = Proxy.getInvocationHandler(excelProperty);
            // 获取 AnnotationInvocationHandler 的 memberValues 字段
            Field hField = h.getClass().getDeclaredField("memberValues");
            // 因为这个字段事 private final 修饰，所以要打开权限
            hField.setAccessible(true);
            // 获取 memberValues
            Map memberValues = (Map) hField.get(h);
            // 修改 value 属性值
            memberValues.put("value", values);

            System.out.println(excelProperty);
            System.out.println(Arrays.toString(values));
        }

        // 获取属性上指定MyField2类型的注解
        System.out.println();
        System.out.println("获取属性上指定MyField2类型的注解:");
        for (Field field : fields) {
            ExcelProperty[] myField2s = field.getAnnotationsByType(ExcelProperty.class);
            for (ExcelProperty myField2 : myField2s) {
                System.out.println(myField2);
                System.out.println(myField2.value()+ "123");
            }
        }
    }


}
