package com.example.android.networkconnect.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.android.networkconnect.R
import com.example.android.networkconnect.model.CharacterModel
import com.facebook.drawee.view.SimpleDraweeView
import kotlinx.android.synthetic.main.list_character.view.*

class CharactersListAdapter(private var characters: MutableList<CharacterModel>) :
    RecyclerView.Adapter<CharactersListAdapter.CharactersViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = CharactersViewHolder(
        LayoutInflater.from(parent.context).inflate(
            R.layout.list_character,
            parent,
            false
        )
    )

    override fun onBindViewHolder(holder: CharactersViewHolder, position: Int) {
        val listItem = characters[position]
        with(holder) {
            name.text = listItem.name
            image.setImageURI(listItem.image)
            species.text = listItem.species
            status.text = listItem.status
        }
    }

    override fun getItemCount(): Int = characters.size

    class CharactersViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.name
        val image: SimpleDraweeView = view.image
        val species: TextView = view.species
        val status: TextView = view.status
    }
}