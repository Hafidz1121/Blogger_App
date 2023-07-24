package com.example.blogapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.blogapp.adapters.ViewPagerAdapter;

public class onBoardActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private Button btnLeft, btnRight;
    private ViewPagerAdapter adapter;
    private LinearLayout dotsLayout;
    private TextView[] dots;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_on_board);

        init();
    }

    private void init() {
        viewPager = findViewById(R.id.view_page);
        btnLeft = findViewById(R.id.btn_skip);
        btnRight = findViewById(R.id.btn_next);
        dotsLayout = findViewById(R.id.dots_layout);
        adapter = new ViewPagerAdapter(this);

        addDots(0);
        viewPager.addOnPageChangeListener(listener);
        viewPager.setAdapter(adapter);

        btnRight.setOnClickListener(v-> {
            if (btnRight.getText().toString().equals("Next")) {
                viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
            } else {
                startActivity(new Intent(onBoardActivity.this, authActivity.class));
                finish();
            }
        });

        btnLeft.setOnClickListener(v-> {
            viewPager.setCurrentItem(viewPager.getCurrentItem() + 2);
        });
    }

    // create dots from html code
    private void addDots(int position) {
        dotsLayout.removeAllViews();
        dots = new TextView[3];

        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(this);

            dots[i].setText(Html.fromHtml("&#8226"));
            dots[i].setTextSize(35);
            dots[i].setTextColor(getResources().getColor(R.color.colorLightGrey));
            dotsLayout.addView(dots[i]);
        }

        // change selected dot color
        if (dots.length > 0) {
            dots[position].setTextColor(getResources().getColor(R.color.colorGrey));
        }
    }

    private ViewPager.OnPageChangeListener listener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            addDots(position);

            // change text button next to finish if end of dot
            // and hide skip button if not in page 1

            if (position == 0) {
                btnLeft.setVisibility(View.VISIBLE);
                btnLeft.setEnabled(true);
                btnRight.setText("Next");
            } else if (position == 1) {
                btnLeft.setVisibility(View.GONE);
                btnLeft.setEnabled(false);
                btnRight.setText("Next");
            } else {
                btnLeft.setVisibility(View.GONE);
                btnLeft.setEnabled(false);
                btnRight.setText("Finish");
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };
}