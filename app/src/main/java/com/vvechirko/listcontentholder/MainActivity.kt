package com.vvechirko.listcontentholder

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val emptyView = findViewById<View>(R.id.emptyView)
        val rv = findViewById<RecyclerView>(R.id.recyclerView)

        val adapter = Adapter(this)
        rv.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        rv.adapter = adapter

        // simulate data delay
        adapter.loading = true
        rv.postDelayed({
//            adapter.data = listOf()
//            emptyView.isVisible = adapter.data.isEmpty()
        }, 4000)
    }
}

class Adapter(context: Context) : RecyclerView.Adapter<Holder>() {

    val placeHolderDrawable = PlaceHolderDrawable(context, R.layout.item_person)

    var data: List<String> = emptyList()
        set(value) {
            field = value
            loading = false
            notifyDataSetChanged()
        }

    var loading: Boolean = false
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_person, parent, false)
        )
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        if (loading) {
            holder.itemView.background = placeHolderDrawable.mutate().constantState!!.newDrawable()
        } else {
            holder.itemView.background = null
        }
    }

    override fun getItemCount(): Int {
        return if (loading) 5 else data.size
    }
}

class Holder(view: View) : RecyclerView.ViewHolder(view) {

}