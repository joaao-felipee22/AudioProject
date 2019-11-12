package com.example.tmdbaudio.view;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.tmdbaudio.R;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.Picasso;

import java.util.Arrays;

public class LoginActivity extends AppCompatActivity {

    private Button btnfacebookDefault;
    private CallbackManager callbackManager;
    private String fbEmail;
    private String profileUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        btnfacebookDefault = findViewById(R.id.btnLoginFacebook);

        callbackManager = CallbackManager.Factory.create();

        btnfacebookDefault.setOnClickListener(v -> {

            LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("email", "public_profile"));

            LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(LoginResult loginResult) {
                    getFacebookProfile(loginResult);
                }

                @Override
                public void onCancel() {
                    Snackbar.make(btnfacebookDefault, "Cancel ", Snackbar.LENGTH_LONG).show();
                }

                @Override
                public void onError(FacebookException error) {
                    Log.i("ERRO", error.getMessage());
                    Snackbar.make(btnfacebookDefault, "Error \n" + error.getMessage(), Snackbar.LENGTH_LONG).show();
                }
            });
        });

        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        boolean isLoggedIn = accessToken != null && !accessToken.isExpired();

        if (isLoggedIn) {
           getUserProfile();
            Snackbar.make(btnfacebookDefault, "Usuario já logado", Snackbar.LENGTH_LONG).show();
        }
    }


    private void gotoHome(String email, String imagem) {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.putExtra("EMAIL", email);
        intent.putExtra("IMG", imagem);
        startActivity(intent);
        finishActivity(64206);
    }

    private void getFacebookProfile(LoginResult loginResult) {
        GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), (result, response) -> {
            try {

                Log.i("RESAULTS : ", result.toString());
                getUserProfile();

            } catch (Exception e) {

                Log.e("TAG", "Erro ao buscar profile : ", e);
            }
        });

        Bundle parameters = new Bundle();
        parameters.putString("fields", "email");
        request.setParameters(parameters);
        request.executeAsync();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void getUserProfile() {
        ProfileTracker profileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {

                // Agora que temos o token do usuario podemos requisitar os dados
                Profile.fetchProfileForCurrentAccessToken();
                if (currentProfile != null) {
                    String fbUserId = currentProfile.getId();
                    fbEmail = currentProfile.getName();
                    profileUrl = currentProfile.getProfilePictureUri(200, 200).toString();
                    gotoHome(fbEmail, profileUrl);
                }
            }
        };
    }

}
