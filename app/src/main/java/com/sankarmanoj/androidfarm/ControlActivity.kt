package com.sankarmanoj.androidfarm

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_control.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_control.*
import org.json.JSONObject
import kotlin.math.abs
import android.graphics.Color
import android.preference.Preference
import android.view.View

class ControlActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener,ToServer.onServerMessageListener {

    var pumpstatus = -1
    var gate1status = -1
    var gate2status = -1
    var blowerstatus = -1
    override fun onServerMessage(input_data: JSONObject) {
        if (input_data.has("control-pump-main-tank")) {
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
        if(input_data.has("alert-message"))
        {
            var sharedprefs = getSharedPreferences("AndroidFarmPreferences", Context.MODE_PRIVATE)
            sharedprefs.edit().putString("alert-message",input_data.getString("alert-message")).commit()

        }
        if (input_data.has("control-blower")) {
            if (input_data["control-blower"] == 0) {
                runOnUiThread {
                    toggleBlowerButton.isEnabled = true
                    blowerStatusValueTextView.setText("OFF")
                    blowerStatusValueTextView.setTextColor(Color.RED)
                    toggleBlowerButton.setText("Turn Blower ON")
                    blowerstatus = 0
                }
            } else if (input_data["control-blower"] == 1) {
                runOnUiThread {
                    toggleBlowerButton.isEnabled = true
                    blowerStatusValueTextView.setText("ON")
                    blowerStatusValueTextView.setTextColor(Color.GREEN)
                    toggleBlowerButton.setText("Turn Blower OFF")
                    blowerstatus = 1
                }
            }
        }
            if (input_data.has("control-valve-raft-tank-1")) {
                if (input_data["control-valve-raft-tank-1"] == 0) {
                    runOnUiThread {
                        toggleGate1Button.isEnabled = true
                        gate1StatusValueTextView.setText("Open")
                        gate1StatusValueTextView.setTextColor(Color.BLUE)
                        toggleGate1Button.setText("Close Gate Valve 1")
                        gate1status = 0
                    }
                } else if (input_data["control-valve-raft-tank-1"] == 1) {
                    runOnUiThread {
                        toggleGate1Button.isEnabled = true
                        gate1StatusValueTextView.setText("Closed")
                        gate1StatusValueTextView.setTextColor(Color.MAGENTA)
                        toggleGate1Button.setText("Open Gate Valve 1")
                        gate1status = 1
                    }
                }
            }

            if (input_data.has("control-valve-raft-tank-2")) {
                if (input_data["control-valve-raft-tank-2"] == 0) {
                    runOnUiThread {
                        toggleGate2Button.isEnabled = true
                        gate2StatusValueTextView.setText("Open")
                        gate2StatusValueTextView.setTextColor(Color.BLUE)
                        toggleGate2Button.setText("Close Gate Valve 2")
                        gate2status = 0
                    }
                } else if (input_data["control-valve-raft-tank-2"] == 1) {
                    runOnUiThread {
                        toggleGate2Button.isEnabled = true
                        gate2StatusValueTextView.setText("Closed")
                        gate2StatusValueTextView.setTextColor(Color.MAGENTA)
                        toggleGate2Button.setText("Open Gate Valve 2")
                        gate2status = 1
                    }
                }
            }

            if (input_data.has("sensor-water-level-buffer-tank-1")) {
                runOnUiThread {
                    tank1LevelValueTextView.setText(input_data.get("sensor-water-level-buffer-tank-1").toString())
                }
            }
            if (input_data.has("sensor-water-level-buffer-tank-2")) {
                runOnUiThread {
                    tank2LevelValueTextView.setText(input_data.get("sensor-water-level-buffer-tank-2").toString())
                }
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_control)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }

        val toggle = ActionBarDrawerToggle(
            this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)

        val toserver = ToServer.getToServer()

        toserver.addOnServerMessageListener(this)

        togglePumpButton.setOnClickListener {
            Thread(Runnable {
                val to_send_json = JSONObject()
                pumpstatus = abs(pumpstatus -1)
                to_send_json.put("control-pump-main-tank",pumpstatus)
                toserver.write(to_send_json.toString())
            }).start()
            togglePumpButton.isEnabled = false
            annoyingAlert()
        }
        togglePumpButton.isEnabled = false


        toggleBlowerButton.setOnClickListener {
            Thread(Runnable {
                val to_send_json = JSONObject()
                blowerstatus = abs(blowerstatus -1)
                to_send_json.put("control-blower",blowerstatus)
                toserver.write(to_send_json.toString())
            }).start()
            toggleBlowerButton.isEnabled = false
            annoyingAlert()
        }
        toggleBlowerButton.isEnabled = false


        toggleGate1Button.setOnClickListener {
            Thread(Runnable {
                val to_send_json = JSONObject()
                gate1status = abs(gate1status -1)
                to_send_json.put("control-valve-raft-tank-1",gate1status)
                toserver.write(to_send_json.toString())
            }).start()
            toggleGate1Button.isEnabled = false
            annoyingAlert()
        }
        toggleGate1Button.isEnabled = false

        toggleGate2Button.setOnClickListener {
            Thread(Runnable {
                val to_send_json = JSONObject()
                gate2status = abs(gate2status -1)
                to_send_json.put("control-valve-raft-tank-2",gate2status)
                toserver.write(to_send_json.toString())
            }).start()
            toggleGate2Button.isEnabled = false
            annoyingAlert()
        }
        toggleGate2Button.isEnabled = false
        annoyingAlert()
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.control, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            R.id.action_settings -> return true
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_camera -> {

            }
            R.id.nav_gallery -> {

            }
            R.id.nav_slideshow -> {

            }
            R.id.nav_manage -> {

            }
            R.id.nav_share -> {

            }
            R.id.nav_send -> {

            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    fun annoyingAlert()
    {
        var sharedprefs = getSharedPreferences("AndroidFarmPreferences", Context.MODE_PRIVATE)
        var alertMessage =  sharedprefs.getString("alert-message","")
        if( alertMessage.isNotEmpty())
        {
            val alertDialogBuilder = AlertDialog.Builder(this)
            alertDialogBuilder.setMessage(alertMessage)
            alertDialogBuilder.create().show()
        }
    }
}
