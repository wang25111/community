package com.mycoder.community.entity;

/**
 * @author cj
 * @create 2021-12-09 20:07
 * 封装分页的相关信息
 */
public class Page {
    //当期页码
    private int current = 1;
    //每页数量
    private int limit = 10;
    //数据总行数
    private int rows;
    //查询路径，复用分页链接
    private String path;

    //获取当前页的起始行号:（页码号 - 1） * 每页显示的数量
    public int getOffset(){
        return (current - 1) * limit;
    }

    //获取总页数
    public int getTotal(){
        if (rows % limit == 0){
            return rows / limit;
        }else{
            return rows / limit + 1;
        }
    }

    //获取起始页号码，显示前两页
    public int getFrom(){
        int from = current - 2;
        return from < 1 ? 1 : from;
    }

    //获取终止页码，显示后两页
    public int getTo(){
        int to = current + 2;
        int totalPage = getTotal();
        return to > totalPage ? totalPage : to;
    }


    //属性的get、set方法
    public int getCurrent() {
        return current;
    }
    //需要判断
    public void setCurrent(int current) {
        if(current >= 1){
            this.current = current;
        }
    }

    public int getLimit() {
        return limit;
    }
    //需要判断
    public void setLimit(int limit) {
        if(limit >= 1 && limit <= 100){
            this.limit = limit;
        }
    }

    public int getRows() {
        return rows;
    }
    //需要判断
    public void setRows(int rows) {
        if(rows >= 0){
            this.rows = rows;
        }
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }


}
