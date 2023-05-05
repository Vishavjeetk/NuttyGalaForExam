package com.example.nuttygala

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class AdapterChatBot : RecyclerView.Adapter<AdapterChatBot.MyViewHolder>() {
    private val list = ArrayList<ChatModel>()

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var txtChat: TextView
        init {
            txtChat = view.findViewById(R.id.txtChat)
        }
        fun bind(chat: ChatModel) = with(itemView) {
            if(!chat.isBot) {
                txtChat.setBackgroundColor(Color.WHITE)
                txtChat.setTextColor(Color.BLACK)
                txtChat.text = chat.chat
            }else{
                txtChat.setBackgroundColor(Color.CYAN)
                txtChat.setTextColor(Color.BLACK)
                txtChat.text = chat.chat
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) : MyViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.listitem_chat, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount() = list.size

    fun addChatToList(chat: ChatModel) {
        list.add(chat)
        notifyDataSetChanged()
    }

}