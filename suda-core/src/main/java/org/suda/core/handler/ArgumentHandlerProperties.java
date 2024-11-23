package org.suda.core.handler;

import org.suda.core.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 参数安全检查配置，用于接收自动配置类的数据
 * @author chengshaozhuang
 * @dateTime 2024-10-24 16:18
 */
public class ArgumentHandlerProperties {

    private final XSSAttack xssAttack = new XSSAttack();
    private final SQLInject sqlInject = new SQLInject();
    private final Chars chars = new Chars();
    private final Files files = new Files();

    public XSSAttack getXssAttack() {
        return xssAttack;
    }

    public SQLInject getSqlInject() {
        return sqlInject;
    }

    public Chars getChars() {
        return chars;
    }

    public Files getFiles() {
        return files;
    }

    public boolean hasBeenCustomized() {
        return this.chars.hasBeenCustomized() ||
                this.sqlInject.hasBeenCustomized() ||
                this.xssAttack.hasBeenCustomized() ||
                this.files.hasBeenCustomized();
    }

    public static class Chars {
        // 是否对字符串去空格
        private boolean trimEnabled = true;

        private boolean customized = false;

        public boolean isTrimEnabled() {
            return trimEnabled;
        }

        public void setTrimEnabled(boolean trimEnabled) {
            this.trimEnabled = trimEnabled;
            this.customized = true;
        }

        public boolean hasBeenCustomized() {
            return customized;
        }
    }

    public static class XSSAttack {
        // 是否启用XSS攻击检查，默认不启用
        private boolean checkEnabled = false;
        private boolean customized = false;
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
            this.customized = true;
        }

        public List<String> getServletPathWhitelist() {
            return servletPathWhitelist;
        }

        public void setServletPathWhitelist(List<String> servletPathWhitelist) {
            this.servletPathWhitelist = servletPathWhitelist;
            this.customized = true;
        }

        public String[] getXssRegexList() {
            return xssRegexList;
        }

        public void setXssRegexList(String[] xssRegexList) {
            this.xssRegexList = xssRegexList;
            this.customized = true;
        }

        public boolean hasBeenCustomized() {
            return customized;
        }
    }

    public static class SQLInject {
        private boolean checkEnabled = false;
        private boolean customized = false;
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
            this.customized = true;
        }

        public String[] getSqlKeywordList() {
            return sqlKeywordList;
        }

        public void setSqlKeywordList(String[] sqlKeywordList) {
            this.sqlKeywordList = sqlKeywordList;
            this.customized = true;
        }

        public boolean hasBeenCustomized() {
            return customized;
        }
    }

    public static class Files {
        private boolean checkEnabled = false;
        private boolean customized = false;
        private List<String> servletPathWhitelist = new ArrayList<>();
        private String[] extensionBlacklist = new String[]{".bat,",".cmd,",".vbs,",".sh,",".java,",
                ".class,",".js",".ts",".jsp",".html",".htm",".xhtml",".php",".py"};

        public boolean isCheckEnabled() {
            return checkEnabled;
        }

        public void setCheckEnabled(boolean checkEnabled) {
            this.checkEnabled = checkEnabled;
            this.customized = true;
        }

        public List<String> getServletPathWhitelist() {
            return servletPathWhitelist;
        }

        public void setServletPathWhitelist(List<String> servletPathWhitelist) {
            this.servletPathWhitelist = servletPathWhitelist;
            this.customized = true;
        }

        public String[] getExtensionBlacklist() {
            return extensionBlacklist;
        }

        public void setExtensionBlacklist(String[] extensionBlacklist) {
            this.extensionBlacklist = StringUtils.appendDotIfNecessary(extensionBlacklist);
            this.customized = true;
        }

        public boolean hasBeenCustomized() {
            return customized;
        }
    }
}
