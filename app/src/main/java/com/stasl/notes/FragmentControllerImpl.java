package com.stasl.notes;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.support.v4.app.FragmentActivity;

public class FragmentControllerImpl extends FragmentActivity implements FragmentController
{

    private final Activity activity;

    FragmentTransaction fragmentTransaction;

    public FragmentControllerImpl(Activity activity)
    {
        this.activity = activity;
    }

    @Override
    public void addFragment(int container, Fragment fragment)
    {
            fragmentTransaction = activity.getFragmentManager().beginTransaction();
            fragmentTransaction.add(container, fragment);
            fragmentTransaction.commit();
            activity.getFragmentManager().executePendingTransactions();
    }

    @Override
    public void changeFragment(int container, Fragment fragment)
    {
        fragmentTransaction = activity.getFragmentManager().beginTransaction();
        fragmentTransaction.replace(container, fragment);
        fragmentTransaction.commit();
        activity.getFragmentManager().executePendingTransactions();
    }

    @Override
    public void removeFragment(Fragment fragment)
    {
        fragmentTransaction = activity.getFragmentManager().beginTransaction();
        fragmentTransaction.remove(fragment);
        fragmentTransaction.commit();
        activity.getFragmentManager().executePendingTransactions();
    }
}