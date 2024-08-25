package com.example.planandmeet;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import android.annotation.SuppressLint;
import android.view.Gravity;
import android.widget.Toast;
import java.util.Calendar;
import mehdi.sakout.aboutpage.AboutPage;
import mehdi.sakout.aboutpage.Element;


public class AboutUs extends AppCompatActivity {

    DrawerLayout drawerLayout;
    TextView toolBarText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);
        toolBarText =findViewById(R.id.toolbarText);
        toolBarText.setText(R.string.aboutUsTitle);

        drawerLayout = findViewById(R.id.drawer_layout);

        Element adsElement = new Element();
        View aboutPage = new AboutPage(this)
                .isRTL(false)
                .setImage(R.drawable.logo)
                .setDescription(" Plan&Meet is an android application, which is used for online and offline scheduling of meetings with the team members, clients, colleagues, partners, and friends.")
                .addItem(new Element().setTitle("Version 1.0"))
                .addGroup("CONNECT WITH US!")
                .addEmail("hamzamalik12235@gmail.com")
                .addYoutube("")
                .addPlayStore("")
                .addInstagram("")
                .addItem(createCopyright())
                .create();

        setContentView(aboutPage);
    }


    private Element createCopyright()
    {
        Element copyright = new Element();
        @SuppressLint("DefaultLocale") final String copyrightString = String.format("Copyright %d by Plan&Meet", Calendar.getInstance().get(Calendar.YEAR));
        copyright.setTitle(copyrightString);
        copyright.setGravity(Gravity.CENTER);
        copyright.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(AboutUs.this,copyrightString,Toast.LENGTH_SHORT).show();
            }
        });
        return copyright;

    }

    public void ClickMenu(View view){
        MainActivity.openDrawer(drawerLayout);
    }

    public void ClickLogo(View view){
        //close drawer
        MainActivity.closeDrawer(drawerLayout);
    }

    public void ClickHome(View view){
        //redirect activity
        MainActivity.redirectActivity(this, MainActivity.class);
    }

    public void ClickUpcomingMeeting(View view){
        //redirect activity
        MainActivity.redirectActivity(this, UpcomingMeeting.class);
    }

    public void ClickProfile(View view) {
        //redirect activity
        MainActivity.redirectActivity(this, Profile.class);

    }

    public  void ClickShare(View view)
    {
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_TEXT, "https://www.youtube.com/watch?v=WvJBXWiSkTU&t=1085s");
        shareIntent.setType("text/plain");
        startActivity(Intent.createChooser(shareIntent, "ShareVia"));
    }

    public void ClickAboutUs(View view){
        //recreate activity
        recreate();
    }

    public void ClickLogout(View view){
        //close app
        MainActivity.logout(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //close drawer
        MainActivity.closeDrawer(drawerLayout);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(AboutUs.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}