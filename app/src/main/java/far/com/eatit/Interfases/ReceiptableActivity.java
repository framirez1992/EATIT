package far.com.eatit.Interfases;

import java.util.ArrayList;

import far.com.eatit.CloudFireStoreObjects.Receipts;
import far.com.eatit.CloudFireStoreObjects.Sales;

public interface ReceiptableActivity {
    void closeOrders(Receipts receipt, ArrayList<Sales> sales);
    void showReceiptFragment();
}
