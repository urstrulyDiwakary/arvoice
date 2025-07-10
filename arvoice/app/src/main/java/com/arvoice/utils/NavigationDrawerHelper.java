package com.arvoice.utils;

import android.app.Activity;
import android.content.Context;
import android.view.View;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.arvoice.R;
import com.arvoice.databinding.LayoutNavigationDrawerBinding;


public class NavigationDrawerHelper implements View.OnClickListener {

    private Context context;
    private DrawerLayout drawer;

    private NavigationItemClickListener listener;
    LayoutNavigationDrawerBinding drawerContentLayoutBinding;

    public interface NavigationItemClickListener {
        public void onClick(boolean isClick);
    }

    public void setUp(Activity context,
                      DrawerLayout drawer,
                      LayoutNavigationDrawerBinding drawerContentLayoutBinding, Toolbar toolbar,
                      NavigationItemClickListener listener) {

        this.drawer = drawer;
        this.context = context;
        this.drawerContentLayoutBinding = drawerContentLayoutBinding;


        //setDrawer Icon
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                context, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        toggle.setDrawerIndicatorEnabled(false);


        toggle.setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawer.openDrawer(GravityCompat.START);
            }
        });
        toggle.setHomeAsUpIndicator(R.drawable.icon_hamburger);

        drawer.addDrawerListener(toggle);
        toggle.syncState();

        drawerContentLayoutBinding.llAdd.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        if (v == drawerContentLayoutBinding.llAdd) {
            drawer.closeDrawer(GravityCompat.START);
//            context.startActivity(AddCustomerActivity.makeIntent(context));
        }

    }

}
