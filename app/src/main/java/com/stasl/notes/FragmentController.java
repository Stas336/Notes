package com.stasl.notes;

import android.app.Fragment;

public interface FragmentController
{
    void addFragment(int container, Fragment fragment);
    void changeFragment(int container, Fragment fragment);
    void removeFragment(Fragment fragment);
}
