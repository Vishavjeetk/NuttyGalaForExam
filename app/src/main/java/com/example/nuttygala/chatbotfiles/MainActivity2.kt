package com.example.nuttygala.chatbotfiles

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.nuttygala.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class MainActivity2 : AppCompatActivity() {
    private val adapterChatBot = AdapterChatBot()
    private lateinit var rvChatList: RecyclerView
    private lateinit var etChat: EditText
    private lateinit var btnSend: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)
        rvChatList = findViewById(R.id.rvChatList)
        etChat = findViewById(R.id.etChat)
        btnSend = findViewById(R.id.btnSend)

        val retrofit = Retrofit.Builder()
            .baseUrl("https://9c24-103-217-116-45.ngrok-free.app/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(APIService::class.java)

        rvChatList.layoutManager = LinearLayoutManager(this)
        rvChatList.adapter = adapterChatBot

        btnSend.setOnClickListener {
            if(etChat.text.isNullOrEmpty()){
                Toast.makeText(this@MainActivity2, "Please enter a text", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            adapterChatBot.addChatToList(ChatModel(etChat.text.toString()))
            apiService.chatWithTheBit(etChat.text.toString()).enqueue(callBack)
            etChat.text.clear()
        }
    }

    private val callBack = object  : Callback<ChatResponse>{
        override fun onResponse(call: Call<ChatResponse>, response: Response<ChatResponse>) {
            if(response.isSuccessful &&  response.body()!= null){
                adapterChatBot.addChatToList(ChatModel(response.body()!!.chatBotReply, true))
            }else{
                Toast.makeText(this@MainActivity2, "Something went wrong in OnResponse", Toast.LENGTH_LONG).show()
            }
        }

        override fun onFailure(call: Call<ChatResponse>, t: Throwable) {
            Toast.makeText(this@MainActivity2, "Something went wrong in OnFailure", Toast.LENGTH_LONG).show()
        }

    }
}