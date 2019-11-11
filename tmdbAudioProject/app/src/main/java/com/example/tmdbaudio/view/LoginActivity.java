package com.example.tmdbaudio.view;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.tmdbaudio.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

public class LoginActivity extends AppCompatActivity {
    private SignInButton googleSignInButton;
    private GoogleSignInClient googleSignInClient;
    public static final String GOOGLE_ACCOUNT = "google_account";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        onStart();

        googleSignInButton = findViewById(R.id.sign_in_button);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, gso);
        googleSignInButton.setOnClickListener(view -> {
            Intent signInIntent = googleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, 101);
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        GoogleSignInAccount alreadyloggedAccount = GoogleSignIn.getLastSignedInAccount(this);
        if (alreadyloggedAccount != null) {
            Toast.makeText(this, "Você já está logado", Toast.LENGTH_SHORT).show();
            concluirLogin(alreadyloggedAccount);
        } else {
            Toast.makeText(this, "Entre em alguma conta", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case 101:
                    try {
                        // A tarefa retornada desta chamada é sempre concluída, sem necessidade de anexar
                        // um ouvinte.
                        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                        GoogleSignInAccount conta = task.getResult(ApiException.class);
                        concluirLogin(conta);
                    } catch (ApiException e) {

                        // O código de status ApiException indica o motivo detalhado da falha.
                        Log.i("LOG", "Error: " + e.getMessage());
                        Toast.makeText(getApplicationContext(), "Deu merda", Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        }
    }

    private void concluirLogin(GoogleSignInAccount googleSignInAccount) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(GOOGLE_ACCOUNT, googleSignInAccount);
        startActivity(intent);
        finish();
    }
}
