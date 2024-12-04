package com.example.cineinfo

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.cineinfo.databinding.GenreCardBinding

class GenreAdapter(private val onClickGenreCard: (id: String) -> Unit) : RecyclerView.Adapter<GenreAdapter.GenreViewHolder>() {
    private val genres = mutableListOf<Genre>()

    fun setGenres(genres: List<Genre>) {
        this.genres.clear()
        this.genres.addAll(genres)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GenreViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = GenreCardBinding.inflate(inflater, parent, false)
        return GenreViewHolder(binding, onClickGenreCard)
    }

    override fun onBindViewHolder(holder: GenreViewHolder, position: Int) {
        holder.bind(genres[position])
    }

    override fun getItemCount(): Int = genres.size

    class GenreViewHolder(private val binding: GenreCardBinding, private val onClick: (id: String) -> Unit) : RecyclerView.ViewHolder(binding.root) {

        fun bind(genre: Genre) {
            binding.genreName.text = genre.name

            binding.root.setOnClickListener{
                onClick.invoke(genre.id.toString())
            }

        }

    }
}
