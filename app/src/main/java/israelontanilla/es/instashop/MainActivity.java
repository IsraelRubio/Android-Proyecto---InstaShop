package israelontanilla.es.instashop;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

import israelontanilla.es.instashop.Fragments.HelpFragment;
import israelontanilla.es.instashop.Fragments.ProductsFragment;
import israelontanilla.es.instashop.Fragments.ProfileFragment;
import israelontanilla.es.instashop.Fragments.SaleFragment;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView mBottonNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBottonNavigation = findViewById(R.id.bottonNavigation);

        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.container, new ProfileFragment())
                .commit();


        mBottonNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                if (item.getItemId() == R.id.menu_profile){
                    showSelectedFragment(new ProfileFragment());
                }

                if (item.getItemId() == R.id.menu_sale){
                    showSelectedFragment(new SaleFragment());
                }

                if (item.getItemId() == R.id.menu_products){
                    showSelectedFragment(new ProductsFragment());
                }

                if (item.getItemId() == R.id.menu_help){
                    showSelectedFragment(new HelpFragment());
                }

                return true;
            }
        });
    }

    private void showSelectedFragment(Fragment fragment){
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, fragment)
                .commit();
    }

}
