package com.sankarmanoj.androidfarm

import android.util.Log
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject
import java.io.BufferedOutputStream
import java.io.InputStreamReader
import java.net.Socket

class ToServer: Runnable {
    private val SERVER_PORT = 3213
    private val SERVER_IP = "sankar-manoj.com"
    lateinit var socket: Socket
    lateinit var outputStream: BufferedOutputStream
    lateinit var inputStream: InputStreamReader
    companion object {
        lateinit var singleton : ToServer
        fun getToServer() :ToServer
        {
            if (!::singleton.isInitialized)
            {
                singleton = ToServer()
                Thread(singleton).start()
            }
            return singleton
        }
    }
    public interface onServerMessageListener
    {
        public  fun onServerMessage(data: JSONObject);
    }
    val messageListeners =  mutableListOf<onServerMessageListener>()
    public fun addOnServerMessageListener(listener: onServerMessageListener)
    {
        messageListeners.add(listener)
    }
    override fun run() {
        socket = Socket(SERVER_IP, SERVER_PORT)
        outputStream = BufferedOutputStream(socket.getOutputStream())
        inputStream = InputStreamReader(socket.getInputStream())
        while (true) {
            val input_data_str = CharArray(1000)
            val read_Size = inputStream.read(input_data_str)

            val string_input_data = String(input_data_str).subSequence(0, read_Size).toString()
            Log.i("FromServerSize", read_Size.toString())
            Log.i("FromServer", string_input_data + string_input_data.length.toString())

            val input_data = JSONObject(string_input_data)
            for (listener in messageListeners)
            {
                listener.onServerMessage(input_data)
            }

        }

    }
    fun write(toWrite:String)
    {
     outputStream.write(toWrite.toByteArray())
        outputStream.flush()
    }
}