package com.askerlve.grace.shutdown.service;

/**
 * @author Askerlve
 * @Description: shutdown api
 * @date 2018/5/31下午4:27
 */
public interface Shutdown {

    /**
     * 拒绝新连接接入
     * @throws InterruptedException
     */
    void pause() throws InterruptedException;

    /**
     * 停止容器
     * @param delay 多少秒以后强制关闭
     * @throws InterruptedException
     */
    void shutdown(Integer delay) throws InterruptedException;

}
