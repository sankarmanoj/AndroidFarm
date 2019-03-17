package com.sankarmanoj.androidfarm

import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject
import java.io.BufferedOutputStream
import java.io.InputStreamReader
import java.net.Socket
import kotlin.math.abs

class MainActivity : AppCompatActivity() {
    private val SERVER_PORT = 3213
    private val SERVER_IP = "sankar-manoj.com"
    lateinit var socket : Socket
    lateinit var outputStream: BufferedOutputStream
    lateinit var inputStream: InputStreamReader
    var pumpstatus = -1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        var clientThread = ClientThread()
        Thread(clientThread).start()
        togglePumpButton.setOnClickListener {
            Thread(Runnable {
                val to_send_json = JSONObject()
                pumpstatus = abs(pumpstatus -1)
                to_send_json.put("control-pump-main-tank",pumpstatus)
                outputStream.write(to_send_json.toString().toByteArray())
                outputStream.flush()
            }).start()
            togglePumpButton.isEnabled = false
        }
        togglePumpButton.isEnabled = false

    }

    inner class ClientThread: Runnable
    {

        override fun run() {
            socket = Socket(SERVER_IP,SERVER_PORT)
            outputStream = BufferedOutputStream(socket.getOutputStream())
            inputStream = InputStreamReader(socket.getInputStream())
            while(true)
            {
                val input_data_str =  CharArray(1000)
                val read_Size = inputStream.read(input_data_str)

                val string_input_data = String(input_data_str).subSequence(0,read_Size).toString()
                Log.i("FromServerSize",read_Size.toString())
                Log.i("FromServer",string_input_data + string_input_data.length.toString())

                val input_data = JSONObject(string_input_data)
                if(input_data.has("control-pump-main-tank")) {
                    if (input_data["control-pump-main-tank"] == 0) {
                        runOnUiThread {
                            togglePumpButton.isEnabled = true
                            pumpStatusValueTextView.setText("OFF")
                            pumpStatusValueTextView.setTextColor(Color.RED)
                            togglePumpButton.setText("Turn Pump ON")
                            pumpstatus = 0
                        }
                    } else if (input_data["control-pump-main-tank"] == 1) {
                        runOnUiThread {
                            togglePumpButton.isEnabled = true
                            pumpStatusValueTextView.setText("ON")
                            pumpStatusValueTextView.setTextColor(Color.GREEN)
                            togglePumpButton.setText("Turn Pump OFF")
                            pumpstatus = 1
                        }
                    }
                }
                if(input_data.has("sensor-water-level-buffer-tank-1"))
                {
                    runOnUiThread {
                        tank1LevelValueTextView.setText(input_data.get("sensor-water-level-buffer-tank-1").toString())
                    }
                }
                if(input_data.has("sensor-water-level-buffer-tank-2"))
                {
                    runOnUiThread {
                        tank2LevelValueTextView.setText(input_data.get("sensor-water-level-buffer-tank-2").toString())
                    }
                }

            }

        }
    }
}
