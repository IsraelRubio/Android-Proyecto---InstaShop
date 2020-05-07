package israelontanilla.es.instashop.Adapters;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import Models.Product;
import israelontanilla.es.instashop.MainActivity;
import israelontanilla.es.instashop.R;

public class ProductsDataAdapter extends RecyclerView.Adapter<ProductsDataAdapter.ViewHolderProduct> {

    private View view;
    private List<Product> list;
    private int mPosition;

    public ProductsDataAdapter(List<Product> productList) {
        this.list = productList;
    }

    @NonNull
    @Override
    public ViewHolderProduct onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_products,null,false);
        return new ViewHolderProduct(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderProduct holder, int i) {
        final int position = i;
        // ---
        Product p = list.get(position);
        holder.setData(p);
        // ---

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPosition = position;
                notifyDataSetChanged();
            }
        });

        if (mPosition == i){
            holder.loadDialogProduct(view, i, holder);
        }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolderProduct extends RecyclerView.ViewHolder {
        TextView textViewNameProduct;
        TextView textViewNameProductDialog;
        TextView textViewPriceProductDialog;
        TextView textViewPriceProduct;
        TextView textViewDescriptionProductDialog;
        TextView textViewLocationProductDialog;
        ImageView imageViewProduct;
        ImageView imageViewProductDialog;
        Button btnCallDialog;
        CardView cardView;

        private ViewHolderProduct(@NonNull View itemView) {
            super(itemView);
            textViewNameProduct = itemView.findViewById(R.id.textViewNameProductList);
            textViewPriceProduct = itemView.findViewById(R.id.textViewPriceProductList);
            imageViewProduct = itemView.findViewById(R.id.imageViewProductList);
            cardView = itemView.findViewById(R.id.cardViewProductList);
        }

        private void setData(Product p) {
            textViewNameProduct.setText(p.getName());
            textViewPriceProduct.setText(p.getPrice());
        }

        private void loadDialogProduct(final View view, int position, ViewHolderProduct holder) {
            AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());

            final View viewDialogProduct = LayoutInflater.from(view.getContext()).inflate(R.layout.dialod_dates_products_list, null, false);

            builder.setView(viewDialogProduct);
            Dialog dialog = builder.create();
            dialog.show();

            final Product product = list.get(position);

            setDataDialog(viewDialogProduct, product);

            btnCallDialog.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(viewDialogProduct.getContext(), "Has pulsado la llamada!!", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + product.getMobile()));
                    view.getContext().startActivity(intent);

                }
            });
        }

        private void setDataDialog(View viewDialog, Product product){
            textViewNameProductDialog = viewDialog.findViewById(R.id.textViewNameProductDialog);
            textViewPriceProductDialog = viewDialog.findViewById(R.id.textViewPriceProductDialog);
            textViewDescriptionProductDialog = viewDialog.findViewById(R.id.textViewDescriptionProductDialog);
            textViewLocationProductDialog = viewDialog.findViewById(R.id.textViewLocationProductDialog);
            imageViewProductDialog = viewDialog.findViewById(R.id.imageViewProductDialog);
            btnCallDialog = viewDialog.findViewById(R.id.btnCallProductDialog);

            textViewNameProductDialog.setText(product.getName());
            textViewPriceProductDialog.setText(product.getPrice());
            textViewLocationProductDialog.setText(product.getLocation());
        }
    }
}
