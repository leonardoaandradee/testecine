package com.example.cineinfo

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cineinfo.databinding.ActivityMainBinding
import com.google.gson.annotations.SerializedName
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import kotlin.math.log
import retrofit2.http.Query
import android.view.MenuItem
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import android.content.Intent
import com.google.android.material.floatingactionbutton.FloatingActionButton



// Definição dos dados de gênero e filme
data class Genre(
    val id: Int,
    val name: String
)

data class MovieResponse(
    val results: List<Movie> // Lista de filmes
)

data class Movie(
    val id: Int,
    @SerializedName("poster_path") val posterPath: String,
    val title: String,
    val overview: String,
    val genres: List<Genre>
)

interface MovieService {
    @GET("movie/{movie_id}")
    fun getMovie(
        @Path("movie_id") movieId: Int,
        @Header("Authorization") auth: String = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiI4ZmYyZGNjZjBkNmM1NDA1NzYxMzk2YTQyYjU3MWI0MCIsIm5iZiI6MTczMjA3MTAwMi40NDAzMDgzLCJzdWIiOiI2NzNiZTk1Zjc3ZWI1OWYyZjAxYTY1YTIiLCJzY29wZXMiOlsiYXBpX3JlYWQiXSwidmVyc2lvbiI6MX0.gI-Nb9r-Uo7L9EGhWwNh8XcNTBQxC5Lfy1yqV6O7TzM"
    ): Call<Movie>

    @GET("movie/popular")
    fun getPopularMovies(
        @Header("Authorization") auth: String = " Bearer eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiI4ZmYyZGNjZjBkNmM1NDA1NzYxMzk2YTQyYjU3MWI0MCIsIm5iZiI6MTczMjA3MTAxNy4zMjgxNTI0LCJzdWIiOiI2NzNiZTk1Zjc3ZWI1OWYyZjAxYTY1YTIiLCJzY29wZXMiOlsiYXBpX3JlYWQiXSwidmVyc2lvbiI6MX0.pq7-utQ6zYd2HG2gCNrU8ghXRGlejZ6TYRKzjEu_cX4"
    ): Call<MovieResponse>

    @GET("search/movie")
    fun searchMovies(
        @Query("query") query: String,
        @Header("Authorization") auth: String = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiI4ZmYyZGNjZjBkNmM1NDA1NzYxMzk2YTQyYjU3MWI0MCIsIm5iZiI6MTczMjA3MTAxNy4zMjgxNTI0LCJzdWIiOiI2NzNiZTk1Zjc3ZWI1OWYyZjAxYTY1YTIiLCJzY29wZXMiOlsiYXBpX3JlYWQiXSwidmVyc2lvbiI6MX0.pq7-utQ6zYd2HG2gCNrU8ghXRGlejZ6TYRKzjEu_cX4"
    ): Call<MovieResponse>
}

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var binding: ActivityMainBinding
    private lateinit var adapterMovie: MovieAdapter // Adaptador para a lista de filmes
    private var moviesList: List<Movie> = listOf() // Armazenar a lista completa de filmes para pesquisa
    private lateinit var drawerLayout: DrawerLayout // Adição do DrawerLayout
    private val favoriteMovies = mutableListOf<Movie>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        drawerLayout = binding.drawerLayout
        var navigationView = findViewById<NavigationView>(R.id.navigation_view)
        navigationView.setNavigationItemSelectedListener(this) // Adicionando o listener para o NavigationView


        binding.imageButton3.setOnClickListener {
            Log.d("ImageButton", "Botão clicado!")
            drawerLayout.openDrawer(GravityCompat.START) // Abrir o menu lateral
        }

        // Configurar o RecyclerView
        adapterMovie = MovieAdapter { movie ->
            favoriteMovies.add(movie)
            Log.d("Favorites", "Filme adicionado aos favoritos: ${movie.title}")
        }
        binding.genreList.adapter = adapterMovie
        binding.genreList.layoutManager = LinearLayoutManager(this)

        // Criar o Retrofit e a instância do MovieService
        val movieService = Retrofit.Builder()
            .baseUrl("https://api.themoviedb.org/3/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(MovieService::class.java)

        // Adicionar o TextWatcher para a barra de pesquisa
        binding.searchBar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(charSequence: CharSequence?, start: Int, before: Int, count: Int) {
                val query = charSequence.toString()
                if (query.isNotEmpty()) {
                    searchMovies(query, movieService) // Buscar filmes com base na pesquisa
                } else {
                    adapterMovie.setMovies(moviesList) // Exibir todos os filmes quando a pesquisa estiver vazia
                }
            }

            override fun afterTextChanged(editable: Editable?) {}
        })

        // Fazer a requisição para obter os filmes populares
        movieService.getPopularMovies().enqueue(object : Callback<MovieResponse> {
            override fun onResponse(call: Call<MovieResponse>, response: Response<MovieResponse>) {
                if (response.isSuccessful) {
                    val movies = response.body()?.results
                    movies?.let {
                        moviesList = it // Armazenar a lista completa de filmes
                        adapterMovie.setMovies(it) // Passar os filmes para o adaptador
                    }
                } else {
                    Log.e("MovieAPI", "Erro na resposta: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<MovieResponse>, t: Throwable) {
                Log.e("MovieAPI", "Falha na requisição: ${t.message}")
            }
        })

        val fabFavorites: FloatingActionButton = findViewById(R.id.fabFavorites)
        fabFavorites.setOnClickListener {
            showFavoriteMovies()
        }
    }

    // Lidar com seleção de itens do menu
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_logout -> {
                Log.d("Menu", "Sair da Conta selecionado")

                // Redirecionar para a tela de login
                val intent = Intent(this@MainActivity, LoginActivity::class.java)
                startActivity(intent)
                finish() // Finaliza a MainActivity para evitar retorno ao pressionar "Voltar"
                Log.d("Menu", "Navegação para LoginActivity realizada.")
            }
        }

        // Fechar o menu lateral após a seleção
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }


    override fun onBackPressed() {
        // Fecha o menu se estiver aberto
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    // Função para realizar a busca de filmes com base na pesquisa
    private fun searchMovies(query: String, movieService: MovieService) {
        movieService.searchMovies(query).enqueue(object : Callback<MovieResponse> {
            override fun onResponse(call: Call<MovieResponse>, response: Response<MovieResponse>) {
                if (response.isSuccessful) {
                    val movies = response.body()?.results
                    movies?.let {
                        adapterMovie.setMovies(it) // Atualizar a lista de filmes no adaptador
                    }
                } else {
                    Log.e("MovieAPI", "Erro na resposta: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<MovieResponse>, t: Throwable) {
                Log.e("MovieAPI", "Falha na requisição de busca: ${t.message}")
            }
        })
    }

    private fun showFavoriteMovies() {
        adapterMovie.setMovies(favoriteMovies)
    }
}
