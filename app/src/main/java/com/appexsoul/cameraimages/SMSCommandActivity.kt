package com.appexsoul.cameraimages

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import org.json.JSONArray
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.appexsoul.cameraimages.databinding.ActivitySmsCommandBinding

class SMSCommandActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySmsCommandBinding
    private lateinit var commands: List<SMSCommand>
    private val REQUEST_SMS_PERMISSION = 200

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySmsCommandBinding.inflate(layoutInflater)
        setContentView(binding.root)

        commands = loadSMSCommands()
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, commands.map { it.name })
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.commandSpinner.adapter = adapter

        binding.sendButton.setOnClickListener {
            val phone = binding.phoneEditText.text.toString()
            val command = commands[binding.commandSpinner.selectedItemPosition]
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {
                SMSHelper.sendCommand(this, phone, command.code)
                Toast.makeText(this, "Command sent", Toast.LENGTH_SHORT).show()
            } else {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.SEND_SMS), REQUEST_SMS_PERMISSION)
            }
        }
    }

    private fun loadSMSCommands(): List<SMSCommand> {
        val inputStream = resources.openRawResource(R.raw.sms_commands)
        val json = inputStream.bufferedReader().use { it.readText() }
        val array = JSONArray(json)
        val result = mutableListOf<SMSCommand>()
        for (i in 0 until array.length()) {
            val obj = array.getJSONObject(i)
            result.add(SMSCommand(obj.getString("name"), obj.getString("code")))
        }
        return result
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_SMS_PERMISSION && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            binding.sendButton.performClick()
        } else if (requestCode == REQUEST_SMS_PERMISSION) {
            Toast.makeText(this, "SMS permission denied", Toast.LENGTH_SHORT).show()
        }
    }
}
