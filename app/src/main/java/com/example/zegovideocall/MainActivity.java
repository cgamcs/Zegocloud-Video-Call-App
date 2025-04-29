package com.example.zegovideocall;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.permissionx.guolindev.PermissionX;
import com.permissionx.guolindev.callback.ExplainReasonCallback;
import com.permissionx.guolindev.callback.RequestCallback;
import com.permissionx.guolindev.request.ExplainScope;
import com.zegocloud.uikit.plugin.invitation.ZegoInvitationType;
import com.zegocloud.uikit.prebuilt.call.invite.ZegoUIKitPrebuiltCallInvitationConfig;
import com.zegocloud.uikit.prebuilt.call.invite.ZegoUIKitPrebuiltCallInvitationService;
import com.zegocloud.uikit.prebuilt.call.invite.widget.ZegoSendCallInvitationButton;
import com.zegocloud.uikit.service.defines.ZegoUIKitUser;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseApp.initializeApp(MainActivity.this);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        TextView userNameTV = findViewById(R.id.userNameTV);
        RecyclerView recyclerView = findViewById(R.id.recycler);

        setSupportActionBar(toolbar);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
        } else {
            long appID = getResources().getInteger(R.integer.app_id);
            String appSign = getString(R.string.app_sign);

            FirebaseDatabase.getInstance().getReference("usersList").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    ArrayList<User> arrayList = new ArrayList<>();

                    snapshot.getChildren().forEach(new Consumer<DataSnapshot>() {
                        @Override
                        public void accept(DataSnapshot dataSnapshot) {
                            User user = dataSnapshot.getValue(User.class);

                            if (Objects.requireNonNull(user).getUserID().equals(currentUser.getUid())) {
                                userNameTV.setText(MessageFormat.format("Logged in as: {0}", user.getUserName()));

                                ZegoUIKitPrebuiltCallInvitationConfig callInvitationConfig= new ZegoUIKitPrebuiltCallInvitationConfig();
                                ZegoUIKitPrebuiltCallInvitationService.init(getApplication(), appID, appSign, user.getUserID(), user.getUserName(), callInvitationConfig);
                            } else {
                                arrayList.add(user);
                            }
                        }
                    });

                    UsersAdapter adapter = new UsersAdapter(MainActivity.this, arrayList);
                    recyclerView.setAdapter(adapter);

                    adapter.setOnItemClickListener(new UsersAdapter.OnItemClickListener() {
                        @Override
                        public void onClick(User user) {
                            BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(MainActivity.this);
                            View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.user_bottom_sheet, null);
                            bottomSheetDialog.setContentView(view);
                            bottomSheetDialog.show();

                            TextView userNameTV = view.findViewById(R.id.userNameTV);
                            TextView userIdTV = view.findViewById(R.id.userIdTV);

                            ZegoSendCallInvitationButton voiceCallBtn = view.findViewById(R.id.voiceCallBtn);
                            ZegoSendCallInvitationButton videoCallBtn = view.findViewById(R.id.videoCallBtn);

                            voiceCallBtn.setIsVideoCall(false);
                            voiceCallBtn.setType(ZegoInvitationType.VOICE_CALL);
                            voiceCallBtn.setResourceID("zego_uikit_call");
                            voiceCallBtn.setInvitees(Collections.singletonList(new ZegoUIKitUser(user.getUserID(), user.getUserName())));

                            videoCallBtn.setIsVideoCall(true);
                            videoCallBtn.setType(ZegoInvitationType.VIDEO_CALL);
                            videoCallBtn.setResourceID("zego_uikit_call");
                            videoCallBtn.setInvitees(Collections.singletonList(new ZegoUIKitUser(user.getUserID(), user.getUserName())));

                            userNameTV.setText(MessageFormat.format("User Name: {0}", user.getUserName()));
                            userIdTV.setText(MessageFormat.format("User ID: {0}", user.getUserID()));
                        }
                    });
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            PermissionX.init(MainActivity.this).permissions(Manifest.permission.SYSTEM_ALERT_WINDOW)
                    .onExplainRequestReason(new ExplainReasonCallback() {
                        @Override
                        public void onExplainReason(@NonNull ExplainScope scope, @NonNull List<String> deniedList) {
                            String message = "We need your consent for the following permissions in order to use the offline call function properly";
                            scope.showRequestReasonDialog(deniedList, message, "Allow", "Deny");
                        }
                    }).request(new RequestCallback() {
                        @Override
                        public void onResult(boolean allGranted, @NonNull List<String> grantedList,
                                             @NonNull List<String> deniedList) {
                        }
                    });
        }
    }
}