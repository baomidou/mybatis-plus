package com.baomidou.mybatisplus.test;

public class SnowFlake {

    // 向左偏移10位
    public static final long NODE_SHIFT = 10L;
    // 向左偏移12位
    public static final long SEQUENCE_SHIFT = 12l;
    // 最大机器值
    public static final long MAX_NODE = -1L ^ (-1L << NODE_SHIFT);
    // 最大序列值
    public static final long MAX_SEQUENCE = -1L ^ (-1L << SEQUENCE_SHIFT);
    // 机器id
    private long node;
    // 序列
    private long sequence;
    // 最后一次取id的时间戳
    private long referenceTime;

    // 初始化如果没有指定node随机生成一个1-最大机器值之间的值
    public SnowFlake() {
        this.node = 1l + (long) (Math.random() * MAX_NODE);
    }

    // 初始化指定每台编号避免不同的机器在同一时间生成一样的id
    public SnowFlake(long node) {
        if (node >= MAX_NODE || node < 0) throw new IllegalArgumentException(
                String.format("node can't be greater than %d or less than 0", MAX_NODE));
        this.node = node;
    }

    // 生成下一个id(线程安全)
    public synchronized long next() {
        // 获取1970年1月1日0时起的毫秒差
        long currentTime = System.currentTimeMillis();
        // 序列当前值
        long counter;
        // 当前时间不能小于最后取id时间(毫秒差会随时间增长,说明系统时钟回退过)
        if (currentTime < referenceTime) {
            throw new RuntimeException(String.format("Last referenceTime %s is after reference time %s",
                    referenceTime, currentTime));
        }
        // 大与最后时间戳序列归零
        else if (currentTime > referenceTime) {
            this.sequence = 0l;
        } else {
            // 序列小于最大值自增
            if (this.sequence <= SnowFlake.MAX_SEQUENCE) {
                this.sequence++;
            }
            // 序列溢出
            else {
                // 阻塞到下一个毫秒,获得新时间戳
                while (currentTime <= referenceTime) {
                    currentTime = System.currentTimeMillis();
                }
                /**
                 * 在新的时间戳获取下一个id 关于重复阻塞:缩小机器标识值，增大序列最大值
                 */
                next();
            }
        }
        // 记录当前值
        counter = this.sequence;
        // 记录最后生成id时间戳
        referenceTime = currentTime;
        // 移位并通过或运算拼到一起组成64位的ID
        return currentTime << NODE_SHIFT << SEQUENCE_SHIFT | node << SEQUENCE_SHIFT | counter;
    }

}
