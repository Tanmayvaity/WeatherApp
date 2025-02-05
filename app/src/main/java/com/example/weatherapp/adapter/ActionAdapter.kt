package com.example.weatherapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.weatherapp.R
import com.example.weatherapp.models.Action

class ActionAdapter(
    val ctx:Context,
    val actions:List<Action>
) : ArrayAdapter<Action>(ctx,0,actions) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val action = getItem(position)

        var view = convertView

        if (view == null){
            view = LayoutInflater.from(ctx).inflate(R.layout.action_list_item,parent,false)
        }

        val actionImage = view!!.findViewById<ImageView>(R.id.iv_action)
        val tvTopic = view.findViewById<TextView>(R.id.tv_action_topic)

        tvTopic.text = action!!.topic
        actionImage.setImageResource(action.image)


        return view
    }
}