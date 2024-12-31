package application.bookstore.models;

import java.util.ArrayList;
import java.util.Date;

public class Bill {
    private int orderId;
    private final ArrayList<SoldBookType> booksSold;
    private double amount = 0;
    protected Date dateIssued;

    public Bill(ArrayList<SoldBookType> booksSold,Date date) {
        this.orderId++;
        this.booksSold = booksSold;
        for(SoldBookType books:booksSold)
            amount += books.getSoldBook().getSellingPrice() * books.getSoldQuantity();
        this.dateIssued=date;
    }

    public double getTotalAmount() {
        return amount;
    }

    public ArrayList<SoldBookType> getBooksSold() {
        return booksSold;
    }

    public Date getDateIssued() {
        return dateIssued;
    }

    public int getOrderId() {
        return orderId;
    }

}
