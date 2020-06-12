package israelontanilla.es.instashop.Adapters;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DownloadManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.List;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

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
    public void onBindViewHolder(@NonNull final ViewHolderProduct holder, final int i) {
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

                if (mPosition == i){
                    holder.loadDialogProduct(view, i, holder);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolderProduct extends RecyclerView.ViewHolder {
        TextView textViewNameProduct;
        TextView textViewNameProductDialog;
        TextView textViewSellerProductDialog;
        TextView textViewPriceProductDialog;
        TextView textViewPriceProduct;
        TextView textViewDescriptionProductDialog;
        TextView textViewLocationProductDialog;
        TextView textViewCategoryProductDialog;
        ImageView imageViewProduct;
        ImageView imageViewProductDialog;
        ImageButton btnCallDialog;
        CardView cardView;
        Session session = null;

        private ViewHolderProduct(@NonNull View itemView) {
            super(itemView);
            textViewNameProduct = itemView.findViewById(R.id.textViewNameProductList);
            textViewPriceProduct = itemView.findViewById(R.id.textViewPriceProductList);
            imageViewProduct = itemView.findViewById(R.id.imageViewProductList);
            cardView = itemView.findViewById(R.id.cardViewProductList);
        }

        private void setData(Product p) {
            textViewNameProduct.setText(p.getName());
            textViewPriceProduct.setText(String.valueOf(p.getPrice()));
            if (p.getImage().trim().equals("")){
                imageViewProduct.setImageResource(R.mipmap.recyclerviewitemsale);
            }else{
                Glide.with(view.getContext())
                        .load(p.getImage())
                        .fitCenter()
                        .centerCrop()
                        .into(imageViewProduct);
            }

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
                    AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());

                    final View viewDialogMsn = LayoutInflater.from(view.getContext()).inflate(R.layout.dialog_msn_product, null, false);

                    builder.setView(viewDialogMsn);
                    final Dialog dialog = builder.create();
                    dialog.show();

                    final EditText msn = viewDialogMsn.findViewById(R.id.editTextMsn);
                    Button btnSendMsn = viewDialogMsn.findViewById(R.id.btnSendMsn);

                    btnSendMsn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            final String msnText = msn.getText().toString();

                            FirebaseAuth mAuth = FirebaseAuth.getInstance();
                            DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

                            // Saco el id del user actual y creo la query
                            //----------------------------------------------------------
                            String id = "";
                            if(mAuth.getCurrentUser() != null)
                                id = mAuth.getCurrentUser().getUid();

                            Query query = mDatabase.child("Users").child(id);
                            query.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    String mEmail = String.valueOf(dataSnapshot.child("email").getValue());

                                    sendMsn(msnText, product, mEmail);
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                            //-----------------------------------------------------------

                            dialog.dismiss();
                        }
                    });

                }
            });
        }

        private void sendMsn(String message, Product product, String mEmail){
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build(); //Construyo unas politicas
            StrictMode.setThreadPolicy(policy); // las agrego
            Properties properties = new Properties();
            properties.put("mail.smtp.host", "smtp.googlemail.com");
            properties.put("mail.smtp.socketFactory.port", "465"); // socket para recibir respuesta de gmail
            properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory"); // trabajar con socket con ssl
            properties.put("mail.smtp.auth", "true"); // indico que nos autenticamos

            try {
                // autentico los datos
                session = Session.getDefaultInstance(properties, new Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication("speedshopcompany@gmail.com", "speedshopapp123456789");
                    }
                });

                if (session != null){
                    session.setDebug(true);
                    String msn = "<b>Hello!!</b><br>" +
                            "<b>I´m " +product.getSeller()+ "</b><br>" +
                            message +"<br>" +
                            "<br>" +
                            "<b>Contact: " +mEmail+ "</b>";
                    Message messageProduct = new MimeMessage(session);
                    messageProduct.setFrom(new InternetAddress("speedshopcompany@gmail.com")); // emisor
                    messageProduct.setSubject("Message of " +product.getSeller()); // asunto
                    messageProduct.setRecipients(Message.RecipientType.TO, InternetAddress.parse(product.getEmail())); // receptor
                    messageProduct.setContent(msn,"text/html; charset=utf-8");
                    Transport.send(messageProduct);
                }

            }catch (Exception e){
                e.printStackTrace();
            }

        }

        private void setDataDialog(View viewDialog, Product product){
            textViewNameProductDialog = viewDialog.findViewById(R.id.textViewNameProductDialog);
            textViewPriceProductDialog = viewDialog.findViewById(R.id.textViewPriceProductDialog);
            textViewSellerProductDialog = viewDialog.findViewById(R.id.textViewSellerProductDialog);
            textViewLocationProductDialog = viewDialog.findViewById(R.id.textViewLocationProductDialog);
            textViewCategoryProductDialog = viewDialog.findViewById(R.id.textViewCategoryProductDialog);
            imageViewProductDialog = viewDialog.findViewById(R.id.imageViewProductDialog);
            btnCallDialog = viewDialog.findViewById(R.id.btnCallProductDialog);
            if (product.getImage().trim().equals("")){
                imageViewProductDialog.setImageResource(R.mipmap.sale_empty_icon);
            }else{
                Glide.with(viewDialog.getContext())
                        .load(product.getImage())
                        .fitCenter()
                        .centerCrop()
                        .into(imageViewProductDialog);
            }
            textViewSellerProductDialog.setText(product.getSeller());
            textViewNameProductDialog.setText(product.getName());
            textViewPriceProductDialog.setText(String.valueOf(product.getPrice()));
            textViewLocationProductDialog.setText(product.getLocation());
            textViewCategoryProductDialog.setText(product.getCategory());
        }
    }
}
