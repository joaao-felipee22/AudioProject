package com.example.tmdbaudio.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tmdbaudio.R;
import com.example.tmdbaudio.adapter.RecyclerViewAdapater;
import com.example.tmdbaudio.model.Album;
import com.example.tmdbaudio.viewmodel.AlbumViewModel;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static com.example.tmdbaudio.view.LoginActivity.GOOGLE_ACCOUNT;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private List<Album> albuns = new ArrayList<>();
    private AlbumViewModel viewModel;
    private ProgressBar progressBar;
    private RecyclerViewAdapater adapter;
    private SearchView searchView;
    private String bandName = "Aerosmith";
    private TextView nomeUsuario;
    private TextView emailUsuario;
    private GoogleSignInClient googleSignInClient;
    private Button sair;
    private ImageView imgUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();

        //recuperando login
        GoogleSignInOptions gso = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()//request email id
                .build();

        googleSignInClient= GoogleSignIn.getClient(this, gso);
        setDataOnView();

        sair.setOnClickListener(v -> googleSignInClient.signOut().addOnCompleteListener(task -> {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }));

        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerView.setAdapter(adapter);

        viewModel.getAlbuns(bandName);

        viewModel.getAlbumLiveData().observe(this, (List<Album> albuns) -> {
            if (albuns != null && !albuns.isEmpty()){
                adapter.setUpdate(albuns);
            }else{
                Snackbar.make(searchView,"Album não encontrado", Snackbar.LENGTH_LONG);
                adapter.setUpdate(this.albuns);
            }

        });

        viewModel.isLoading().observe(this, (Boolean loading) -> {
            if (loading) {
                progressBar.setVisibility(View.VISIBLE);
            } else {
                progressBar.setVisibility(View.GONE);
            }
        });

        viewModel.getErrorAlbum().observe(this, error -> {
            Toast.makeText(this, error, Toast.LENGTH_LONG);
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String text) {
                bandName = text;
                adapter.clear();
                viewModel.getAlbuns(bandName);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String text) {
                if (text.length() > 3){
                    bandName = text;
                    adapter.clear();
                    viewModel.getAlbuns(bandName);
                }
                return false;
            }
        });
    }

    private void setDataOnView() {
        GoogleSignInAccount googleSignInAccount = getIntent().getParcelableExtra(GOOGLE_ACCOUNT);
        Picasso.get().load(googleSignInAccount.getPhotoUrl()).centerInside().fit().into(imgUsuario);
        nomeUsuario.setText(googleSignInAccount.getDisplayName());
        emailUsuario.setText(googleSignInAccount.getEmail());
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recyclerView);
        progressBar = findViewById(R.id.progress_bar);
        nomeUsuario = findViewById(R.id.txt_nome);
        imgUsuario = findViewById(R.id.img_usuario);
        emailUsuario = findViewById(R.id.txt_email);
        sair = findViewById (R.id.btn_sair);
        searchView = findViewById(R.id.searchView);
        adapter = new RecyclerViewAdapater(albuns);
        viewModel = ViewModelProviders.of(this).get(AlbumViewModel.class);
        recyclerView.setAdapter(adapter);
    }
}
