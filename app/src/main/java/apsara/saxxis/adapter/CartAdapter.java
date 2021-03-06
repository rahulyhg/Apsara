package apsara.saxxis.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;

import apsara.saxxis.R;
import apsara.saxxis.activities.MainActivity;
import apsara.saxxis.activities.ProductDetailsActivity;
import apsara.saxxis.models.Stock;
import apsara.saxxis.models.Product;
import apsara.saxxis.reterofit.APIUrls;
import apsara.saxxis.util.DatabaseHandler;

/**
 * Created by Rajesh Dabhi on 26/6/2017.
 */

public class CartAdapter extends CommonRecyclerAdapter<Product> {
    private Context context;
    private DatabaseHandler dbcart;
    private Toast toast;

    public CartAdapter(Context context) {
        this.context = context;
        dbcart = new DatabaseHandler();
    }

    @Override
    public RecyclerView.ViewHolder onCreateBasicItemViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product, parent, false);

        return new CartHolder(view);
    }

    @Override
    public void onBindBasicItemView(RecyclerView.ViewHolder holder, int position) {
        CartHolder viewHolder = (CartHolder) holder;
        viewHolder.bindData(position);
    }

    private class CartHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView tv_subcat_title, tv_subcat_price, tv_subcat_content, tv_subcat_discount_price, tv_discount, tv_out_of_stock;
        public ImageView iv_subcat, iv_subcat_plus, iv_subcat_minus, iv_subcat_remove;
        public Spinner spinner_subcat;

        private CartHolder(View view) {
            super(view);

            tv_out_of_stock = view.findViewById(R.id.tv_out_of_stock);
            tv_out_of_stock.setVisibility(View.GONE);
            tv_discount = view.findViewById(R.id.tv_discount);
            tv_subcat_title = view.findViewById(R.id.tv_subcat_title);
            tv_subcat_discount_price = view.findViewById(R.id.tv_subcat_discount_price);
            tv_subcat_price = view.findViewById(R.id.tv_subcat_price);
            tv_subcat_price.setPaintFlags(tv_subcat_price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            tv_subcat_content = view.findViewById(R.id.tv_subcat_content);
            iv_subcat = view.findViewById(R.id.iv_subcat);
            iv_subcat_plus = view.findViewById(R.id.iv_subcat_plus);
            iv_subcat_minus = view.findViewById(R.id.iv_subcat_minus);
            iv_subcat_remove = view.findViewById(R.id.iv_subcat_remove);
            spinner_subcat = view.findViewById(R.id.spinner_subcat);

            iv_subcat_remove.setOnClickListener(this);
            iv_subcat_minus.setOnClickListener(this);
            iv_subcat_plus.setOnClickListener(this);
            iv_subcat.setOnClickListener(this);

            tv_subcat_title.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int id = view.getId();
            int position = getAdapterPosition();
            if (position != -1) {
                final Product product = getItem(position);

                switch (id) {
                    case R.id.iv_subcat_remove:

                        if (product.getId() != null) {
                            dbcart.removeItemFromCart(product.getProduct_id(), product.getStockId());
                            removeItem(position);
                            updateIntent();
                        }

                        break;
                    case R.id.iv_subcat_plus:
                        int qty = Integer.valueOf(tv_subcat_content.getText().toString());
                        qty = qty + 1;

                        if (qty > product.getQuantity_per_user()) {

                            if (toast != null) {
                                toast.cancel();
                            }

                            toast = Toast.makeText(context, String.format("Only %s items allowed per user for this product", product.getQuantity_per_user()), Toast.LENGTH_SHORT);
                            toast.show();
                        } else {
                            final Stock stock = (Stock) spinner_subcat.getSelectedItem();
                            final int qty1 = Integer.parseInt(tv_subcat_content.getText().toString().trim());

                            if (!TextUtils.isEmpty(stock.getStock()) && Integer.parseInt(stock.getStock()) > qty1) {
                                tv_subcat_content.setText(String.valueOf(qty));
                                addProduct(product);
                            } else {
                                if (toast != null) {
                                    toast.cancel();
                                }

                                if (stock.getStock().equals("0")) {
                                    toast = Toast.makeText(context, R.string.product_out_of_stock, Toast.LENGTH_SHORT);
                                    toast.show();
                                } else {
                                    toast = Toast.makeText(context, String.format("Only %s products left", stock.getStock()), Toast.LENGTH_SHORT);
                                    toast.show();
                                }
                            }
                        }

                        break;
                    case R.id.spinner_subcat:
                        int qty1 = Integer.valueOf(tv_subcat_content.getText().toString());
                        qty1 = qty1 + 1;

                        tv_subcat_content.setText(String.valueOf(qty1));
                        break;
                    case R.id.iv_subcat_minus:
                        int qty2 = 0;
                        if (!tv_subcat_content.getText().toString().equalsIgnoreCase(""))
                            qty2 = Integer.valueOf(tv_subcat_content.getText().toString());

                        if (qty2 > 0) {
                            qty2 = qty2 - 1;
                            tv_subcat_content.setText(String.valueOf(qty2));
                        }

                        addProduct(product);
                        break;

                    case R.id.tv_subcat_title:
                        List<Stock> stocks = new Gson().fromJson(product.getStocks(), new TypeToken<List<Stock>>() {
                        }.getType());
                        product.setCustom_fields(stocks);
                        String value = new Gson().toJson(product);
                        Intent intent = new Intent(context, ProductDetailsActivity.class);
                        intent.putExtra(ProductDetailsActivity.PRODUCT, value);
                        Activity activity = (Activity) context;
                        activity.startActivityForResult(intent, ProductDetailsActivity.PRODUCT_DETAIL);
                        break;
                }
            }
        }

        private synchronized void addProduct(final Product product) {
            final Stock stock = (Stock) spinner_subcat.getSelectedItem();
            final int qty = Integer.parseInt(tv_subcat_content.getText().toString().trim());

            if (qty > 0) {
                if (Integer.parseInt(stock.getStock()) >= qty) {

                    if (qty > product.getQuantity_per_user()) {
                        if (toast != null) {
                            toast.cancel();
                        }

                        toast = Toast.makeText(context, String.format("Only %s items allowed per user for this product", product.getQuantity_per_user()), Toast.LENGTH_SHORT);
                        toast.show();
                    } else {

                        product.setStockId(stock.getStockId());
                        product.setQuantity(qty);

                        dbcart.setCart(product);
                        tv_subcat_content.setText(String.valueOf(qty));
                        updateIntent();

                        ((MainActivity) context).setCartCounter(String.valueOf(dbcart.getCartCount()));
                    }
                } else {
                    if (toast != null) {
                        toast.cancel();
                    }

                    if (stock.getStock().equals("0")) {
                        toast = Toast.makeText(context, R.string.product_out_of_stock, Toast.LENGTH_SHORT);
                        toast.show();
                    } else {
                        toast = Toast.makeText(context, String.format("Only %s products left", stock.getStock()), Toast.LENGTH_SHORT);
                        toast.show();
                    }
                }
            } else {
                Product p = dbcart.getProduct(product.getProduct_id(), product.getStockId());

                if (p != null) {
                    dbcart.removeItemFromCart(p.getProduct_id(), product.getStockId());
                }

                removeItem(getAdapterPosition());

                updateIntent();

                ((MainActivity) context).setCartCounter(String.valueOf(dbcart.getCartCount()));
            }
        }

        private void bindData(int position) {
            final Product product = getItem(position);

            List<Stock> stocks = new Gson().fromJson(product.getStocks(), new TypeToken<List<Stock>>() {
            }.getType());

            final PriceAdapter priceAdapter = new PriceAdapter(context, stocks);
            spinner_subcat.setAdapter(priceAdapter);
            spinner_subcat.setEnabled(false);
            spinner_subcat.setClickable(false);

            RequestOptions requestOptions = new RequestOptions()
                    .placeholder(R.drawable.ic_placeholder)
                    .error(R.drawable.ic_placeholder)
                    .diskCacheStrategy(DiskCacheStrategy.ALL);

            String image = product.getProduct_image();

            if (image.contains(",")) {
                String[] images = image.split(",");

                if (images.length > 0) {
                    image = images[0];
                }
            }

            Glide.with(context)
                    .load(APIUrls.IMG_PRODUCT_URL + image.replace(" ", "%20"))
                    .apply(requestOptions)
                    .into(iv_subcat);

            tv_subcat_title.setText(product.getProduct_name());

            tv_subcat_content.setText(dbcart.getCartItemQty(product.getProduct_id(), product.getStockId()));

            for (int i = 0; i < stocks.size(); i++) {
                Stock stock = stocks.get(i);

                if (product.getStockId() == stock.getStockId()) {
                    spinner_subcat.setSelection(i);

                    if (TextUtils.isEmpty(stock.getStrikeprice())) {
                        tv_subcat_discount_price.setText(String.format("\u20B9 %s", stock.getPrice_val()));
                        tv_subcat_price.setVisibility(View.GONE);
                        tv_discount.setVisibility(View.GONE);
                    } else {
//                        float price = Integer.parseInt(stock.getPrice_val());
//                        float discountPrice = Integer.parseInt(stock.getStrikeprice());
//                        int result = (int) Math.ceil(((price - discountPrice) / price) * 100);

                        tv_discount.setVisibility(View.VISIBLE);
                        tv_discount.setText(stock.getDiscount_percentage());
                        tv_subcat_price.setVisibility(View.VISIBLE);
                        tv_subcat_price.setText(String.format("(\u20B9 %s)", stock.getPrice_val()));
                        tv_subcat_discount_price.setText(String.format("\u20B9 %s", stock.getStrikeprice()));
                    }

                    if (stock.getStock().equals("0")) {
                        iv_subcat_plus.setOnClickListener(null);
                        iv_subcat_minus.setOnClickListener(null);
                        tv_out_of_stock.setVisibility(View.VISIBLE);
                    } else {
                        iv_subcat_minus.setOnClickListener(CartHolder.this);
                        iv_subcat_plus.setOnClickListener(CartHolder.this);
                        tv_out_of_stock.setVisibility(View.GONE);
                    }
                    break;
                }
            }
        }
    }

    private void updateIntent() {
        Intent updates = new Intent("Grocery_cart");
        updates.putExtra("type", "update");
        context.sendBroadcast(updates);
    }
}

