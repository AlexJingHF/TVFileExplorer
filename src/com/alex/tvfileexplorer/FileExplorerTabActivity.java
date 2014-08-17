package com.alex.tvfileexplorer;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.ActionMode;
import com.alex.sdk.log.AndroidLogUtil;
import com.alex.tvfileexplorer.util.Util;
import com.alex.tvfileexplorer.view.FileCategoryFragment;

import java.util.ArrayList;

public class FileExplorerTabActivity extends Activity {

    private static final String TAG = FileExplorerTabActivity.class.getSimpleName();
    private static final String INSTANCE_STATE_TAB = "TAB"; //储存页面TAB的状态（页面处于第几页）

    private static final int DEFAULT_OFFSCREEN_PAGES = 2;   //viewPager预加载的页面数量

    private ViewPager mViewPager = null;
    private ActionMode mActionMode = null;                  //控制actionBar的显示模式，主要是竖屏下得情况
    private TabsAdapter mTabsAdapter = null;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_pager);
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setOffscreenPageLimit(DEFAULT_OFFSCREEN_PAGES);//设置最多预加载的view
        //设置ActionBar的显示模式，测试需要多用几种模式测试
        final ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        actionBar.setDisplayOptions(0,ActionBar.DISPLAY_SHOW_TITLE | ActionBar.DISPLAY_SHOW_HOME);

        mTabsAdapter = new TabsAdapter(this,mViewPager);
        mTabsAdapter.addTab(actionBar.newTab().setText(getString(R.string.tab_category)), FileCategoryFragment.class,null);
        mTabsAdapter.addTab(actionBar.newTab().setText(getString(R.string.tab_sd)), FileCategoryFragment.class,null);
        mTabsAdapter.addTab(actionBar.newTab().setText(getString(R.string.tab_remote)), FileCategoryFragment.class,null);
        //返回到上次退出TAB
        actionBar.setSelectedNavigationItem(PreferenceManager.getDefaultSharedPreferences(this).getInt(INSTANCE_STATE_TAB,Util.TAB_INDEX_CATEGORY));
    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
        editor.putInt(INSTANCE_STATE_TAB, getActionBar().getSelectedNavigationIndex());//记录actionBar位置
        editor.commit();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        if (Util.TAB_INDEX_CATEGORY == getActionBar().getSelectedNavigationIndex()) {
            AndroidLogUtil.d(TAG, "onConfigurationChanged -> TAB_INDEX_CATEGORY");
        }
        super.onConfigurationChanged(newConfig);
    }

    /**
     * 重新初始化分类界面
     */
    public void reInstantiateCategoryTab() {
        mTabsAdapter.destroyItem(mViewPager, Util.TAB_INDEX_CATEGORY , mTabsAdapter.getItem(Util.TAB_INDEX_CATEGORY));
        mTabsAdapter.instantiateItem(mViewPager, Util.TAB_INDEX_CATEGORY);
    }

    public ActionMode getActionMode() {
        return mActionMode;
    }

    public void setActionMode(ActionMode actionMode) {
        this.mActionMode = actionMode;
    }

    public Fragment getFragmet(int position){
       return mTabsAdapter.getItem(position);
    }

    /**
     * 方便控制页面转换与标签设置
     */
    public static class TabsAdapter extends FragmentPagerAdapter implements ActionBar.TabListener, ViewPager.OnPageChangeListener {
        private final Context mContext;
        private final ViewPager mPager;
        private final ActionBar mBar;
        private final ArrayList<TabInfo> mTabList = new ArrayList<TabInfo>();

        public static class TabInfo {
            private final Class<?> aClass;
            private final Bundle bundle;
            private Fragment fragment;

            public TabInfo(Class<?> clss, Bundle args) {
                aClass = clss;
                bundle = args;
            }
        }

        public TabsAdapter(Activity activity, ViewPager viewPager) {
            super(activity.getFragmentManager());
            mContext = activity;
            mBar = activity.getActionBar();
            mPager = viewPager;

            mPager.setAdapter(this);
            mPager.setOnPageChangeListener(this);
        }

        public void addTab(ActionBar.Tab tab, Class<?> clss, Bundle args) {
            TabInfo tabInfo = new TabInfo(clss, args);
            tab.setTag(tabInfo);
            tab.setTabListener(this);
            mTabList.add(tabInfo);
            mBar.addTab(tab);
            notifyDataSetChanged();
        }

        @Override
        public Fragment getItem(int i) {
            TabInfo info = mTabList.get(i);
            if (info.fragment == null){
                info.fragment = Fragment.instantiate(mContext,info.aClass.getName(),info.bundle);
            }
            return info.fragment;
        }

        @Override
        public int getCount() {
            return mTabList.size();
        }

        @Override
        public void onPageScrolled(int i, float v, int i2) {

        }

        @Override
        public void onPageSelected(int i) {
            mBar.setSelectedNavigationItem(i);
        }

        @Override
        public void onPageScrollStateChanged(int i) {

        }

        /**
         * Called when a tab enters the selected state.
         *
         * @param tab The tab that was selected
         * @param ft  A {@link android.app.FragmentTransaction} for queuing fragment operations to execute
         *            during a tab switch. The previous tab's unselect and this tab's select will be
         *            executed in a single transaction. This FragmentTransaction does not support
         */
        @Override
        public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
            Object tag = tab.getTag();
            for (int i = 0; i < mTabList.size(); i++) {
                if (tag == mTabList.get(i)){
                    mPager.setCurrentItem(i);
                }
            }

            //actionMode 判断
        }

        /**
         * Called when a tab exits the selected state.
         *
         * @param tab The tab that was unselected
         * @param ft  A {@link android.app.FragmentTransaction} for queuing fragment operations to execute
         *            during a tab switch. This tab's unselect and the newly selected tab's select
         *            will be executed in a single transaction. This FragmentTransaction does not
         */
        @Override
        public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {

        }

        /**
         * Called when a tab that is already selected is chosen again by the user.
         * Some applications may use this action to return to the top level of a category.
         *
         * @param tab The tab that was reselected.
         * @param ft  A {@link android.app.FragmentTransaction} for queuing fragment operations to execute
         *            once this method returns. This FragmentTransaction does not support
         */
        @Override
        public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {

        }

    }
}
