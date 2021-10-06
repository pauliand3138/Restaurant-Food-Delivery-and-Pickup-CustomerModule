package com.example.capstoneproject;

import android.content.ClipData;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.capstoneproject.Common.Common;
import com.example.capstoneproject.Interface.ItemClickListener;
import com.example.capstoneproject.Model.Food;
import com.example.capstoneproject.Model.FoodCategory;
import com.example.capstoneproject.ViewHolder.MenuViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.view.menu.MenuView;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.capstoneproject.databinding.ActivityHomeBinding;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

public class Home extends AppCompatActivity {

    FirebaseDatabase database;
    DatabaseReference category;
    TextView txtFullName;
    RecyclerView recyclerMenu;
    RecyclerView.LayoutManager layoutManager;

    FirebaseRecyclerAdapter<FoodCategory,MenuViewHolder> adapter;

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityHomeBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //Init Firebase
        database = FirebaseDatabase.getInstance("https://capstoneproject-c2dbe-default-rtdb.asia-southeast1.firebasedatabase.app");
        category = database.getReference("FoodCategory");



        setSupportActionBar(binding.appBarHome.toolbar);
        binding.appBarHome.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent cartIntent = new Intent(Home.this,Cart.class);
                startActivity(cartIntent);
            }
        });
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_home);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                if(id == R.id.nav_menu){

                }
                else if(id == R.id.nav_cart){
                    Intent cartIntent = new Intent(Home.this,Cart.class);
                    startActivity(cartIntent);
                }
                else if(id == R.id.nav_orders){
                    Intent orderIntent = new Intent(Home.this,OrderStatus.class);
                    startActivity(orderIntent);
                }
                else if(id == R.id.nav_log_out){
                    //Logout
                    Intent logIn = new Intent(Home.this,Login.class);
                    logIn.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(logIn);
                } else if(id == R.id.nav_ratings){
                    Intent ratingList = new Intent(Home.this, RatingList.class);
                    startActivity(ratingList);
                } else if(id == R.id.nav_profile){
                    Intent profile = new Intent(Home.this, Profile.class);
                    startActivity(profile);
                }
                return false;
            }
        });

        //Set Name for user
        View headerView = navigationView.getHeaderView(0);
        txtFullName = headerView.findViewById(R.id.txtFullName);
        txtFullName.setText(Common.currentUser.getCustName());

        //Load menu
        recyclerMenu = findViewById(R.id.recycler_menu);
        recyclerMenu.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerMenu.setLayoutManager(layoutManager);
        
        loadMenu();
    }

    private void loadMenu() {

        adapter = new FirebaseRecyclerAdapter<FoodCategory, MenuViewHolder>(FoodCategory.class, R.layout.menu_item, MenuViewHolder.class,category) {
            @Override
            protected void populateViewHolder(MenuViewHolder menuViewHolder, FoodCategory foodCategory, int i) {
                menuViewHolder.txtMenuName.setText(foodCategory.getFoodCatName());
//                Picasso.with(getBaseContext()).load(foodCategory.getFoodCatImageURL()).into(menuViewHolder.imageView);
                Glide.with(getBaseContext()).load(foodCategory.getFoodCatImageURL()).into(menuViewHolder.imageView);
                FoodCategory clickItem = foodCategory;
                menuViewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        //Get the foodCategoryID which the user selected and send to new Activity
                        Intent foodList = new Intent(Home.this, FoodList.class); //Need to put Home.this because .this refers to ItemClickListener

                        //foodCategoryID is a key in firebase, so we just need to use getKey, and send this info to new Activity
                        foodList.putExtra("Food Category ID", adapter.getRef(position).getKey());
                        startActivity(foodList);
                    }
                });
            }
        };
        recyclerMenu.setAdapter(adapter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_home);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    public void onBackPressed(){
        Intent orderTypeIntent = new Intent(Home.this,OrderType.class);
        startActivity(orderTypeIntent);
        finish();
    }

    //Refresh customer name after applying changes in Profile page
    @Override
    protected void onRestart() {
        super.onRestart();
        finish();
        overridePendingTransition(0, 0);
        startActivity(getIntent());
        overridePendingTransition(0, 0);
    }
}
