package com.example.bouncy

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.bouncy.GameView

class GameActivity : AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(GameView(this))
    }

}