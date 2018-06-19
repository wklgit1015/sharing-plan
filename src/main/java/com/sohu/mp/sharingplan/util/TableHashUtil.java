package com.sohu.mp.sharingplan.util;

public class TableHashUtil {

    private static final long NEWS_TABLE_STEP = 1000 * 10000;

    private static final int USER_NEWS_TABLE_SIZE = 64;

    private static final int PROFIT_TABLE_SIZE = 10;

    /**
     * 根据 userId 获取用户文章表index
     *
     * @param userId
     * @return
     */
    public static int getUserNewsTableIndex(int userId) {
        return userId % USER_NEWS_TABLE_SIZE;
    }

    /**
     * 根据文章 id 获取文章index
     *
     * @param newsId
     * @return
     */
    public static int getNewsTableIndex(long newsId) {
        return (int) (newsId / NEWS_TABLE_STEP);
    }

    /**
     * 根据 userId 获取收益表index
     *
     * @param userId
     * @return
     */
    public static int getProfitTableIndex(long userId) {
        return (int) (userId % PROFIT_TABLE_SIZE);
    }

}
