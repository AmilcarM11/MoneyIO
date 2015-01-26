package com.menjivar.android.ingresosgastos;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

/**
 *
 * Created by Amilcar Menjivar on 04/01/2015.
 */
public class RegistryPagerAdapter extends FragmentPagerAdapter implements ViewPager.OnPageChangeListener {

    private static String[] tabtitles = { "Rubros", "Cuentas", "Registros" };

    public RegistryPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    Fragment[] fragments = new Fragment[3];

    @Override
    public Fragment getItem(int position) {
        if(fragments[position] == null) {
            switch(position) {
                case 0: // Rubro
                    if(fragments[position] == null ) {
                        fragments[position] = new ListRubrosFragment();
                    }
                    break;
                case 1: // Cuenta
                    if(fragments[position] == null ) {
                        fragments[position] = new ListCuentasFragment();
                    }
                    break;
                case 2: // Registro
                    if(fragments[position] == null ) {
                        fragments[position] = new ListRegistrosFragment();
                    }
                    break;
            }
        }
        return fragments[position];
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabtitles[position];
    }

    @Override
    public int getCount() {
        return fragments.length;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

    @Override
    public void onPageSelected(int position) {
        //
    }

    @Override
    public void onPageScrollStateChanged(int state) {}
}
