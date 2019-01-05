package com.xuexiang.xuidemo.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.LinearLayout;

import com.xuexiang.xpage.base.XPageActivity;
import com.xuexiang.xui.XUI;
import com.xuexiang.xui.utils.ResUtils;
import com.xuexiang.xui.utils.ThemeUtils;
import com.xuexiang.xui.utils.ViewUtils;
import com.xuexiang.xui.widget.dialog.DialogLoader;
import com.xuexiang.xuidemo.R;
import com.xuexiang.xuidemo.adapter.menu.DrawerAdapter;
import com.xuexiang.xuidemo.adapter.menu.DrawerItem;
import com.xuexiang.xuidemo.adapter.menu.SimpleItem;
import com.xuexiang.xuidemo.adapter.menu.SpaceItem;
import com.xuexiang.xuidemo.fragment.ComponentsFragment;
import com.xuexiang.xuidemo.fragment.ExpandsFragment;
import com.xuexiang.xuidemo.fragment.UtilitysFragment;
import com.xuexiang.xutil.XUtil;
import com.xuexiang.xutil.tip.ToastUtils;
import com.yarolegovich.slidingrootnav.SlidingRootNav;
import com.yarolegovich.slidingrootnav.SlidingRootNavBuilder;
import com.yarolegovich.slidingrootnav.callback.DragStateListener;

import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * 项目壳工程
 *
 * @author xuexiang
 * @since 2018/11/13 下午5:20
 */
public class MainActivity extends XPageActivity implements DrawerAdapter.OnItemSelectedListener {
    private static final int POS_COMPONENTS = 0;
    private static final int POS_UTILITYS = 1;
    private static final int POS_EXPANDS = 2;
    private static final int POS_ABOUT = 3;
    private static final int POS_LOGOUT = 5;

    Unbinder unbinder;

    @Override
    protected void attachBaseContext(Context newBase) {
        //注入字体
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @BindView(R.id.tabs)
    TabLayout mTabLayout;

    private SlidingRootNav mSlidingRootNav;
    private LinearLayout mLLMenu;
    private String[] mMenuTitles;
    private Drawable[] mMenuIcons;
    private DrawerAdapter mAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        XUI.initTheme(this);
        super.onCreate(savedInstanceState);
        unbinder = ButterKnife.bind(this);

        initSlidingMenu(savedInstanceState);

        initViews();
    }

    private void initViews() {
        openPage(ComponentsFragment.class);

        initTab();
    }

    /**
     * 初始化Tab
     */
    private void initTab() {
        TabLayout.Tab component = mTabLayout.newTab();
        component.setText("组件");
        component.setIcon(R.drawable.selector_icon_tabbar_component);
        mTabLayout.addTab(component);

        TabLayout.Tab util = mTabLayout.newTab();
        util.setText("工具");
        util.setIcon(R.drawable.selector_icon_tabbar_util);
        mTabLayout.addTab(util);

        TabLayout.Tab expand = mTabLayout.newTab();
        expand.setText("拓展");
        expand.setIcon(R.drawable.selector_icon_tabbar_expand);
        mTabLayout.addTab(expand);

        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mAdapter.setSelected(tab.getPosition());
                switch (tab.getPosition()) {
                    case POS_COMPONENTS:
                        openPage(ComponentsFragment.class);
                        break;
                    case 1:
                        openPage(UtilitysFragment.class);
                        break;
                    case 2:
                        openPage(ExpandsFragment.class);
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    public void switchTab(final boolean isShow) {
        Animation animation;
        if (isShow) {
            animation = new AlphaAnimation(0.2F, 1.0F);
        } else {
            animation = new AlphaAnimation(1.0F, 0.2F);
        }
        animation.setDuration(300);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (isShow) {
                    mTabLayout.setVisibility(View.VISIBLE);
                } else {
                    mTabLayout.setVisibility(View.GONE);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        mTabLayout.startAnimation(animation);
    }

    public void openMenu() {
        if (mSlidingRootNav != null) {
            mSlidingRootNav.openMenu();
        }
    }

    private void initSlidingMenu(Bundle savedInstanceState) {
        mMenuTitles = loadMenuTitles();
        mMenuIcons = loadMenuIcons();

        mSlidingRootNav = new SlidingRootNavBuilder(this)
                .withMenuOpened(false)
                .withContentClickableWhenMenuOpened(false)
                .withSavedState(savedInstanceState)
                .withMenuLayout(R.layout.menu_left_drawer)
                .inject();

        mLLMenu = mSlidingRootNav.getLayout().findViewById(R.id.ll_menu);
        mSlidingRootNav.getLayout().findViewById(R.id.iv_qrcode).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToastUtils.toast("扫码二维码加关注哦～～");
            }
        });

        mAdapter = new DrawerAdapter(Arrays.asList(
                createItemFor(POS_COMPONENTS).setChecked(true),
                createItemFor(POS_UTILITYS),
                createItemFor(POS_EXPANDS),
                createItemFor(POS_ABOUT),
                new SpaceItem(48),
                createItemFor(POS_LOGOUT)));
        mAdapter.setListener(this);

        RecyclerView list = findViewById(R.id.list);
        list.setNestedScrollingEnabled(false);
        list.setLayoutManager(new LinearLayoutManager(this));
        list.setAdapter(mAdapter);

        mAdapter.setSelected(POS_COMPONENTS);
        mSlidingRootNav.setMenuLocked(true);
        mSlidingRootNav.getLayout().addDragStateListener(new DragStateListener() {
            @Override
            public void onDragStart() {
                if (mSlidingRootNav.isMenuOpened()) {
                    ViewUtils.fadeOut(mLLMenu, 300, null);
                } else {
                    ViewUtils.fadeIn(mLLMenu, 300, null);
                }
            }
            @Override
            public void onDragEnd(boolean isMenuOpened) {

            }
        });
        mLLMenu.setVisibility(View.GONE);
    }

    @Override
    public void onItemSelected(int position) {
        switch(position) {
            case POS_COMPONENTS:
            case POS_UTILITYS:
            case POS_EXPANDS:
                if (mTabLayout != null) {
                    TabLayout.Tab tab = mTabLayout.getTabAt(position);
                    if (tab != null) {
                        tab.select();
                    }
                }
                mSlidingRootNav.closeMenu();
                break;
            case POS_ABOUT:
                break;
            case POS_LOGOUT:
                DialogLoader.getInstance().showConfirmDialog(
                        this,
                        getString(R.string.lab_logout_confirm),
                        getString(R.string.lab_yes),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                XUtil.get().exitApp();
                            }
                        },
                        getString(R.string.lab_no),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }
                );
                break;
            default:
                break;
        }
    }

    private DrawerItem createItemFor(int position) {
        return new SimpleItem(mMenuIcons[position], mMenuTitles[position])
                .withIconTint(ResUtils.getColor(R.color.gray_icon))
                .withTextTint(ThemeUtils.resolveColor(this, R.attr.xui_config_color_content_text))
                .withSelectedIconTint(ThemeUtils.resolveColor(this, R.attr.colorAccent))
                .withSelectedTextTint(ThemeUtils.resolveColor(this, R.attr.colorAccent));
    }

    private String[] loadMenuTitles() {
        return getResources().getStringArray(R.array.menu_titles);
    }

    private Drawable[] loadMenuIcons() {
        TypedArray ta = getResources().obtainTypedArray(R.array.menu_icons);
        Drawable[] icons = new Drawable[ta.length()];
        for (int i = 0; i < ta.length(); i++) {
            int id = ta.getResourceId(i, 0);
            if (id != 0) {
                icons[i] = ContextCompat.getDrawable(this, id);
            }
        }
        ta.recycle();
        return icons;
    }

    @Override
    protected void onRelease() {
        unbinder.unbind();
        super.onRelease();
    }


}
