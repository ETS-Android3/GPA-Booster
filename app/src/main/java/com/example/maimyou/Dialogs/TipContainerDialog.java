package com.example.maimyou.Dialogs;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import com.example.maimyou.Activities.DashBoardActivity;
import com.example.maimyou.CarouselLayout.CoverFlowAdapter;
import com.example.maimyou.CarouselLayout.Tip;
import com.example.maimyou.R;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import it.moondroid.coverflow.components.ui.containers.FeatureCoverFlow;

public class TipContainerDialog extends Dialog {
    //Vars
    public DashBoardActivity D;
    private FeatureCoverFlow coverFlow;
    private ArrayList<Tip> tips;
    int currentPage = 0;
    Dialog dialog = this;
    Boolean checked = false;

    public void setCheckBox(boolean isChecked) {
        this.checked = isChecked;
    }

    //Views
    ImageButton closeTip;
    TextView pageNumber, Manually;
    Button back, next;
    CardView cardView;
    LinearLayout Container;
    RelativeLayout backGround;
    CheckBox checkBox;

    public TipContainerDialog(DashBoardActivity a) {
        super(a);
        // TODO Auto-generated constructor stub
        this.D = a;
    }

    @SuppressLint({"ClickableViewAccessibility", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.tip_container);

        View v = getWindow().getDecorView();
        v.setBackgroundResource(android.R.color.transparent);
        setCanceledOnTouchOutside(true);//See here is the code

        backGround = findViewById(R.id.backGround);
        Container = findViewById(R.id.Container);
        checkBox = findViewById(R.id.checkBox);
        closeTip = findViewById(R.id.closeTip);
        back = findViewById(R.id.back);
        next = findViewById(R.id.next);
        pageNumber = findViewById(R.id.pageNumber);
        cardView = findViewById(R.id.cardView);
        Manually = findViewById(R.id.Manually);
        coverFlow = findViewById(R.id.coverflow);

        checkBox.setChecked(checked);

        backGround.setOnTouchListener((v1, event) -> {
            dialog.dismiss();
            return false;
        });

        Manually.setOnClickListener(v12 -> {
            dialog.dismiss();
            D.setManually();
        });

        checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                FirebaseDatabase.getInstance().getReference().child("Member").child(D.loadData("Id")).child("DoNotShowUploadTip").setValue("1");
            } else {
                FirebaseDatabase.getInstance().getReference().child("Member").child(D.loadData("Id")).child("DoNotShowUploadTip").removeValue();
            }
        });

        back.setOnClickListener(view -> {
            currentPage--;
            if (currentPage < 0) {
                currentPage = tips.size() - 1;
            }
            pageNumber.setText((currentPage + 1) + "/" + tips.size());
            coverFlow.scrollToPosition(currentPage);
        });
        next.setOnClickListener(view -> {
            currentPage++;
            if (currentPage >= tips.size()) {
                currentPage = 0;
            }
            pageNumber.setText((currentPage + 1) + "/" + tips.size());
            coverFlow.scrollToPosition(currentPage);
        });
        closeTip.setOnClickListener(view -> dialog.dismiss());

        CoverFlowAdapter adapter = new CoverFlowAdapter(getContext(), tips);
        coverFlow.setAdapter(adapter);
        coverFlow.setOnScrollPositionListener(onScrollListener());
        pageNumber.setText((currentPage + 1) + "/" + tips.size());

        coverFlow.post(() -> {
            coverFlow.setCoverHeight((int)(((double)coverFlow.getHeight())/1.3d));
            coverFlow.setCoverWidth((int)(((double)coverFlow.getHeight())/2.6d));
            coverFlow.setMaxRotationAngle(0);
            coverFlow.setMaxScaleFactor(1.2f);
            coverFlow.setRotation(0.1f);
            coverFlow.setSpacing(0.5f);
            coverFlow.setVerticalPaddingTop(0);
            coverFlow.setReflectionOpacity(0);
            coverFlow.setAdapter(adapter);
            coverFlow.scrollToPosition(getInt(D.loadData("Tip")));
        });

//        coverFlow.setScalingThreshold();
//        coverFlow.setRotationTreshold();
    }

//    public int dpToPx(int dip) {
//        Resources r = D.getResources();
//        float px = TypedValue.applyDimension(
//                TypedValue.COMPLEX_UNIT_DIP,
//                dip,
//                r.getDisplayMetrics()
//        );
//        return (int) px;
//    }

    public int getInt(String str){
        try{
            return Integer.parseInt(str);
        }catch (Exception e){
            e.printStackTrace();
            return 0;
        }
    }
    @SuppressLint("SetTextI18n")
    private FeatureCoverFlow.OnScrollPositionListener onScrollListener() {
        return new FeatureCoverFlow.OnScrollPositionListener() {
            @Override
            public void onScrolledToPosition(int position) {
                Log.v("MainActiivty", "position: " + position);
                D.saveData(Integer.toString(position),"Tip");
                currentPage = position;
                pageNumber.setText((currentPage + 1) + "/" + tips.size());
            }

            @Override
            public void onScrolling() {
                Log.i("MainActivity", "scrolling");
                pageNumber.setText("-/" + tips.size());
            }
        };
    }

    public void setTips(ArrayList<Tip> tips) {
        this.tips = tips;
//        tips.add(new Tip(R.mipmap.ic_launcher, "Assassin Creed 3"));
//        tips.add(new Tip(R.mipmap.ic_launcher, "Avatar 3D"));
//        tips.add(new Tip(R.mipmap.ic_launcher, "Call Of Duty Black Ops 3"));
//        tips.add(new Tip(R.mipmap.ic_launcher, "DotA 2"));
//        tips.add(new Tip(R.mipmap.ic_launcher, "Halo 5"));
//        tips.add(new Tip(R.mipmap.ic_launcher, "Left 4 Dead 2"));
//        tips.add(new Tip(R.mipmap.ic_launcher, "StarCraft"));
//        tips.add(new Tip(R.mipmap.ic_launcher, "The Witcher 3"));
//        tips.add(new Tip(R.mipmap.ic_launcher, "Tom raider 3"));
//        tips.add(new Tip(R.mipmap.ic_launcher, "Need for Speed Most Wanted"));
    }
}