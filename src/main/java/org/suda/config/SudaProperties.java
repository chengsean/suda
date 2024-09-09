package org.suda.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import java.util.ArrayList;
import java.util.List;

/**
 * 安全配置
 * @author shaozhuang.cheng
 * @dateTime 2024-09-02 13:18
 */
@ConfigurationProperties(prefix = "suda")
public class SudaProperties {

    private final XSSAttack xssAttack = new XSSAttack();
    private final SQLInject sqlInject = new SQLInject();
    private final Chars chars = new Chars();

    public XSSAttack getXssAttack() {
        return xssAttack;
    }

    public SQLInject getSqlInject() {
        return sqlInject;
    }

    public Chars getChars() {
        return chars;
    }

    public static class Chars {
        // 是否对字符串去空格
        private boolean trimEnabled = true;

        public boolean isTrimEnabled() {
            return trimEnabled;
        }

        public void setTrimEnabled(boolean trimEnabled) {
            this.trimEnabled = trimEnabled;
        }
    }

    public static class XSSAttack {
        // 是否启用XSS攻击检查，默认不启用
        private boolean checkEnabled = false;
        // 配置不需要XSS攻击检查的接口（白名单）
        private List<String> servletPathWhitelist = new ArrayList<>();
        // 检查XSS攻击的正则表达式
        private String[] xssRegexList = new String[] {"[\\S\\s\\t\\r\\n]*<[\\S\\s\\t\\r\\n]+(/)?>[\\S\\s\\t\\r\\n]*",
                "[\\S\\s\\t\\r\\n]*<[\\S\\s\\t\\r\\n]+>[\\S\\s\\t\\r\\n]+</[\\S\\s\\t\\r\\n]+>[\\S\\s\\t\\r\\n]*"};

        public boolean isCheckEnabled() {
            return checkEnabled;
        }

        public void setCheckEnabled(boolean checkEnabled) {
            this.checkEnabled = checkEnabled;
        }

        public List<String> getServletPathWhitelist() {
            return servletPathWhitelist;
        }

        public void setServletPathWhitelist(List<String> servletPathWhitelist) {
            this.servletPathWhitelist = servletPathWhitelist;
        }

        public String[] getXssRegexList() {
            return xssRegexList;
        }

        public void setXssRegexList(String[] xssRegexList) {
            this.xssRegexList = xssRegexList;
        }
    }

    public static class SQLInject {
        private boolean checkEnabled = false;
        private List<String> servletPathWhitelist = new ArrayList<>();
        private String[] sqlKeywordList = new String[] {"and ","exec ","insert ","select ","delete ","update ","drop ","count ","chr ","mid ","master ","truncate ","char ","declare ",";|or ","+|user()"};

        public boolean isCheckEnabled() {
            return checkEnabled;
        }

        public void setCheckEnabled(boolean checkEnabled) {
            this.checkEnabled = checkEnabled;
        }

        public List<String> getServletPathWhitelist() {
            return servletPathWhitelist;
        }

        public void setServletPathWhitelist(List<String> servletPathWhitelist) {
            this.servletPathWhitelist = servletPathWhitelist;
        }

        public String[] getSqlKeywordList() {
            return sqlKeywordList;
        }

        public void setSqlKeywordList(String[] sqlKeywordList) {
            this.sqlKeywordList = sqlKeywordList;
        }
    }
}
