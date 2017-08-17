package com.wzc.mobileplayer.Utils;

import com.wzc.mobileplayer.Domain.LyricBean;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by admin on 2017/8/16.
 */

public class LyricParaser {
    private ArrayList<LyricBean> lyricBeans;
    public void readFile(File file) throws IOException {
        if (file == null || !file.exists()){
            // 歌词文件不存在
            lyricBeans = null;
            isExistsLyric = false;
        } else {
            lyricBeans = new ArrayList<>();
            isExistsLyric = true;
            // 歌词文件存在
            // 解析歌词--一句一句
            BufferedReader reader = null;
            try {
//                reader = new BufferedReader(new InputStreamReader(new FileInputStream(file),"GBK"));
                reader = new BufferedReader(new InputStreamReader(new FileInputStream(file),getCharset(file)));
                String line;
                while ((line = reader.readLine())!=null){
                    line = analyzeLyric(line);
                }
            } catch (Exception e){
                e.printStackTrace();
            }

            // 排序
            Collections.sort(lyricBeans, new MyComparator());

            // 计算每句的高亮时间
            for (int i = 0; i<lyricBeans.size(); i++){
                LyricBean one = lyricBeans.get(i);
                if (i+1 <lyricBeans.size()){
                    LyricBean two = lyricBeans.get(i+1);
                    one.setSleeptime(two.getTimePoint()-one.getTimePoint());
                }
            }
        }
    }

    /**
     * 判断文件编码
     * @param file
     * @return
     */
    private String getCharset(File file) {
        String charset = "GBK";
        byte[] first3Bytes = new byte[3];
        try {
            boolean checked = false;
            BufferedInputStream bis = new BufferedInputStream(
                    new FileInputStream(file));
            bis.mark(0);
            int read = bis.read(first3Bytes, 0, 3);
            if (read == -1)
                return charset;
            if (first3Bytes[0] == (byte) 0xFF
                    && first3Bytes[1] == (byte) 0xFE) {
                charset = "UTF-16LE";
                checked = true;
            } else if (first3Bytes[0] == (byte) 0xFE
                    && first3Bytes[1] == (byte) 0xFF) {
                charset = "UTF-16BE";
                checked = true;
            } else if (first3Bytes[0] == (byte) 0xEF
                    && first3Bytes[1] == (byte) 0xBB
                    && first3Bytes[2] == (byte) 0xBF) {
                charset = "UTF-8";
                checked = true;
            }
            bis.reset();
            if (!checked) {
                int loc = 0;
                while ((read = bis.read()) != -1) {
                    loc++;
                    if (read >= 0xF0)
                        break;
                    if (0x80 <= read && read <= 0xBF)
                        break;
                    if (0xC0 <= read && read <= 0xDF) {
                        read = bis.read();
                        if (0x80 <= read && read <= 0xBF)

                            continue;
                        else
                            break;
                    } else if (0xE0 <= read && read <= 0xEF) {
                        read = bis.read();
                        if (0x80 <= read && read <= 0xBF) {
                            read = bis.read();
                            if (0x80 <= read && read <= 0xBF) {
                                charset = "UTF-8";
                                break;
                            } else
                                break;
                        } else
                            break;
                    }
                }
            }bis.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        return charset;
    }

    /**
     * 解析每一句
     * @param line [02:04.12][03:37.32][00:59.73]我在这里欢笑
     * @return
     */
    private String analyzeLyric(String line) {
        int pos1 = line.indexOf("[");// 0
        int pos2 = line.indexOf("]"); // 9 如果没有就返回-1
        if (pos1 == 0 && pos2 != -1){
            // 解析歌词
            // 装时间的集合
            long[] timeLongs = new long[getCountTag(line)];
            String timeStr = line.substring(pos1 +1,pos2); // 02:04.12
            timeLongs[0] = stTimeToLong(timeStr); // 02:04.12 -->毫秒

            if (timeLongs[0] == -1){
                return "";
            }

            int i = 1;
            String content = line; // line [02:04.12][03:37.32][00:59.73]我在这里欢笑
            while(pos1 ==0 && pos2 != -1){
                content = content.substring(pos2 + 1); // [03:37.32][00:59.73]我在这里欢笑
                pos1 = content.indexOf("["); // 0-->-1
                pos2 = content.indexOf("]"); // 9 如果没有就返回-1

                if (pos2 != -1){
                    // 还有下一句
                    timeStr = content.substring(pos1 + 1, pos2); // 03:37.21-->00:59.73
                    timeLongs[i] = stTimeToLong(timeStr); // 03:37.21-->毫秒

                    if (timeLongs[i]==-1){
                        return "";
                    }
                    i++;
                }
                // 把解析好的时间和歌词对应起来
                LyricBean lyricBean1 = new LyricBean();
                for (int j=0; j<timeLongs.length;j++){
                    // 设置歌词内容
                    lyricBean1.setContent(content);
                    lyricBean1.setTimePoint(timeLongs[j]);

                    lyricBeans.add(lyricBean1);
                    lyricBean1 = new LyricBean();
                }
            }
            return content;
        }
        return null;
    }

    /**
     * 返回重复了几回
     * @param line [02:04.12][03:37.32][00:59.73]我在这里欢笑
     * @return
     */
    private int getCountTag(String line) {
        int count = 1;
        String[] left = line.split("\\[");
        String[] right = line.split("\\]");

        if (left.length == 0 && right.length ==0){
            count = 1;
        } else if (left.length>right.length){
            count = left.length;
        } else {
            count = right.length;
        }
        return count;
    }



    /**
     * 02:04.12-->毫秒
     * @param timeStr
     * @return
     */
    private long stTimeToLong(String timeStr) {
        long time = -1;
        try {
            // 1.根据":"切成02和04.12
            String[] s1 = timeStr.split(":");
            // 2.根据"."把04.12切成04和12
            String[] s2 = s1[1].split("\\.");
            // 3.转换成long类型的毫秒时间
            // 分
            long min = Long.valueOf(s1[0]); // 02
            // 秒
            long second = Long.valueOf(s2[0]); // 04
            // 毫秒
            long mil = Long.valueOf(s2[1]); // 12
            time = min * 60 * 1000 + second * 1000 + mil * 10;
        } catch (Exception e){
            e.printStackTrace();
        }
         return  time;
    }

    /**
     * 得到歌词列表
     */
    public ArrayList<LyricBean> getLyricBeans() {
        return lyricBeans;
    }

    /**
     * 歌词是否存在
     * @return
     */
    public boolean isExistsLyric() {
        return isExistsLyric();
    }

    private boolean isExistsLyric = false;


    private class MyComparator implements java.util.Comparator<LyricBean> {
        @Override
        public int compare(LyricBean o1, LyricBean o2) {
            if (o1.getTimePoint()<o2.getTimePoint()){
                return -1;
            } else if (o1.getTimePoint() >o2.getTimePoint()) {
                return 1;
            } else {
                return 0;
            }
        }
    }
}
