package far.com.eatit.Adapters.Models;

import java.util.ArrayList;

public class OrderModel {
    String orderNum;
    String time;
    String notes;
    ArrayList<OrderDetailModel> detail;
    String status;
    boolean edited;

    public OrderModel(String orderNum,String status,String notes, String time,boolean edited, ArrayList<OrderDetailModel> detail){
        this.orderNum = orderNum;
        this.time = time;
        this.detail = detail;
        this.notes = notes;
        this.status = status;
        this.edited = edited;
    }

    public String getOrderNum() {
        return orderNum;
    }

    public String getTime() {
        return time;
    }

    public ArrayList<OrderDetailModel> getDetail() {
        return detail;
    }

    public String getNotes() {
        return notes;
    }

    public String getStatus() {
        return status;
    }

    public boolean isEdited() {
        return edited;
    }
}
