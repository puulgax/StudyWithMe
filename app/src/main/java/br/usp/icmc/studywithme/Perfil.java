package br.usp.icmc.studywithme;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.facebook.Profile;
import com.facebook.login.widget.ProfilePictureView;

public class Perfil extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        Profile profile = Profile.getCurrentProfile().getCurrentProfile();
        ProfilePictureView profileImage = (ProfilePictureView) findViewById(R.id.profilePicture);
        profileImage.setProfileId(profile.getId());

        TextView tv = (TextView) findViewById(R.id.profileName);
        tv.setText(profile.getName());
    }
}
