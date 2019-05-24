package far.com.eatit.Adapters.Models;

public class TableFilterRowModel {
        String code, tables, user, usertype,producttype,productsubtype, task, filter;
        String userDescription,  userTypeDescription, productTypeDescription, productSubTypeDescription;
        boolean enabled, inserver;

        public TableFilterRowModel(String code, String tables, String user,String userDescription,String userType,String userTypeDescription,
                                   String productType,String productTypeDescription, String productSubType,String productSubTypeDescription,
                                   String task, String filter, String enabled, boolean inServer){
            this.code = code;
            this.tables = tables;
            this.user = user;
            this.userDescription = userDescription;
            this.usertype = userType;
            this.userTypeDescription = userTypeDescription;
            this.producttype = productType;
            this.productTypeDescription = productTypeDescription;
            this.productsubtype = productSubType;
            this.productSubTypeDescription = productSubTypeDescription;
            this.task = task;
            this.filter = filter;
            this.enabled = enabled.equals("1");
            this.inserver = inServer;

        }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getTables() {
        return tables;
    }

    public void setTables(String tables) {
        this.tables = tables;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getUsertype() {
        return usertype;
    }

    public void setUsertype(String usertype) {
        this.usertype = usertype;
    }

    public String getProducttype() {
        return producttype;
    }

    public void setProducttype(String producttype) {
        this.producttype = producttype;
    }

    public String getProductsubtype() {
        return productsubtype;
    }

    public void setProductsubtype(String productsubtype) {
        this.productsubtype = productsubtype;
    }

    public String getTask() {
        return task;
    }

    public void setTask(String task) {
        this.task = task;
    }

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isInserver() {
        return inserver;
    }

    public void setInserver(boolean inserver) {
        this.inserver = inserver;
    }

    public String getUserDescription() {
        return userDescription;
    }

    public void setUserDescription(String userDescription) {
        this.userDescription = userDescription;
    }

    public String getUserTypeDescription() {
        return userTypeDescription;
    }

    public void setUserTypeDescription(String userTypeDescription) {
        this.userTypeDescription = userTypeDescription;
    }

    public String getProductTypeDescription() {
        return productTypeDescription;
    }

    public void setProductTypeDescription(String productTypeDescription) {
        this.productTypeDescription = productTypeDescription;
    }

    public String getProductSubTypeDescription() {
        return productSubTypeDescription;
    }

    public void setProductSubTypeDescription(String productSubTypeDescription) {
        this.productSubTypeDescription = productSubTypeDescription;
    }
}
