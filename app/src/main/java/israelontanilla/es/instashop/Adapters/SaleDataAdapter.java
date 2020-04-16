package israelontanilla.es.instashop.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import Models.Product;
import israelontanilla.es.instashop.R;

public class SaleDataAdapter extends RecyclerView.Adapter<SaleDataAdapter.ViewHolderSale>
implements View.OnClickListener {

    View.OnClickListener listener;
    List<Product> productList;

    public SaleDataAdapter(List<Product> list) {
        productList = list;
    }

    @NonNull
    @Override
    public ViewHolderSale onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_sale,null,false);

        view.setOnClickListener(this);

        return new ViewHolderSale(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderSale holder, int position) {
        Product p = productList.get(position);

        holder.SetData(p);
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public void setOnClickListener(View.OnClickListener listener){
        this.listener = listener;
    }

    @Override
    public void onClick(View v) {
        if (listener != null){
            listener.onClick(v);
        }
    }

    public class ViewHolderSale extends RecyclerView.ViewHolder {

        TextView textViewName;
        TextView textViewPrice;

        public ViewHolderSale(@NonNull View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.textViewNameSale);
            textViewPrice = itemView.findViewById(R.id.textViewPriceSale);
        }

        public void SetData(Product p) {
            textViewName.setText(p.getNombre());
            textViewPrice.setText(String.valueOf(p.getPrecio()));
        }
    }
}
