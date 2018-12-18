package com.example.asus.magnumassignment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Firebase firebase;
    boolean myMessage = true;
    private ArrayAdapter<ChatBubble> adapter;
    int i=0;
    private ShimmerFrameLayout shimmer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        android.support.v7.app.ActionBar bar = getSupportActionBar();
        bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#ff9900")));

        shimmer = findViewById(R.id.shimmer);

        Firebase.setAndroidContext(getApplicationContext());
        firebase = new Firebase("https://magnumassignment.firebaseio.com/");

        List<ChatBubble> chatBubbles = new ArrayList<>();
        ListView listView = findViewById(R.id.list_msg);

        //set ListView adapter first
        adapter = new MessageAdapter(MainActivity.this, R.layout.left_chat_bubble, chatBubbles);
        listView.setAdapter(adapter);

        if(!isOnline()){
            try {
                Intent i2 = new Intent(MainActivity.this, Network_Error_Fragment.class);
                startActivity(i2);
            }catch(Exception e) {
                System.out.println(e);
            }
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                if (isOnline()) {
                    shimmer.startShimmer();
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            shimmer.stopShimmer();
                        }
                    }, 500);

                    retrieveData(i);
                    i++;

                } else {
                    try {
                        Intent i2 = new Intent(MainActivity.this, Network_Error_Fragment.class);
                        startActivity(i2);
                    }catch(Exception e){
                        System.out.println(e);
                    }
                }
            }
        });


        LinearLayout linearLayout = findViewById(R.id.linear_layout);
        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isOnline()) {
                    shimmer.startShimmer();
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            shimmer.stopShimmer();
                        }
                    }, 500);

                    retrieveData(i);
                    i++;

                } else {
                    try {
                        Intent i = new Intent(MainActivity.this, Network_Error_Fragment.class);
                        startActivity(i);
                    }catch(Exception e){
                        System.out.println(e);
                    }
                }

            }

        });


    }

    public void retrieveData(int i) {

        if(i>10){
            ChatBubble chatBubble = new ChatBubble("hahahaha", myMessage);
            adapter.add(chatBubble);
            myMessage = !myMessage;
        }
        else {
            firebase.child(String.valueOf(i)).addValueEventListener(new com.firebase.client.ValueEventListener() {
                @Override
                public void onDataChange(com.firebase.client.DataSnapshot dataSnapshot) {
                    String myChildText = dataSnapshot.getValue(String.class);
                    ChatBubble chatBubble = new ChatBubble(myChildText, myMessage);
                    adapter.add(chatBubble);
                    myMessage = !myMessage;
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {

                }

            });
        }
    }

    public boolean isOnline() {
        ConnectivityManager conMgr = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = conMgr.getActiveNetworkInfo();

        if(netInfo == null || !netInfo.isConnected() || !netInfo.isAvailable()){
            return false;
        }
        return true;
    }


}
