package jp.krohigewagma.tonegenerator

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.SeekBar
import android.widget.Spinner

class MainActivity : AppCompatActivity(){

    private val toneGenerator = ToneController(22050, 1, 8)

    var level : Int = 0

    @SuppressLint("ClickableViewAccessibility")
    private val btnTouchListener = View.OnTouchListener { v, event ->
        var osc = findViewById<Spinner>(R.id.oscSpinner).selectedItemPosition
        when(event.action){
            MotionEvent.ACTION_DOWN ->{
                when(v.id){
                    R.id.btnC1 -> {
                        toneGenerator.toneOn(Tone.C1, level, osc)
                    }
                    R.id.btnD1 -> {
                        toneGenerator.toneOn(Tone.D1, level, osc)
                    }
                    R.id.btnE1 -> {
                        toneGenerator.toneOn(Tone.E1, level, osc)
                    }
                    R.id.btnF1 -> {
                        toneGenerator.toneOn(Tone.F1, level, osc)
                    }
                    R.id.btnG1 -> {
                        toneGenerator.toneOn(Tone.G1, level, osc)
                    }


                    R.id.btnC2 -> {
                        toneGenerator.toneOn(Tone.C2, level, osc)
                    }
                    R.id.btnD2 -> {
                        toneGenerator.toneOn(Tone.D2, level, osc)
                    }
                    R.id.btnE2 -> {
                        toneGenerator.toneOn(Tone.E2, level, osc)
                    }
                    R.id.btnF2 -> {
                        toneGenerator.toneOn(Tone.F2, level, osc)
                    }
                    R.id.btnG2 -> {
                        toneGenerator.toneOn(Tone.G2, level, osc)
                    }


                    R.id.btnC3 -> {
                        toneGenerator.toneOn(Tone.C3, level, osc)
                    }
                    R.id.btnD3 -> {
                        toneGenerator.toneOn(Tone.D3, level, osc)
                    }
                    R.id.btnE3 -> {
                        toneGenerator.toneOn(Tone.E3, level, osc)
                    }
                    R.id.btnF3 -> {
                        toneGenerator.toneOn(Tone.F3, level, osc)
                    }
                    R.id.btnG3 -> {
                        toneGenerator.toneOn(Tone.G3, level, osc)
                    }


                    R.id.btnC4 -> {
                        toneGenerator.toneOn(Tone.C4, level, osc)
                    }
                    R.id.btnD4 -> {
                        toneGenerator.toneOn(Tone.D4, level, osc)
                    }
                    R.id.btnE4 -> {
                        toneGenerator.toneOn(Tone.E4, level, osc)
                    }
                    R.id.btnF4 -> {
                        toneGenerator.toneOn(Tone.F4, level, osc)
                    }
                    R.id.btnG4 -> {
                        toneGenerator.toneOn(Tone.G4, level, osc)
                    }

                    R.id.btnCodeC -> {
                        toneGenerator.toneOn2(arrayListOf<ToneGenerator>(ToneGenerator(Tone.C3, level, osc), ToneGenerator(Tone.E3, level, osc), ToneGenerator(Tone.G3, level, osc)))
                    }
                    R.id.btnCodeD -> {
                        toneGenerator.toneOn2(arrayListOf<ToneGenerator>(ToneGenerator(Tone.D3, level, osc), ToneGenerator(Tone.F3s, level, osc), ToneGenerator(Tone.A3, level, osc)))
                    }
                    R.id.btnCodeE -> {
                        toneGenerator.toneOn2(arrayListOf<ToneGenerator>(ToneGenerator(Tone.E3, level, osc), ToneGenerator(Tone.G3s, level, osc), ToneGenerator(Tone.B3, level, osc)))
                    }
                    R.id.btnCodeF -> {
                        toneGenerator.toneOn2(arrayListOf<ToneGenerator>(ToneGenerator(Tone.F3, level, osc), ToneGenerator(Tone.A3, level, osc), ToneGenerator(Tone.C4, level, osc)))
                    }
                    R.id.btnCodeG -> {
                        toneGenerator.toneOn2(arrayListOf<ToneGenerator>(ToneGenerator(Tone.G3, level, osc), ToneGenerator(Tone.B3, level, osc), ToneGenerator(Tone.D4, level, osc)))
                    }
                }
            }
            MotionEvent.ACTION_MOVE->{
            }
            MotionEvent.ACTION_UP->{
                toneGenerator.toneOff()
            }
            MotionEvent.ACTION_CANCEL->{
                toneGenerator.toneOff()
            }
            MotionEvent.ACTION_OUTSIDE->{
                toneGenerator.toneOff()
            }
        }
        true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        var adapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item)
        adapter.add("sin")
        adapter.add("Square 50%")
        adapter.add("Square 25%")
        findViewById<Spinner>(R.id.oscSpinner).adapter = adapter

        findViewById<SeekBar>(R.id.levelBar).setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                level = progress
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })

        var btnC1 = findViewById<Button>(R.id.btnC1)
        var btnD1 = findViewById<Button>(R.id.btnD1)
        var btnE1 = findViewById<Button>(R.id.btnE1)
        var btnF1 = findViewById<Button>(R.id.btnF1)
        var btnG1 = findViewById<Button>(R.id.btnG1)

        var btnC2 = findViewById<Button>(R.id.btnC2)
        var btnD2 = findViewById<Button>(R.id.btnD2)
        var btnE2 = findViewById<Button>(R.id.btnE2)
        var btnF2 = findViewById<Button>(R.id.btnF2)
        var btnG2 = findViewById<Button>(R.id.btnG2)

        var btnC3 = findViewById<Button>(R.id.btnC3)
        var btnD3 = findViewById<Button>(R.id.btnD3)
        var btnE3 = findViewById<Button>(R.id.btnE3)
        var btnF3 = findViewById<Button>(R.id.btnF3)
        var btnG3 = findViewById<Button>(R.id.btnG3)

        var btnC4 = findViewById<Button>(R.id.btnC4)
        var btnD4 = findViewById<Button>(R.id.btnD4)
        var btnE4 = findViewById<Button>(R.id.btnE4)
        var btnF4 = findViewById<Button>(R.id.btnF4)
        var btnG4 = findViewById<Button>(R.id.btnG4)

        var btnCodeC = findViewById<Button>(R.id.btnCodeC)
        var btnCodeD = findViewById<Button>(R.id.btnCodeD)
        var btnCodeE = findViewById<Button>(R.id.btnCodeE)
        var btnCodeF = findViewById<Button>(R.id.btnCodeF)
        var btnCodeG = findViewById<Button>(R.id.btnCodeG)

        btnC1.setOnTouchListener(btnTouchListener)
        btnD1.setOnTouchListener(btnTouchListener)
        btnE1.setOnTouchListener(btnTouchListener)
        btnF1.setOnTouchListener(btnTouchListener)
        btnG1.setOnTouchListener(btnTouchListener)

        btnC2.setOnTouchListener(btnTouchListener)
        btnD2.setOnTouchListener(btnTouchListener)
        btnE2.setOnTouchListener(btnTouchListener)
        btnF2.setOnTouchListener(btnTouchListener)
        btnG2.setOnTouchListener(btnTouchListener)

        btnC3.setOnTouchListener(btnTouchListener)
        btnD3.setOnTouchListener(btnTouchListener)
        btnE3.setOnTouchListener(btnTouchListener)
        btnF3.setOnTouchListener(btnTouchListener)
        btnG3.setOnTouchListener(btnTouchListener)


        btnC4.setOnTouchListener(btnTouchListener)
        btnD4.setOnTouchListener(btnTouchListener)
        btnE4.setOnTouchListener(btnTouchListener)
        btnF4.setOnTouchListener(btnTouchListener)
        btnG4.setOnTouchListener(btnTouchListener)

        btnCodeC.setOnTouchListener(btnTouchListener)
        btnCodeD.setOnTouchListener(btnTouchListener)
        btnCodeE.setOnTouchListener(btnTouchListener)
        btnCodeF.setOnTouchListener(btnTouchListener)
        btnCodeG.setOnTouchListener(btnTouchListener)
    }

}