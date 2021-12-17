package com.mycoder.community.util;

import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * @author cj
 * @create 2021-12-17 15:13
 */
@Component
public class SensitiveFilter {

    public static final Logger log = LoggerFactory.getLogger(SensitiveFilter.class);

    //要替换的符号
    public static final String REPLACEMENT = "***";

    //前缀树的根节点
    private TrieNode rootNode = new TrieNode();

    /**在过滤器构造函数结束后，调用此方法，读取敏感词库文件，构造前缀树，只构造一次*/
    @PostConstruct
    public void init(){

        try(
                //获取敏感词库文件的字节流
                InputStream is = this.getClass().getClassLoader().getResourceAsStream("sensitive-words.txt");
                //字节流转换为缓冲流
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        ){
            String word;
            while((word = reader.readLine()) != null){
                this.addKeyword(word);
            }

        }catch (IOException e){
            log.error("读取敏感词库文件失败" + e.getMessage());
        }
    }

    //向前缀树中插入字符串
    private void addKeyword(String word) {
        TrieNode cur = rootNode;
        for(int i = 0; i < word.length(); i++){
            char c = word.charAt(i);

            if(!cur.child.containsKey(c)){
                cur.child.put(c, new TrieNode());
            }

            //if(i == word.length() - 1) cur.isEnd = true;
            cur = cur.child.get(c);
        }
            //注意，前缀树的构造
            cur.isEnd = true;
    }

    /**
     * 将一段文本中的敏感词过滤掉
     * @param text 要判断的文本
     * @return 过滤过的文本
     * */
    public String filter(String text){
        if(StringUtils.isBlank(text)) return null;

        //临时结果
        StringBuilder res = new StringBuilder();

        //三个指针
        TrieNode temp = rootNode;
        int start = 0;
        int end = 0;

        while(start < text.length()){
            char c = text.charAt(end);
            //若c是字符串
            if(isSymbol(c)){
                if(temp == rootNode){
                    start++;
                    res.append(c);
                }
                end++;
                continue;
            }

            if(!temp.child.containsKey(c)){
                res.append(c);
                end = ++start;
                temp = rootNode;
                //注意
            }else if(temp.child.get(c).isEnd){
                res.append(REPLACEMENT);
                start = ++end;
                temp = rootNode;
                //注意
            }else if(!temp.child.get(c).isEnd){
                end++;
                temp = temp.child.get(c);
            }
        }

        return res.toString();
    }

    /**若是特殊符号，返回true*/
    private boolean isSymbol(Character c){
        //既不是常规字符，也不是东亚字符，则说明是特殊符号
        return !CharUtils.isAsciiAlphanumeric(c) && (c < 0x2e80 || c > 0x9fff);
    }

    /**定义前缀树的结构*/
    private class TrieNode{
        private boolean isEnd = false;
        private Map<Character, TrieNode> child = new HashMap<>();

        public void addChildNode(Character c, TrieNode childNode){
            child.put(c, childNode);
        }

        public TrieNode getChildNode( Character c){
            return child.get(c);
        }

        public boolean isEnd() {
            return isEnd;
        }

        public void setEnd(boolean end) {
            isEnd = end;
        }

    }

}
