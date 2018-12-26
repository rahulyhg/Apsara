package apsara.saxxis.models;

import com.orm.SugarRecord;
import com.orm.dsl.Unique;

public class Stock extends SugarRecord {

    private String stock;

    private String strikeprice;

    private String status;

    private String gst_price;

    private int stockId;

    private String quantity;

    private String price_val;

    private String gst_text;

    private String discount_percentage;

    public String getStock ()
    {
        return stock;
    }

    public void setStock (String stock)
    {
        this.stock = stock;
    }

    public String getStrikeprice ()
    {
        return strikeprice;
    }

    public void setStrikeprice (String strikeprice)
    {
        this.strikeprice = strikeprice;
    }

    public String getStatus ()
    {
        return status;
    }

    public void setStatus (String status)
    {
        this.status = status;
    }

    public String getGst_price ()
    {
        return gst_price;
    }

    public void setGst_price (String gst_price)
    {
        this.gst_price = gst_price;
    }

    public int getStockId ()
    {
        return stockId;
    }

    public void setStockId (int stockId)
    {
        this.stockId = stockId;
    }

    public String getQuantity ()
    {
        return quantity;
    }

    public void setQuantity (String quantity)
    {
        this.quantity = quantity;
    }

    public String getPrice_val ()
    {
        return price_val;
    }

    public void setPrice_val (String price_val)
    {
        this.price_val = price_val;
    }

    public String getGst_text ()
    {
        return gst_text;
    }

    public void setGst_text (String gst_text)
    {
        this.gst_text = gst_text;
    }

    public String getDiscount_percentage ()
    {
        return discount_percentage;
    }

    public void setDiscount_percentage (String discount_percentage)
    {
        this.discount_percentage = discount_percentage;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [stock = "+stock+", strikeprice = "+strikeprice+", status = "+status+", gst_price = "+gst_price+", stockId = "+stockId+", quantity = "+quantity+", price_val = "+price_val+", gst_text = "+gst_text+", discount_percentage = "+discount_percentage+"]";
    }
}
