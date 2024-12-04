package com.example.cineinfo

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.cineinfo.databinding.MovieCardBinding

class MovieAdapter(private val onFavoriteClick: (Movie) -> Unit) : RecyclerView.Adapter<MovieAdapter.MovieViewHolder>() {
    private val movies = mutableListOf<Movie>()

    fun setMovies(movies: List<Movie>) {
        this.movies.clear()
        this.movies.addAll(movies)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = MovieCardBinding.inflate(inflater, parent, false) // Aplique seu layout de filme aqui
        return MovieViewHolder(binding, onFavoriteClick)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        holder.bind(movies[position])
    }

    override fun getItemCount(): Int = movies.size

    class MovieViewHolder(private val binding: MovieCardBinding, private val onFavoriteClick: (Movie) -> Unit) : RecyclerView.ViewHolder(binding.root) {

        fun bind(movie: Movie) {
            binding.title.text = movie.title
            binding.overview.text = movie.overview
            val imageUrl = "https://image.tmdb.org/t/p/w500${movie.posterPath}"
            // Use Glide ou Picasso para carregar a imagem
            Glide.with(binding.root.context).load(imageUrl).into(binding.posterImage)
            binding.favoriteButton.setOnClickListener {
                onFavoriteClick(movie)
            }
        }
    }
}
